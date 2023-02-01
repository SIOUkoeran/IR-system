package org.example.document;

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
}
