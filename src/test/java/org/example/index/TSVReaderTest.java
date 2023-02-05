package org.example.index;

import org.example.document.Document;
import org.example.document.DocumentConfig;
import org.example.document.DocumentField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class TSVReaderTest {

    private final Reader reader = new TSVReader(
            new DocumentConfig(),
            "\t",
            new SegmentWriter(
                    "./",
                    new TokenNormalizer()
            )
    );
    private final String path = "src/main/resources/title.basics.tsv";
    @Test
    void testTsvReadTest() throws IOException {
        List<String> inputData = reader.read(path, 10);
        inputData
                .forEach(System.out::println);
        Assertions.assertEquals(inputData.size(), 10);
    }

    @Test
    void testStreamZipTest() throws Exception {
        //when

        String[] tokens = new String[]{"tt10431818", "tvEpisode", "Uterus Independence Day", "Uterus Independence Day", "0", "2019", "\\N", "\\N", "Comedy"};
        Document document = new Document();
        DocumentConfig documentConfig = new DocumentConfig();
        List<DocumentField> documentFields = new ArrayList<>();

        //given
        documentFields.add(new DocumentField("id", "text"));
        documentFields.add(new DocumentField("type", "text"));
        documentFields.add(new DocumentField("primaryTitle", "text"));
        documentFields.add(new DocumentField("secondaryTitle", "text"));
        documentFields.add(new DocumentField("?", "text"));
        documentFields.add(new DocumentField("year", "text"));
        documentFields.add(new DocumentField("?", "text"));
        documentFields.add(new DocumentField("p", "text"));
        documentFields.add(new DocumentField("category", "text"));
        documentConfig.setDocumentFieldList(documentFields);
        document.setFieldByConfig(documentConfig);

        //then
        document.setToken(tokens);
        HashMap<DocumentField, String> fieldsMap = document.getFieldsMap();
        System.out.println(fieldsMap.size());
        fieldsMap.keySet().forEach(key -> {
            System.out.println("key : " + key.toString() + "value " + fieldsMap.get(key));
        });
    }

    @Test
    void testDocumentsReadTest() throws Exception {
        //when

        String[] tokens = new String[]{"tt10431818", "tvEpisode", "Uterus Independence Day", "Uterus Independence Day", "0", "2019", "\\N", "\\N", "Comedy"};
        String[] tokens2 = new String[]{"tt10431819", "tvEpisode", "Secondary Independence Day", "Secondary Independence Day", "0", "2019", "\\N", "\\N", "Comedy"};
        Document document = new Document();
        DocumentConfig documentConfig = new DocumentConfig();
        List<DocumentField> documentFields = new ArrayList<>();

        //given
        documentFields.add(new DocumentField("id", "text"));
        documentFields.add(new DocumentField("type", "text"));
        documentFields.add(new DocumentField("primaryTitle", "text"));
        documentFields.add(new DocumentField("secondaryTitle", "text"));
        documentFields.add(new DocumentField("?", "text"));
        documentFields.add(new DocumentField("year", "text"));
        documentFields.add(new DocumentField("?", "text"));
        documentFields.add(new DocumentField("p", "text"));
        documentFields.add(new DocumentField("category", "text"));
        documentConfig.setDocumentFieldList(documentFields);
        document.setFieldByConfig(documentConfig);

        //then
        List<Document> collections = new ArrayList<>();
        document.setToken(tokens);
        HashMap<DocumentField, String> fieldsMap = document.getFieldsMap();
        System.out.println(fieldsMap.size());
        fieldsMap.keySet().forEach(key -> {
            System.out.println("key : " + key.toString() + "value " + fieldsMap.get(key));
        });
        collections.add(document);

        document = new Document();
        document.setFieldByConfig(documentConfig);
        document.setToken(tokens2);
        HashMap<DocumentField, String> fieldsMap2 = document.getFieldsMap();
        System.out.println(fieldsMap2.size());
        fieldsMap2.keySet().forEach(key -> {
            System.out.println("key : " + key.toString() + "value " + fieldsMap.get(key));
        });
        collections.add(document);

        collections
                .forEach(collection ->
                        System.out.println(collection.getFieldsMap())
                );
    }

    @Test
    @DisplayName("TSVReader 클래스 안에 있는 bufferedReader 소요시간 테스트")
    void brTestInTSVReader() {
        long startTime = System.currentTimeMillis();

        long endTime = System.currentTimeMillis();

    }
}