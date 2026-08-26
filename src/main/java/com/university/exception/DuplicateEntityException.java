package com.university.exception;

public class DuplicateEntityException extends RuntimeException {

    private final String entityName;
    private final String fieldName;
    private final Object fieldValue;

    public DuplicateEntityException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s with %s '%s' already exists", entityName, fieldName, fieldValue));
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
