package org.example.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.Streams.forEachPair;

public class Document {

    private final int documentId = DocumentIdGenerator.generateIdSeq();
    private List<DocumentField> fields = new ArrayList<DocumentField>();

    private List<DocumentField> fieldConfig = new ArrayList<>();
    private HashMap<DocumentField, String> fieldsMap = new HashMap<>();
    public Document(){}


    public void setField(String fieldName, String fieldType) {
        fields.add(new DocumentField(fieldName, fieldType));
    }

    public void setFieldByConfig(DocumentConfig documentConfig){
        fieldConfig.addAll(documentConfig.getDocumentFieldList());
    }

    /**
     * map field 에 config 객체 통해 변수 설정
     * @param documentConfig
     */
    public void setFieldMapByConfig(DocumentConfig documentConfig) {
        fieldConfig.addAll(documentConfig.getDocumentFieldList());
    }

    /**
     * set tokens to fields method
     * @param tokens token list
     */
    @SuppressWarnings("Beta")
    public void setToken(String[] tokens) {
        System.out.println(Arrays.toString(tokens));
        forEachPair(fieldConfig.stream(), Arrays.stream(tokens), fieldsMap::put);
    }

    public HashMap<DocumentField, String> getFieldsMap() {
        return fieldsMap;
    }

    public int getDocumentId() {
        return documentId;
    }

}