package org.example;

public final class EditDistance {

    private EditDistance() {}

    public interface Algorithm {
        int distance(String a, String b);
        long operations();
    }

    public static final class Recursive implements Algorithm {
        private long calls;

        // State: f(i,j) is the distance between two prefixes.
        // Transition: match -> diagonal; mismatch -> minimum of
        // insertion, deletion and replacement plus one.
        // Base case: f(0,j)=j and f(i,0)=i.
        @Override
        public int distance(String a, String b) {
            calls = 0;
            return solve(a, b, a.length(), b.length());
        }

        private int solve(String a, String b, int i, int j) {
            calls++;

            if (i == 0) return j;
            if (j == 0) return i;

            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                return solve(a, b, i - 1, j - 1);
            }

            int delete = solve(a, b, i - 1, j);
            int insert = solve(a, b, i, j - 1);
            int replace = solve(a, b, i - 1, j - 1);

            return 1 + Math.min(delete, Math.min(insert, replace));
        }

        @Override
        public long operations() {
            return calls;
        }
    }

    public static final class Table implements Algorithm {
        private long cells;

        // State: dp[i][j] stores distance between prefixes.
        // Transition: min(delete+1, insert+1, replace+cost).
        // Base case: dp[i][0]=i and dp[0][j]=j.
        @Override
        public int distance(String a, String b) {
            cells = 0;
            int n = a.length();
            int m = b.length();

            int[][] dp = new int[n + 1][m + 1];

            for (int i = 0; i <= n; i++) dp[i][0] = i;
            for (int j = 0; j <= m; j++) dp[0][j] = j;

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= m; j++) {
                    int cost = a.charAt(i - 1) == b.charAt(j - 1)
                            ? 0 : 1;

                    dp[i][j] = Math.min(
                            dp[i - 1][j] + 1,
                            Math.min(
                                    dp[i][j - 1] + 1,
                                    dp[i - 1][j - 1] + cost
                            )
                    );
                    cells++;
                }
            }

            return dp[n][m];
        }

        @Override
        public long operations() {
            return cells;
        }
    }

    public static final class TwoRows implements Algorithm {
        private long cells;

        // State: prev and curr represent two DP rows.
        // Transition: min(delete, insert, replace).
        // Base case: prev[j]=j and curr[0]=i.
        @Override
        public int distance(String a, String b) {
            cells = 0;

            if (b.length() > a.length()) {
                String temp = a;
                a = b;
                b = temp;
            }

            int n = a.length();
            int m = b.length();

            int[] prev = new int[m + 1];
            int[] curr = new int[m + 1];

            for (int j = 0; j <= m; j++) prev[j] = j;

            for (int i = 1; i <= n; i++) {
                curr[0] = i;

                for (int j = 1; j <= m; j++) {
                    int cost = a.charAt(i - 1) == b.charAt(j - 1)
                            ? 0 : 1;

                    curr[j] = Math.min(
                            prev[j] + 1,
                            Math.min(
                                    curr[j - 1] + 1,
                                    prev[j - 1] + cost
                            )
                    );
                    cells++;
                }

                int[] temp = prev;
                prev = curr;
                curr = temp;
            }

            return prev[m];
        }

        @Override
        public long operations() {
            return cells;
        }
    }
}
