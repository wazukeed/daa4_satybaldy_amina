package org.example;

public class Main {

    public static void main(String[] args) {

        System.out.println("DAA Assignment 4");
        System.out.println("City Search & Spell-Check");

        EditDistance.Algorithm recursive = new EditDistance.Recursive();
        EditDistance.Algorithm table = new EditDistance.Table();
        EditDistance.Algorithm twoRows = new EditDistance.TwoRows();

        String a = "kitten";
        String b = "sitting";

        System.out.println("\nEDIT DISTANCE");
        System.out.println("Recursive: " + recursive.distance(a, b));
        System.out.println("DP Table: " + table.distance(a, b));
        System.out.println("Two Rows: " + twoRows.distance(a, b));

        String text = "ABABABA";
        String pattern = "ABA";

        StringSearch.Algorithm naive = new StringSearch.Naive();
        StringSearch.Algorithm kmp = new StringSearch.KMP();

        System.out.println("\nSTRING SEARCH");
        System.out.println("Naive: " + naive.search(text, pattern));
        System.out.println("KMP: " + kmp.search(text, pattern));
    }
}
