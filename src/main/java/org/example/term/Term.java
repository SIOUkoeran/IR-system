package org.example.term;

import java.util.Comparator;
import java.util.Objects;

/**
 * document term class
 */
public class Term implements Comparable<Term> {

    private String term;

    /**
     * 전체 인덱스에서 term 빈도수
     */
    private int frequency = 0;

    public Term(String term) {
        this.term = term;
    }

    public Term increaseFrequency() {
        ++this.frequency;
        return this;
    }

    public Term setTermFreq(int freq) {
        this.frequency = freq;
        return this;
    }
    /**
     * term frequency increase method
     */
    public void addFrequency() {
        this.frequency++;
    }

    @Override
    public int compareTo(Term o) {
        return this.term.compareTo(o.term);
    }

    @Override
    public String toString() {
        return term + " " + frequency + " ";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(term);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Term term = (Term) obj;
        return Objects.equals(term.term, this.term);
    }
}
