package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import ru.otus.jdbc.annotation.Id;

@SuppressWarnings("java:S3011")
public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> entityClass;
    private Constructor<T> constructor;
    private Field idField;
    private List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
        initConstructor();
        initFields();
    }

    private void initConstructor() {
        try {
            this.constructor = entityClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Нет публичного конструктора без параметров для класса " + entityClass.getName(), e);
        }
    }

    private void initFields() {
        var declaredFields = entityClass.getDeclaredFields();
        var forFieldsWithoutId = new ArrayList<Field>(declaredFields.length - 1);
        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
                initIdField(field);
            } else {
                forFieldsWithoutId.add(field);
            }
        }
        checkIdField();
        this.fieldsWithoutId = List.copyOf(forFieldsWithoutId);
    }

    private void initIdField(Field field) {
        if (this.idField != null) {
            throw new IllegalStateException("В классе " + entityClass.getName() + " несколько полей с аннотацией @Id");
        }
        this.idField = field;
    }

    private void checkIdField() {
        if (this.idField == null) {
            throw new IllegalStateException("В классе " + entityClass.getName() + " не найдено поле с аннотацией @Id");
        }
    }

    @Override
    public String getName() {
        return entityClass.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        List<Field> all = new ArrayList<>(fieldsWithoutId);
        all.addFirst(idField);
        return all;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return new ArrayList<>(fieldsWithoutId);
    }
}
