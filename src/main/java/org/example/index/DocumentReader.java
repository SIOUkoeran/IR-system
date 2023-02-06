package org.example.index;

import org.example.document.Document;
import org.example.document.DocumentConfig;

import java.io.BufferedReader;
import java.io.FileReader;

public class DocumentReader {

    private final String path;

    private final DocumentConfig documentConfig;
    public DocumentReader(String path, DocumentConfig documentConfig) {
        this.path = path + "/index/";
        this.documentConfig = documentConfig;
    }

    public Document findDocument(String docId){
        try {
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(path + "collections"));
            while ((line = br.readLine()) != null) {
                if (line.startsWith(docId)) {
                    return new Document(line, documentConfig);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return new Document();
    }
}
