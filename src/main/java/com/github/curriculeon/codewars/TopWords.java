package com.github.curriculeon.codewars;


import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leonhunter
 * @created 03/20/2020 - 12:25 PM
 * Write a function that, given a string of text (possibly with punctuation and line-breaks), returns an array of the top-3 most occurring words, in descending order of the number of occurrences.
 * Assumptions:
 * <p>
 * A word is a string of letters (A to Z) optionally containing one or more apostrophes (') in ASCII. (No need to handle fancy punctuation.)
 * Matches should be case-insensitive, and the words in the result should be lowercased.
 * Ties may be broken arbitrarily.
 * If a text contains fewer than three unique words, then either the top-2 or top-1 words should be returned, or an empty array if a text contains no words.
 */ // https://www.codewars.com/kata/51e056fe544cf36c410000fb/train/java
public class TopWords {

    public List<String> top3(final String input) {
        String s = sanitize(input);
        Map<String, Integer> occurrenceMap = new HashMap<>();
        String[] sentenceArray = s.split(" ");
        List<String> sentenceList = Arrays.asList(sentenceArray);
        for (String word : sentenceList) {
            if ("".equals(word)) continue;
            int occurrences = Collections.frequency(sentenceList, word);
            occurrenceMap.put(word, occurrences);
        }
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(occurrenceMap.entrySet());
        entryList.sort((entry1, entry2) -> {
            Integer entry1Value = entry1.getValue();
            Integer entry2Value = entry2.getValue();
            boolean sameValue = entry1Value.equals(entry2Value);
            if (!sameValue) {
                return -entry1Value.compareTo(entry2Value);
            }
            return entry1.getKey().compareTo(entry2.getKey());
        });

        return Arrays.asList(
                entryList.get(0).getKey(),
                entryList.get(1).getKey(),
                entryList.get(2).getKey());

    }
    private static String sanitize(String s) {
        s = s.toLowerCase();
        String invalidChars = "\\?\\:;,.!'-_/";
        for(Character invalidChar : invalidChars.toCharArray()) {
            s = s.replace(invalidChar, ' ');
        }

        return s.replaceAll("  ", " ");
    }
}
