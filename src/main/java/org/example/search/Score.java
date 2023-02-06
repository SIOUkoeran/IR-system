package org.example.search;

public class Score implements Comparable<Score>{
    private int docId;

    private double score;

    public Score(int docId, double score) {
        this.docId = docId;
        this.score = score;
    }

    public int getDocId() {
        return docId;
    }

    public double getScore() {
        return score;
    }

    @Override
    public int compareTo(Score o) {
        return Double.compare(o.score, this.score);
    }

    @Override
    public String toString() {
        return "docId " + docId + " score " + score;
    }
}
