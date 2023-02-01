package org.example.document;

public class DocumentIdGenerator {
    private static final long offset = 0L;
    private static int idSeq = 0;


    private static void increaseIdSeq() {
        ++idSeq;
    }

    public static synchronized int generateIdSeq() {
        increaseIdSeq();
        return idSeq;
    }

}
