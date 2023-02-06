package org.example.document;

import com.google.common.collect.Streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Document {

    private int documentId = DocumentIdGenerator.generateIdSeq();

    private List<DocumentField> fieldConfig = new ArrayList<>();
    private HashMap<DocumentField, String> fieldsMap = new HashMap<>();
    public Document(){}

    public Document(String line, DocumentConfig documentConfig) {
        String[] strings = line.split("\t");
        this.documentId = Integer.parseInt(strings[0]);
        System.out.println(Arrays.toString(strings));
        fieldConfig.addAll(documentConfig.getDocumentFieldList());
        for (int idx = 0; idx < fieldConfig.size(); idx++) {
            fieldsMap.put(fieldConfig.get(idx), strings[idx + 1]);
        }
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
        fieldsMap.put(fieldConfig.get(0), tokens[0]);
        Streams.forEachPair(fieldConfig.stream(), Arrays.stream(tokens), fieldsMap::put);
    }

    public HashMap<DocumentField, String> getFieldsMap() {
        return fieldsMap;
    }

    public int getDocumentId() {
        return documentId;
    }

    public String writeDocument() {
        StringBuilder sb = new StringBuilder();
        sb.append(documentId).append("\t");
        fieldsMap.values()
                .forEach(s -> sb.append(s).append("\t"));
        return sb.toString();
    }
    @Override
    public String toString() {
        return fieldsMap.toString();
    }
}
