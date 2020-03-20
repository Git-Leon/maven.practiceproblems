package com.github.curriculeon;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author leonhunter
 * @created 03/19/2020 - 8:44 PM
 */
public class BinarySearcher {
    private final Integer[] input;

    public BinarySearcher(Integer[] input) {
        this.input = input;
    }

    public Integer search(Integer itemToFind) {
        int startingIndex = 0;
        int endingIndex = input.length - 1;
        int midPointIndex = endingIndex / 2;
        int midPointValue = input[midPointIndex];
        boolean isPresentAtMidPoint = midPointValue == itemToFind;
        boolean isSmallerThanMidPoint = midPointValue > itemToFind;
        Integer[] subSearchSpace;

        // If the element is present at the middle itself
        if (isPresentAtMidPoint)
            return midPointIndex;

        // If element is smaller than mid, then search left
        if (isSmallerThanMidPoint) {
            subSearchSpace = Arrays.copyOfRange(input, startingIndex, midPointIndex-1);
            return new BinarySearcher(subSearchSpace).search(itemToFind);
        }

        // Else search right
        subSearchSpace = Arrays.copyOfRange(input, midPointIndex+1, endingIndex);
        int result =  new BinarySearcher(subSearchSpace).search(itemToFind);
        return result;
    }
}
