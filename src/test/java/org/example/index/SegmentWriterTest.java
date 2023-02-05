package org.example.index;

import org.example.document.DocumentConfig;
import org.example.document.DocumentField;
import org.example.document.DocumentIdGenerator;
import org.example.posting.Posting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class SegmentWriterTest {

    @Test
    @DisplayName("title 역색인 용어 Jerry 라인 확인")
    void writeSegmentTest() throws IOException {

        //when
        String outputPath = "src/main/resources/segmentWriterTest/";
        String inputPath = "src/main/resources/segment_test_line1.tsv";

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

        //given
        TSVReader tsvReader = new TSVReader(
                documentConfig,
                "\t",
                new SegmentWriter(outputPath, new TokenNormalizer())
        );
        tsvReader.read(inputPath, 3);

        //then
        BufferedReader titleBr = new BufferedReader(new FileReader(outputPath + "primaryTitle0"));

        String[] lineArray = titleBr.readLine().split(" ");
        System.out.println(Arrays.toString(lineArray));
        Assertions.assertArrayEquals(lineArray, new String[]{"jerry", "5", "1", "[1]", "2", "[1,2,3,4]", "17"});
    }

    @Test
    void writePostingTest() throws IOException {
        String outputPath = "src/main/resources/segmentWriterTest/";
        String inputPath = "src/main/resources/";
        List<Posting> postingList = new ArrayList<Posting>() {
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                this.forEach(posting -> {
                    sb.append(posting).append(" ");
                });
                return sb.toString();
            }
        };
        Posting posting = new Posting(DocumentIdGenerator.generateIdSeq(), 1, 2, 3);
        Posting posting2 = new Posting(DocumentIdGenerator.generateIdSeq(), 1, 2, 3, 4);
        Posting posting3 = new Posting(DocumentIdGenerator.generateIdSeq(), 1, 2, 3, 4, 5);
        postingList.add(posting);
        postingList.add(posting2);
        postingList.add(posting3);

        File file = new File(outputPath + "writePostingTestResult.txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(postingList.toString());
            bw.flush();
            bw.close();
        }catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    @Test
    void size100SegmentWriteTest() throws IOException {
        String outputPath = "src/main/resources/segmentWriterTest/100/";
        String inputPath = "src/main/resources/title_test.tsv";

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

        TSVReader tsvReader = new TSVReader(
                documentConfig,
                "\t",
                new SegmentWriter(outputPath, new TokenNormalizer())
        );
        tsvReader.read(inputPath, 1000);

        //then
        BufferedReader titleBr = new BufferedReader(new FileReader(outputPath + "primaryTitle0"));

        String[] lineArray = titleBr.readLine().split(" ");
    }

    @ParameterizedTest
    @DisplayName("사이즈 별 원본 파일 역색인 테스트")
    @ValueSource(ints = {10000})
    void fullSizeSegmentWriteTest(int number) throws IOException {
        String outputPath = "src/main/resources/segmentWriterTest/30000/";
        String inputPath = "src/main/resources/segment30000.tsv";

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

        TSVReader tsvReader = new TSVReader(
                documentConfig,
                "\t",
                new SegmentWriter(outputPath, new TokenNormalizer())
        );
        long startTime = System.currentTimeMillis();
        tsvReader.read(inputPath, number);
        long endTime = System.currentTimeMillis();
        System.out.println("spend time " + (endTime - startTime));
        //then
        BufferedReader titleBr = new BufferedReader(new FileReader(outputPath + "primaryTitle0"));

        String[] lineArray = titleBr.readLine().split(" ");
    }
}