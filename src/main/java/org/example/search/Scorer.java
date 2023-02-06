package org.example.search;

import org.example.posting.Posting;

import java.util.List;

public interface Scorer {
    List<Score> calculateScore(int n, List<Posting> postings, int limit);
    List<Score> search(String query);
}
