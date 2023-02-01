package org.example.document;

import java.util.ArrayList;
import java.util.List;

public class DocumentConfig {

    private final List<DocumentField> documentFieldList = new ArrayList<>();

    public List<DocumentField> getDocumentFieldList() {
        return documentFieldList;
    }

    public void setDocumentFieldList(List<DocumentField> documentFields) {
        System.out.println(documentFields.size());
        documentFieldList.addAll(documentFields);
    }
}
