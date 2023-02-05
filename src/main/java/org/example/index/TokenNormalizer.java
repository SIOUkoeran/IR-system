package org.example.index;

import com.google.common.collect.Lists;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenNormalizer {

    private String token;

    private Set<String> regexSet = new HashSet<>();
    public TokenNormalizer(String ...token) {
        regexSet.addAll(List.of(token));
    }

    public void addSet(String ... token) {
        regexSet.addAll(List.of(token));
    }
    TokenNormalizer makeToLowerCase(String token) {
        this.token = token.toLowerCase();
        return this;
    }

    String replaceRegex() {
        regexSet.forEach(regex -> {
            this.token = token.replace(regex, "");
        });
        return this.token;
    }
}
