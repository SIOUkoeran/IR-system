package org.example.search;

import org.example.index.TokenNormalizer;
import org.example.posting.Posting;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BM25 implements Scorer{

    private final String path;
    private int limit = 50;

    private final TokenNormalizer tokenNormalizer = new TokenNormalizer();

    public BM25(String path) {
        this.path = path;
    }

    public BM25(String path, int limit) {
        this.path = path;
        this.limit = limit;
    }

    /**
     * index 폴더 안에 있는 파일 모두 읽은 후
     * 입력값으로 받은 query 실행
     * query 를 BM25 공식에 따라 계산한후 docId 별로 score 값 합치기
     * 해당 결과를 내림차순으로 limit / 2 크기만큼 정렬하여 반환
     * @param query 실행할 query (term 단위)
     * @return 내림차순 limit / 2 크기만큼의 리스트
     */
    public List<Score> search(String query) {
        File[] files = getFiles(path);
        List<List<Score>> scores = new ArrayList<>();
        String[] queries = splitQuery(query);
        for (File file : files) {
            for (String q : queries) {
                scores.add(new ArrayList<>(calculateScoreDocuments(file, q)));
            }
        }
        return scores.stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(
                        Score::getDocId,
                        Collectors.summingDouble(Score::getScore)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit / 2)
                .map(e ->
                    new Score(e.getKey(), e.getValue())
                )
                .collect(Collectors.toList());
    }
    private String[] splitQuery(String query) {
        return Arrays.stream(query.split(" "))
                .map(q -> tokenNormalizer
                        .makeToLowerCase(q)
                        .replaceRegex())
                .toArray(String[]::new);
    }

    private File[] getFiles(String path) {
        return new File(path).listFiles();
    }

    private List<Score> calculateScoreDocuments(File index, String query) {
        List<Score> scores = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(index));
            String line = "";
            List<Posting> postings = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(query)){
                    postings = getPosting(line);
                    break;
                }
            }
            if (postings == null)
                return scores;
            int totalSize = Integer.parseInt(readLastLine(index));
            return calculateScore(totalSize, postings, limit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readLastLine(File index) {
        try {
            RandomAccessFile file = new RandomAccessFile(index.getPath(), "r");
            long position = file.length() - 1;
            while (true) {
                file.seek(position);
                if (file.readByte() == '\n')
                    break;
                --position;
            }
            file.seek(++position);
            String line = file.readLine();
            file.close();
            return line;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * tf * idf 계산 결과 리스트를 반환하는 함수
     * idf = log (N - df_t + 0.5) / (df_t + 0.5)
     * @param n 인덱스 총 사이즈
     * @param postings 해당 용어의 posting 리스트
     * @return
     */
    @Override
    public List<Score> calculateScore(int n, List<Posting> postings, int limit) {
        List<Score> scores = new ArrayList<>();
        for (Posting posting : postings) {
            int tf = posting.getPositionsLength();
            double idf = Math.log((n - postings.size() + 0.5) / postings.size() + 0.5);
            scores.add(new Score(posting.getDocId(), tf * idf));
        }
        scores.sort(Collections.reverseOrder());
        scores.subList(0, Math.min(scores.size(), limit));
        return scores;
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
        if (lineArray.length < 2)
            return new ArrayList<>();
        for (int i = 1; i < lineArray.length - 1; i+=2) {
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
}
