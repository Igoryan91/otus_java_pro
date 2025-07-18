package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final EntityClassMetaData<?> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        String tableName = entityClassMetaData.getName().toLowerCase();
        List<Field> fields = entityClassMetaData.getAllFields();
        String columns = fields.stream().map(Field::getName).collect(Collectors.joining(", "));
        return String.format("SELECT %s FROM %s", columns, tableName);
    }

    @Override
    public String getSelectByIdSql() {
        String tableName = entityClassMetaData.getName().toLowerCase();
        String idColumnName = entityClassMetaData.getIdField().getName();
        List<Field> fields = entityClassMetaData.getAllFields();
        String columns = fields.stream().map(Field::getName).collect(Collectors.joining(", "));
        return String.format("SELECT %s FROM %s WHERE %s = ?", columns, tableName, idColumnName);
    }

    @Override
    public String getInsertSql() {
        String tableName = entityClassMetaData.getName().toLowerCase();
        List<Field> fields = entityClassMetaData.getFieldsWithoutId();
        String columns = fields.stream().map(Field::getName).collect(Collectors.joining(", "));
        String values = fields.stream().map(field -> "?").collect(Collectors.joining(", "));
        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
    }

    @Override
    public String getUpdateSql() {
        String tableName = entityClassMetaData.getName().toLowerCase();
        List<Field> fields = entityClassMetaData.getFieldsWithoutId();
        String columns = fields.stream()
                .map(field -> String.format("%s = ?", field.getName()))
                .collect(Collectors.joining(", "));
        return String.format(
                "UPDATE %s SET %s WHERE %s = ?",
                tableName, columns, entityClassMetaData.getIdField().getName());
    }
}
