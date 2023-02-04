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

    public TSVReader(DocumentConfig documentConfig, String delimiter, SegmentWriter segmentWriter) {
        this.documentConfig = documentConfig;
        this.delimiter = delimiter;
        this.segmentWriter = segmentWriter;
    }

    /**
     * read tsv method per size 10000
     * @param path data input file path
     * @param size memory buffer size
     */
    public List<String> read(String path, int size){
        if (size == 0){
            size = Integer.MAX_VALUE;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<String> array = new ArrayList<String>(size);
        List<Document> collections = new ArrayList<>(size);

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
                array.add(data);
                /**
                 * if collection size is upper user defined size,
                 * segmentWriter write segment.
                 * and clear memory
                 */
                if (collections.size() >= size) {
                    segmentWriter.writeSegment(collections, documentConfig);
                    collections.clear();
                    array.clear();
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
            segmentWriter.writeSegment(collections, documentConfig);
            collections.clear();
            array.clear();
        }
        return array;
    }

    public void addField(String fieldName) {

    }




}