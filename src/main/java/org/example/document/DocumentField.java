package org.example.document;

import java.util.Objects;

public class DocumentField {

    private final String fieldName;
    private final String fieldType;

    public DocumentField(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }
    public String getFieldType() {
        return fieldType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fieldName);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
