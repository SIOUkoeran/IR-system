package org.example.term;

import java.util.Comparator;

/**
 * document term class
 */
public class Term implements Comparator<Term> {

    private String term;

    /**
     * 전체 인덱스에서 term 빈도수
     */
    private int frequency = 0;

    public Term(String term) {
        this.term = term;
    }

    public void increaseFrequency() {
        ++this.frequency;
    }
    /**
     * term frequency increase method
     */
    public void addFrequency() {
        this.frequency++;
    }

    @Override
    public int compare(Term o1, Term o2) {
        return o1.term.compareTo(o2.term);
    }

    @Override
    public String toString() {
        return term + " " + frequency;
    }
}
