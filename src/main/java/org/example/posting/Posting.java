package org.example.posting;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Posting implements Comparator<Posting> {

    /**
     * document id (posting 정렬 키)
     */
    private int docId;

    /**
     * 위치 색인을 위한 배열
     */
    private List<Integer> positions = new ArrayList<>(){
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Integer position : this) {
                sb.append(position).append(" ");
            }
            return sb.toString();
        }
    };

    public Posting(int docId) {
        this.docId = docId;
    }

    public Posting(int docId, int idx) {
        this.docId = docId;
        positions.add(idx);
    }

    public Posting(int docId, int ... idxes) {
        this.docId = docId;
        for (int idx : idxes) {
            positions.add(idx);
        }
    }

    public void addPositions(int position) {
        positions.add(position);
    }

    @Override
    public int compare(Posting o1, Posting o2) {
        return o1.docId - o2.docId;
    }

    @Override
    public String toString() {
        return docId + " " + positions.toString();
    }
}
