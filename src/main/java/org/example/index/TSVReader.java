package org.example.index;


import org.example.document.Document;
import org.example.document.DocumentConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * .tsv extension file reader class
 */
public class TSVReader implements Serializable, Reader{

    /**
     * document 환경변수
     */
    private final DocumentConfig documentConfig;
    /**
     * tsv delimiter 변수
     */
    private final String delimiter;

    /**
     * segment writer
     */
    private final SegmentWriter segmentWriter;

    private final DocumentWriter documentWriter;

    public TSVReader(DocumentConfig documentConfig,
                     String delimiter,
                     SegmentWriter segmentWriter,
                     DocumentWriter documentWriter) {
        this.documentConfig = documentConfig;
        this.delimiter = delimiter;
        this.segmentWriter = segmentWriter;
        this.documentWriter = documentWriter;
    }

    /**
     * 데이터를 size 인수만큼 끊어서 읽은 뒤, 디스크에 block 을 생성한다.
     * 지정한 데이터를 끝까지 다 읽은 뒤, 해당 block 들을 병합한다.
     * @param path data input file path
     * @param size memory buffer size
     */
    public void read(String path, int size){
        if (size == 0){
            size = Integer.MAX_VALUE;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        List<Document> collections = new ArrayList<>(size * 2);

        Document document;
        String data;
        boolean isColumnLine = true;
        try {
            while ((data = br.readLine()) != null) {
                if (isColumnLine){
                    isColumnLine = false;
                    continue;
                }
                String[] tokens = data.split(delimiter);
                document = new Document();
                document.setFieldByConfig(documentConfig);
                document.setToken(tokens);
                collections.add(document);
                /**
                 * if collection size is upper user defined size,
                 * segmentWriter write segment.
                 * and clear memory
                 */
                if (collections.size() >= size) {
                    System.out.println("read done");
                    segmentWriter.writeSegment(collections, documentConfig);
                    documentWriter.writeDocuments(collections);
                    collections.clear();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (collections.size() > 0){
            System.out.println("read done");
            segmentWriter.writeSegment(collections, documentConfig);
            collections.clear();
        }
        segmentWriter.mergeBlocks();
    }

    public void addField(String fieldName) {

    }




}