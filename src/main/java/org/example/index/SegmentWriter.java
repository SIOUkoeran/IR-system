package org.example.index;

import com.google.common.collect.*;
import org.example.document.Document;
import org.example.document.DocumentConfig;
import org.example.document.DocumentField;
import org.example.posting.Posting;
import org.example.term.Term;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * segment writer
 */
public class SegmentWriter {

    private final String outputPath;

    private final Multimap<String, String> blockFile = ArrayListMultimap.create();
    private final Set<String> blockFiles = new HashSet<>();

    private final TokenNormalizer tokenNormalizer;
    private static int segmentCount = 0;
    public SegmentWriter(String outputPath, TokenNormalizer tokenNormalizer) {
        this.outputPath = outputPath;
        this.tokenNormalizer = tokenNormalizer;
    }

    /**
     * write mini index to disk.
     * @param collections to write Collection in disk.
     */
    public void writeSegment(List<Document> collections, DocumentConfig documentConfig){
        TreeMap<Term, ArrayList<Posting>>[] zoneIndexes = makeInvertedIndex(collections, documentConfig);
        List<DocumentField> documentFieldList = documentConfig.getDocumentFieldList();
        for (int i = 0; i < zoneIndexes.length; i++) {
            writeFile(
                    documentFieldList.get(i).getFieldName() + segmentCount,
                    zoneIndexes[i]
            );
            blockFiles.add(documentFieldList.get(i).getFieldName() + segmentCount);
            blockFile.put(documentFieldList.get(i).getFieldName(), String.valueOf(segmentCount));
        }
        ++segmentCount;
    }

    /**
     * 분할 저장했던 block 들을 병합하여 하나의 파일로 새로 생성하는 메소드
     */
    public void mergeBlocks() {
        Map<String, List<BufferedReader>> bufferedReaders = openBlocksReturnMap();
        for (Map.Entry<String, List<BufferedReader>> brEntry : bufferedReaders.entrySet()) {
            List<BufferedReader> brList = brEntry.getValue();
            List<String> lines = new ArrayList<>();
            brList.removeIf(br -> {
                try {
                    String line = br.readLine();
                    if (line != null && !line.equals(""))
                        lines.add(line);
                    return line == null || line.equals("");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            writeBlocks(brList, lines, brEntry.getKey());
        }
    }

    public void writeBlocks(List<BufferedReader> brList, List<String> lines, String filePrefix) {
        StringBuilder recentTerm = new StringBuilder(" ");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + filePrefix))) {
            while (lines.size() > 0 && brList.size() > 0) {
                int min = lines.indexOf(Collections.min(lines));
                String line = lines.get(min);
                String curTerm = line.split(" ")[0];
                List<Posting> postingList = getPosting(line);
                if (!curTerm.equals(recentTerm.toString())) {
                    bw.write("\n" + curTerm + " " + postingList);
                    recentTerm.setLength(0);
                    recentTerm.append(curTerm);
                } else {
                    bw.write(" " + postingList);
                }

                lines.set(min, brList.get(min).readLine());
                if (lines.get(min) == null || lines.get(min).equals("")){
                    brList.get(min).close();
                    brList.remove(min);
                    lines.remove(min);
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        };

    }

    private List<Posting> getPosting(String line) {
        String[] lineArray = line.split(" ");
        List<Posting> postingList = new ArrayList<>(){
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                for (Posting posting : this) {
                    sb.append(posting.toString()).append(" ");
                }
                return sb.toString();
            }
        };
        if (lineArray.length < 3)
            return new ArrayList<>();
        for (int i = 2; i < lineArray.length - 1; i+=2) {
            int docId = Integer.parseInt(lineArray[i]);
            ArrayList<Integer> positions = getPositions(lineArray[i + 1]);
            postingList.add(new Posting(docId, positions));
        }
        System.out.println(postingList.toString());
        return postingList;
    }

    private ArrayList<Integer> getPositions(String positions) {
        return (ArrayList<Integer>) Arrays.stream(positions.substring(1, positions.length() - 1).split(","))
                .mapToInt(Integer::parseInt)
                 .boxed()
                 .collect(Collectors.toList());
    }

    private Map<String,List<BufferedReader>> openBlocksReturnMap() {
        return blockFile.asMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(value -> {
                                    try{
                                        return new BufferedReader(new FileReader(outputPath + e.getKey() + value));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList())
                ));
    }

    /**
     * 파라미터로 입력받은 treeMap 은 정렬되어 있음.
     * 파일을 생성해 파라미터로 받은 구역 인색 부분을 저장한다.
     * @param fileName 파일 구역 인덱스 이름
     * @param zoneIndex 구연 인덱스
     */
    private void writeFile(String fileName, TreeMap<Term, ArrayList<Posting>> zoneIndex) {
        File file = new File(outputPath + fileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            zoneIndex.forEach((key, value) -> {
                try {
                    bw.write(key.toString() + value.toString());
                    bw.write("\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            bw.flush();
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 입력받은 컬렉션을 토대로 역색인 생성.
     * @param collections document list
     * @return HashMap<Term, List<Posting>>
     */
    private TreeMap<Term, ArrayList<Posting>>[] makeInvertedIndex(List<Document> collections,
                                                                  DocumentConfig documentConfig) {
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
        List<DocumentField> documentFields = documentConfig.getDocumentFieldList();
        for (Document document : collections) {
            HashMap<DocumentField, String> fieldsMap = document.getFieldsMap();
            int idx = 0;
            for (DocumentField documentField : documentFields) {
                makeZoneInvertedIndex(
                        documentField,
                        document,
                        fieldsMap.get(documentField),
                        zoneIndexes[idx++]
                        );
            }
            //TODO (document 색인기 구현)
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
        String[] splitFieldValue = fieldValue.split("[ ,]");
        AtomicInteger index = new AtomicInteger(0);
        Arrays.stream(splitFieldValue)
                .map(this::makeNormalize)
                .filter(StopWordFilter::filter)
                .forEach(splitValue -> {
                    Term term = new Term(splitValue);
                    if (!zoneIndex.containsKey(term)) {
                        zoneIndex.put(
                                term.increaseFrequency(),
                                new ArrayList<>(List.of(new Posting(document.getDocumentId(), index.get()))){
                                    @Override
                                    public String toString() {
                                        StringBuilder sb = new StringBuilder();
                                        this.forEach(posting -> {
                                            sb.append(posting).append(" ");
                                        });
                                        sb.append(sb.length() - 1);
                                        return sb.toString();
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
                        zoneIndex.remove(term);
                        zoneIndex.put(
                                term.setTermFreq(
                                        calculateTermFreq(postings)
                                ),
                                postings
                        );
                    }
                    index.incrementAndGet();
                });
    }

    private int calculateTermFreq(ArrayList<Posting> postingList) {
        int cnt = 0;
        for (Posting posting : postingList) {
            cnt += posting.getPositionsLength();
        }
        return cnt;
    }

    private String makeNormalize(String token) {
        return tokenNormalizer
                .makeToLowerCase(token)
                .replaceRegex();
    }

    public Set<String> getBlocks() {
        return blockFiles;
    }
}
