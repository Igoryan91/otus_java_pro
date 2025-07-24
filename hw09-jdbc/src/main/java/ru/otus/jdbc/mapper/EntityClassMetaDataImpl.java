package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.otus.jdbc.annotation.Id;

@SuppressWarnings("java:S3011")
public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> entityClass;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.constructor = initConstructor();

        Field[] allFields = entityClass.getDeclaredFields();
        makeFieldsAccessible(allFields);

        this.idField = initIdField(allFields);
        this.fieldsWithoutId = initFields(allFields);
    }

    private void makeFieldsAccessible(Field[] fields) {
        Arrays.stream(fields).forEach(field -> field.setAccessible(true));
    }

    private Constructor<T> initConstructor() {
        try {
            return entityClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Нет публичного конструктора без параметров для класса " + entityClass.getName(), e);
        }
    }

    private List<Field> initFields(Field[] fields) {
        return Arrays.stream(fields)
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .toList();
    }

    private Field initIdField(Field[] fields) {
        List<Field> idFields = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .toList();
        if (idFields.isEmpty()) {
            throw new IllegalStateException("В классе " + entityClass.getName() + " нет поля с аннотацией @Id");
        }
        if (idFields.size() > 1) {
            throw new IllegalStateException("В классе " + entityClass.getName() + " несколько полей с аннотацией @Id");
        }
        return idFields.getFirst();
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
