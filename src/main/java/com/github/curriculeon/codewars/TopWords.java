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

        occurrenceMap = occurrenceMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new));

        List<String> keys = new ArrayList<>(occurrenceMap.keySet());
        Collections.reverse(keys);
        List<String> result = keys.subList(0, 3);
        Collections.sort(result);
        Collections.reverse(result);

        return result;

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
