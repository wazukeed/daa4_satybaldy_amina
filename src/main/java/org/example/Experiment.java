package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class Experiment {

    private static final int TRIALS = 5;
    private static final long SEED = 20261108L;

    private Experiment() {}

    private static String randomString(int n, long seed) {
        Random random = new Random(seed);
        StringBuilder builder = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            builder.append((char) ('a' + random.nextInt(4)));
        }
        return builder.toString();
    }

    private static String repeat(char c, int n) {
        return String.valueOf(c).repeat(n);
    }

    private static void writeRow(
            PrintWriter writer, String family, String algorithm,
            String input, int n, int trial,
            long time, long operations, int answer) {

        writer.printf(
                Locale.US,
                "%s,%s,%s,%d,%d,%.6f,%d,%d%n",
                family, algorithm, input, n, trial,
                time / 1_000_000.0, operations, answer
        );
    }

    private static void runEdit(
            PrintWriter writer, String name,
            EditDistance.Algorithm algorithm,
            String type, int n, int trial) {

        String a = type.equals("random")
                ? randomString(n, SEED + n)
                : repeat('a', n);

        String b = type.equals("random")
                ? randomString(n, SEED + 90000 + n)
                : repeat('b', n);

        long start = System.nanoTime();
        int answer = algorithm.distance(a, b);
        long elapsed = System.nanoTime() - start;

        writeRow(writer, "edit", name, type, n, trial,
                elapsed, algorithm.operations(), answer);
    }

    private static void runSearch(
            PrintWriter writer, String name,
            StringSearch.Algorithm algorithm,
            String type, int n, int trial) {

        String text = type.equals("random")
                ? randomString(n, SEED + n)
                : repeat('a', n);

        String pattern = type.equals("random")
                ? "abacaba" : "aaaaab";

        long start = System.nanoTime();
        List<Integer> matches = algorithm.search(text, pattern);
        long elapsed = System.nanoTime() - start;

        writeRow(writer, "search", name, type, n, trial,
                elapsed, algorithm.operations(), matches.size());
    }

    public static void main(String[] args) throws IOException {

        Path output = Paths.get("results/results.csv");
        Files.createDirectories(output.getParent());

        try (PrintWriter writer =
                     new PrintWriter(Files.newBufferedWriter(output))) {

            writer.println(
                    "family,algorithm,input,n,trial,time_ms,operations,answer"
            );

            int[] recursiveSizes = {2, 4, 6, 8};
            int[] dpSizes = {100, 200, 400, 800};
            int[] searchSizes = {1000, 4000, 16000, 64000};

            String[] inputTypes = {"random", "adversarial"};

            for (int n : recursiveSizes) {
                for (String type : inputTypes) {
                    for (int t = 1; t <= TRIALS; t++) {
                        runEdit(writer, "recursive",
                                new EditDistance.Recursive(), type, n, t);
                    }
                }
            }

            for (int n : dpSizes) {
                for (String type : inputTypes) {
                    for (int t = 1; t <= TRIALS; t++) {
                        runEdit(writer, "table",
                                new EditDistance.Table(), type, n, t);

                        runEdit(writer, "two_rows",
                                new EditDistance.TwoRows(), type, n, t);
                    }
                }
            }

            for (int n : searchSizes) {
                for (String type : inputTypes) {
                    for (int t = 1; t <= TRIALS; t++) {
                        runSearch(writer, "naive",
                                new StringSearch.Naive(), type, n, t);

                        runSearch(writer, "kmp",
                                new StringSearch.KMP(), type, n, t);
                    }
                }
            }
        }

        System.out.println("Experiment completed.");
        System.out.println("Saved: " + output.toAbsolutePath());
    }
}
