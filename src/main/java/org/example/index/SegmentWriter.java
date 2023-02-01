package org.example.index;

import org.example.document.Document;
import org.example.document.DocumentField;
import org.example.posting.Posting;
import org.example.term.Term;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * segment writer
 */
public class SegmentWriter {

    private final String outputPath;
    private int segmentCount = 0;
    public SegmentWriter(String outputPath) {
        this.outputPath = outputPath;
    }

    /**
     * write mini index to disk.
     * @param collections to write Collection in disk.
     */
    public void writeSegment(List<Document> collections){
        TreeMap<Term, ArrayList<Posting>>[] zoneIndexes = makeInvertedPostingList(collections);
        for (TreeMap<Term, ArrayList<Posting>> zoneIndex : zoneIndexes){
            writeFile(outputPath, zoneIndex);
        }
        ++segmentCount;
    }

    /**
     * 파라미터로 입력받은 treeMap 은 정렬되어 있음.
     * 파일을 생성해 파라미터로 받은 구역 인색 부분을 저장한다.
     * @param outputPath 파일을 저장하려는 경로
     * @param zoneIndex 구연 인덱스
     */
    private void writeFile(String outputPath, TreeMap<Term, ArrayList<Posting>> zoneIndex) {
        File file = new File(outputPath);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            zoneIndex.forEach((key, value) -> {
                try {
                    bw.write(key.toString() + value.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            bw.close();
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    /**
     * 입력받은 컬렉션을 토대로 역색인 생성.
     * @param collections document list
     * @return HashMap<Term, List<Posting>>
     */
    private TreeMap<Term, ArrayList<Posting>>[] makeInvertedPostingList(List<Document> collections) {
        int zoneIndexSize = collections.get(0).getFieldsMap().size();
        /*
        field index
         */
        TreeMap<Term, ArrayList<Posting>>[] zoneIndexes
                = new TreeMap[zoneIndexSize];
        for (int i = 0; i < zoneIndexSize; i++) {
            zoneIndexes[i] = new TreeMap<Term, ArrayList<Posting>>();
        }
        /*
        collection 돌면서 구역 인덱스 생성
         */
        for (Document document : collections) {
            int idx = 0;
            HashMap<DocumentField, String> fieldsMap = document.getFieldsMap();
            Set<DocumentField> documentFields = fieldsMap.keySet();
            for (DocumentField documentField : documentFields) {
                makeZoneInvertedIndex(documentField,
                        document,
                        fieldsMap.get(documentField),
                        zoneIndexes[idx++]
                        );
            }
        }
        return zoneIndexes;
    }

    /**
     * make zoneIndex
     * 1. split fieldValue
     * 2. make term by split fieldValue
     * 3
     * @param fieldValue
     * @return
     */
    private void makeZoneInvertedIndex(DocumentField documentField,
                                                               Document document,
                                                               String fieldValue,
                                                               TreeMap<Term, ArrayList<Posting>> zoneIndex) {
        String[] splitFieldValue = fieldValue.split(" ");
        AtomicInteger index = new AtomicInteger(0);
        Arrays.stream(splitFieldValue)
                .forEach(splitValue -> {
                    Term term = new Term(splitValue);
                    if (!zoneIndex.containsKey(term)) {
                        zoneIndex.put(
                                term,
                                new ArrayList<>(List.of(new Posting(document.getDocumentId(), index.get()))){
                                    @Override
                                    public String toString() {
                                        return super.stream()
                                                .toString();
                                    }
                                }
                        );
                    }
                    else {
                        ArrayList<Posting> postings = zoneIndex.get(term);
                        Posting posting = new Posting(document.getDocumentId(), index.get());
                        if (!postings.contains(posting))
                            postings.add(posting);
                        /*
                        1. get current posting index
                        2. get current Posting object using index
                        3. add current position to posting
                        4. set changed posting
                         */
                        else {
                            int postingIdx = postings.indexOf(posting);
                            Posting changePosting = postings.get(postingIdx);
                            changePosting.addPositions(index.get());
                            postings.set(postingIdx, changePosting);
                        }
                        zoneIndex.put(
                                term,
                                postings
                                );
                    }
                    index.incrementAndGet();
                });
    }
}