package org.example.index;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 불용어 필터링 클래스
 */
public class StopWordFilter {

    private final static Set<String> stopWordSet = new HashSet<>(
            Arrays.asList(
                    "a",
                    "the",
                    "\\n",
                    "and",
                    "or",
                    " ",
                    ""
            )
    );

    /**
     * 1개의 stopWord 추가
     * @param stopWord 추가하려는 불용어
     */
    public void addStopWord(String stopWord) {
        stopWordSet.add(stopWord);
    }

    /**
     * 여러개의 stopWord 추가
     * @param stopWords 추가하려는 불용어들
     */
    public void addStopWords(String ... stopWords) {
        stopWordSet.addAll(Arrays.asList(stopWords));
    }

    /**
     * 해당 토큰이 불용어인지 체크
     * @param token 불용어인지 체크하려는 토큰
     * @return 불용어이면 false, 불용어가 아니라면 true
     */
    public static boolean filter(String token) {
        return !stopWordSet.contains(token);
    }
}
