package ru.otus.jdbc.repository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;

/** Сохраняет объект в базу, читает объект из базы */
@SuppressWarnings("java:S3011")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        String sql = entitySQLMetaData.getSelectByIdSql();
        return dbExecutor.executeSelect(connection, sql, List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return mapRowToEntity(rs);
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        String sql = entitySQLMetaData.getSelectAllSql();
        return dbExecutor
                .executeSelect(connection, sql, List.of(), rs -> {
                    try {
                        var entityList = new ArrayList<T>();
                        while (rs.next()) {
                            entityList.add(mapRowToEntity(rs));
                        }
                        return entityList;
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T entity) {
        String sql = entitySQLMetaData.getInsertSql();
        var params = getFieldValuesFromEntity(entity);
        try {
            return dbExecutor.executeStatement(connection, sql, params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        String sql = entitySQLMetaData.getUpdateSql();
        var params = getFieldValuesFromEntity(entity);
        params.add(getIdValueFromEntity(entity));
        try {
            dbExecutor.executeStatement(connection, sql, params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private T mapRowToEntity(ResultSet rs) {
        try {
            T entity = entityClassMetaData.getConstructor().newInstance();
            var fields = entityClassMetaData.getAllFields();
            for (var field : fields) {
                setFieldFromResultSet(entity, field, rs);
            }
            return entity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DataTemplateException(e);
        }
    }

    private void setFieldFromResultSet(T entity, Field field, ResultSet rs) {
        try {
            String columnName = field.getName();
            Class<?> fieldType = field.getType();
            Object value = getValueFromResultSet(rs, columnName, fieldType);
            field.set(entity, value);
        } catch (SQLException | IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }

    private Object getValueFromResultSet(ResultSet rs, String columnName, Class<?> fieldType) throws SQLException {
        return switch (fieldType.getSimpleName()) {
            case "long", "Long" -> rs.getLong(columnName);
            case "int", "Integer" -> rs.getInt(columnName);
            case "String" -> rs.getString(columnName);
            case "boolean", "Boolean" -> rs.getBoolean(columnName);
            default -> throw new IllegalArgumentException("Нераспознанный тип поля: " + fieldType);
        };
    }

    private List<Object> getFieldValuesFromEntity(T entity) {
        try {
            List<Object> fieldValues = new ArrayList<>();
            for (Field field : entityClassMetaData.getFieldsWithoutId()) {
                fieldValues.add(field.get(entity));
            }
            return fieldValues;
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }

    private Object getIdValueFromEntity(T entity) {
        try {
            Field idField = entityClassMetaData.getIdField();
            return idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }
}
