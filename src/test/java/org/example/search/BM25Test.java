package org.example.search;

import org.example.document.Document;
import org.example.document.DocumentConfig;
import org.example.document.DocumentField;
import org.example.document.DocumentReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class BM25Test {

    Scorer scorer = new BM25(
            "./src/main/resources/segmentMerge/index/"
    );
    @Test
    void searchQueryTest() {
        List<Score> army = scorer.search("army");
        System.out.println(army);

        Assertions.assertEquals(army.get(0).getDocId(), 71);
    }

    @Test
    void searchQueryTest30000() {
        Scorer scorer1 =
                new BM25(
                        "./src/main/resources/segmentWriterTest/30000/index/"
                );

        List<Score> army = scorer1.search("author army");

        DocumentConfig documentConfig = makeDocumentConfig();
        DocumentReader documentReader = new DocumentReader(
                "./src/main/resources/segmentWriterTest/30000/",
                documentConfig
        );
        for (Score score : army) {
            Document document = documentReader.findDocument(String.valueOf(score.getDocId()));
            System.out.println(document.getDocumentId());
        }
        System.out.println(army);
    }

    private DocumentConfig makeDocumentConfig() {
        DocumentConfig documentConfig = new DocumentConfig();
        List<DocumentField> documentFieldList = new ArrayList<>();

        documentFieldList.add(new DocumentField("tconst", "text"));
        documentFieldList.add(new DocumentField("titleType", "text"));
        documentFieldList.add(new DocumentField("primaryTitle", "text"));
        documentFieldList.add(new DocumentField("originalTitle", "text"));
        documentFieldList.add(new DocumentField("isAdult", "text"));
        documentFieldList.add(new DocumentField("startYear", "text"));
        documentFieldList.add(new DocumentField("endYear", "text"));
        documentFieldList.add(new DocumentField("runtimeMinutes", "text"));
        documentFieldList.add(new DocumentField("genres", "text"));
        documentConfig.setDocumentFieldList(documentFieldList);
        return documentConfig;
    }

    @Test
    void searchQueryFullSize() {
        Scorer scorer =
                new BM25(
                        "./src/main/resources/fullSize/index/",
                        10
                );
        DocumentConfig documentConfig = makeDocumentConfig();
        DocumentReader documentReader = new DocumentReader(
                "./src/main/resources/fullSize/",
                documentConfig
        );
        long startTime = System.currentTimeMillis();
        List<Score> result = scorer.search("author army");
        for (Score score : result) {
            Document document = documentReader.findDocument(String.valueOf(score.getDocId()));
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}