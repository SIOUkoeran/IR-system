package org.example.index;

import org.example.document.Document;

import java.io.*;
import java.util.List;

public class DocumentWriter {
    private final String path;

    private final BufferedWriter bw;
    public DocumentWriter(String path) {
        this.path = path + "/index/";
        mkdir();
        try {
            bw = new BufferedWriter(new FileWriter(this.path + "collections"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeDocuments(List<Document> documents) {
        for (Document document : documents) {
            try {
                bw.write(document.writeDocument() + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void mkdir() {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
}
