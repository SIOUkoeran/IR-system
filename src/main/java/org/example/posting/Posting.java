package org.example.posting;


import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
                sb.append(position).append(",");
            }
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
    };

    public Posting(int docId) {
        this.docId = docId;
    }

    public Posting(int docId, ArrayList<Integer> positions) {
        this.docId = docId;
        this.positions.addAll(positions);
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

    public int getPositionsLength() {
        return positions.size();
    }

    public List<Integer> getPositions() {
        return positions;
    }

    public int getDocId() {
        return docId;
    }

    @Override
    public int compare(Posting o1, Posting o2) {
        return o1.docId - o2.docId;
    }

    @Override
    public String toString() {
        return docId + " [" + positions.toString() + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(docId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Posting posting = (Posting) obj;
        return Objects.equals(posting.docId, this.docId);

    }
}
