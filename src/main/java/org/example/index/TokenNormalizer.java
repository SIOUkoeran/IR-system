package org.example.index;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenNormalizer {

    private String token;

    private final Set<String> regexSet = new HashSet<>(
            List.of(
                    " "
            )
    );
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
            this.token = token.replaceAll(regex, "");
        });
        return this.token;
    }
}
