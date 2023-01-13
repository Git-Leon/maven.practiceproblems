package com.github.curriculeon.sorting;

import java.util.Arrays;

/**
 * @author leonhunter
 * @created 03/19/2020 - 8:44 PM
 */
public class BinarySearcherGPT {
    private final Integer[] input;
    private final Integer relativeStartingIndex;

    public BinarySearcherGPT(Integer[] input) {
        this(0, input);
    }

    public BinarySearcherGPT(Integer relativeStartingIndex, Integer[] input) {
        this.input = input;
        this.relativeStartingIndex = relativeStartingIndex;
    }

    public Integer search(Integer itemToFind) {
        int lastIndex = input.length - 1;
        if (0 > lastIndex) {
            return -1; // item not found
        }

        int mid = (lastIndex) / 2;
        if (input[mid].equals(itemToFind)) {
            return mid + relativeStartingIndex; // item found
        } else if (input[mid] > itemToFind) {
            Integer[] subSearchSpace = Arrays.copyOfRange(input, 0, mid - 1);
            return new BinarySearcherGPT(relativeStartingIndex + 0, subSearchSpace).search(itemToFind);
        } else {
            Integer[] subSearchSpace = Arrays.copyOfRange(input, mid + 1, lastIndex);
            return new BinarySearcherGPT(relativeStartingIndex + mid + 1, subSearchSpace).search(itemToFind);
        }
    }

}
