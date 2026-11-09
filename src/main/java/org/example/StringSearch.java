package org.example;

import java.util.ArrayList;
import java.util.List;

public final class StringSearch {

    private StringSearch() {}

    public interface Algorithm {
        List<Integer> search(String text, String pattern);
        long operations();
    }

    public static final class Naive implements Algorithm {
        private long comparisons;

        @Override
        public List<Integer> search(String text, String pattern) {
            if (pattern.isEmpty()) {
                throw new IllegalArgumentException("Empty pattern");
            }

            comparisons = 0;
            List<Integer> matches = new ArrayList<>();

            int n = text.length();
            int m = pattern.length();

            for (int i = 0; i <= n - m; i++) {
                int j = 0;

                while (j < m) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        break;
                    }
                    j++;
                }

                if (j == m) matches.add(i);
            }

            return matches;
        }

        @Override
        public long operations() {
            return comparisons;
        }
    }

    public static final class KMP implements Algorithm {
        private long comparisons;

        public static int[] lps(String pattern) {
            int[] result = new int[pattern.length()];
            int length = 0;
            int i = 1;

            while (i < pattern.length()) {
                if (pattern.charAt(i) == pattern.charAt(length)) {
                    result[i++] = ++length;
                } else if (length != 0) {
                    length = result[length - 1];
                } else {
                    result[i++] = 0;
                }
            }

            return result;
        }

        private int[] countedLps(String pattern) {
            int[] result = new int[pattern.length()];
            int length = 0;
            int i = 1;

            while (i < pattern.length()) {
                comparisons++;

                if (pattern.charAt(i) == pattern.charAt(length)) {
                    result[i++] = ++length;
                } else if (length != 0) {
                    length = result[length - 1];
                } else {
                    result[i++] = 0;
                }
            }

            return result;
        }

        @Override
        public List<Integer> search(String text, String pattern) {
            if (pattern.isEmpty()) {
                throw new IllegalArgumentException("Empty pattern");
            }

            comparisons = 0;
            List<Integer> matches = new ArrayList<>();

            if (pattern.length() > text.length()) {
                return matches;
            }

            int[] prefix = countedLps(pattern);
            int i = 0;
            int j = 0;

            while (i < text.length()) {
                comparisons++;

                if (text.charAt(i) == pattern.charAt(j)) {
                    i++;
                    j++;

                    if (j == pattern.length()) {
                        matches.add(i - j);
                        j = prefix[j - 1];
                    }
                } else if (j != 0) {
                    j = prefix[j - 1];
                } else {
                    i++;
                }
            }

            return matches;
        }

        @Override
        public long operations() {
            return comparisons;
        }
    }
}
