package org.example.term;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class TermTest {

    @Test
    @DisplayName("term 필드 값이 같은 Term 객체")
    void equalTermTest() {
        Term term1 = new Term("abc");
        Term term2 = new Term("abc");

        Assertions.assertEquals(term2, term1);
    }

    @Test
    @DisplayName("term 필드 값이 다른 Term 객체")
    void EqualTermTest2() {
        Term term1 = new Term("abc");
        Term term2 = new Term("abcd");

        Assertions.assertNotEquals(term1, term2);
    }

}