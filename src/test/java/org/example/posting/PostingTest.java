package org.example.posting;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;



class PostingTest {


    @Test
    @DisplayName("포스팅객체_toString_테스트")
    void toStringPostingTest() {
        Posting posting = new Posting(1, 1, 2, 3);
        System.out.println(posting.toString());
        Assertions.assertEquals(posting.toString(), "1 1 2 3 ");
    }
}