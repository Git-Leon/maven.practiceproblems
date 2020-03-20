package com.github.curriculeon;

import java.util.Arrays;

/**
 * @author leonhunter
 * @created 03/19/2020 - 8:44 PM
 */
public class BinarySearcher {
    private final Integer[] input;
    private final Integer relativeStartingIndex;

    public BinarySearcher(Integer[] input) {
        this(0, input);
    }

    public BinarySearcher(Integer relativeStartingIndex, Integer[] input) {
        this.input = input;
        this.relativeStartingIndex = relativeStartingIndex;
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
            return midPointIndex + relativeStartingIndex;

        // If element is smaller than mid, then search left
        if (isSmallerThanMidPoint) {
            subSearchSpace = Arrays.copyOfRange(input, startingIndex, midPointIndex-1);
            return new BinarySearcher(subSearchSpace).search(itemToFind);
        }

        // Else search right
        startingIndex = midPointIndex+1;
        subSearchSpace = Arrays.copyOfRange(input, startingIndex, endingIndex);
        int relativeOffset =  new BinarySearcher(startingIndex, subSearchSpace).search(itemToFind);
        return relativeOffset + relativeStartingIndex;
    }
}
