package org.example.index;

public class IndexWriter {


    /**
     * file 확장자 별 reader 주입
     */
    private final Reader reader;

    /**
     * file 이 존재하는 경로
     */
    private final String path;

    public IndexWriter(Reader reader, String path) {
        this.reader = reader;
        this.path = path;
    }


}
