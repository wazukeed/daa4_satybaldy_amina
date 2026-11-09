package org.example;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmsTest {

    private final EditDistance.Algorithm[] edits = {
            new EditDistance.Recursive(),
            new EditDistance.Table(),
            new EditDistance.TwoRows()
    };

    private final StringSearch.Algorithm[] searches = {
            new StringSearch.Naive(),
            new StringSearch.KMP()
    };

    @Test
    void editReferences() {
        for (EditDistance.Algorithm algorithm : edits) {
            assertEquals(3, algorithm.distance("kitten", "sitting"));
            assertEquals(2, algorithm.distance("flaw", "lawn"));
            assertEquals(5, algorithm.distance("intention", "execution"));
            assertEquals(3, algorithm.distance("", "abc"));
            assertEquals(3, algorithm.distance("abc", ""));
            assertEquals(0, algorithm.distance("abc", "abc"));
            assertEquals(0, algorithm.distance("", ""));
        }
    }

    @Test
    void lpsReferences() {
        assertArrayEquals(
                new int[]{0, 1, 2, 0, 1, 2, 3, 3},
                StringSearch.KMP.lps("AAACAAAA")
        );

        assertArrayEquals(
                new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4},
                StringSearch.KMP.lps("ABABCABAB")
        );
    }

    @Test
    void searchReferences() {
        for (StringSearch.Algorithm algorithm : searches) {
            assertEquals(
                    List.of(10),
                    algorithm.search(
                            "ABABDABACDABABCABAB", "ABABCABAB")
            );

            assertEquals(
                    List.of(0, 1, 2, 3),
                    algorithm.search("AAAAA", "AA")
            );

            assertEquals(
                    List.of(0, 2, 4),
                    algorithm.search("ABABABA", "ABA")
            );

            assertEquals(
                    List.of(),
                    algorithm.search("abc", "abcdef")
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> algorithm.search("abc", "")
            );
        }
    }

    private String randomString(Random random, int n) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < n; i++) {
            builder.append((char) ('a' + random.nextInt(3)));
        }

        return builder.toString();
    }

    @Test
    void randomEditAgreement() {
        Random random = new Random(12345);

        for (int i = 0; i < 100; i++) {
            String a = randomString(random, random.nextInt(6));
            String b = randomString(random, random.nextInt(6));

            int expected = edits[0].distance(a, b);

            assertEquals(expected, edits[1].distance(a, b));
            assertEquals(expected, edits[2].distance(a, b));
        }
    }

    @Test
    void randomSearchAgreement() {
        Random random = new Random(67890);

        for (int i = 0; i < 100; i++) {
            String text = randomString(random, random.nextInt(30));
            String pattern = randomString(
                    random, 1 + random.nextInt(7)
            );

            assertEquals(
                    searches[0].search(text, pattern),
                    searches[1].search(text, pattern)
            );
        }
    }
}
