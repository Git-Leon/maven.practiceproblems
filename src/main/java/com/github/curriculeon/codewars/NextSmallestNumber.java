package com.github.curriculeon.codewars;

/**
 * @author leonhunter
 * @created 03/20/2020 - 12:11 PM
 */
public class NextSmallestNumber {
    long nextSmaller(Long n) {
        String valueAsString = n.toString();
        int lowestMagnitudeIndex = valueAsString.length()-1;
        int nextLowestMagnitudeIndex = lowestMagnitudeIndex - 1;

        Character element1 = valueAsString.charAt(lowestMagnitudeIndex);
        Character element2 = valueAsString.charAt(nextLowestMagnitudeIndex);
        String integerPrefix = valueAsString.substring(0, nextLowestMagnitudeIndex);
        return Long.parseLong(integerPrefix
                .concat(element1.toString())
                .concat(element2.toString()));
    }
}
