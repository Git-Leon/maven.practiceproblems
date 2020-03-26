package com.github.curriculeon.coderpad;

import java.util.Arrays;

/**
 * @author leonhunter
 * @created 03/26/2020 - 11:26 AM
 */



class Sorter {
    private final Integer[] input;

    public Sorter(Integer[] input) {
        this.input = input;
    }

    public Integer[] sort() {
        Integer[] sortedArray = input.clone();
        for(int i=1; i<input.length; i++) {
            Integer prevValue = input[i-1];
            Integer currValue = input[i];
            if(prevValue < currValue) {
                continue;
            } else { // splice array
                Integer[] sortedSearchSpace = Arrays.copyOfRange(input, 0, i);
                int insertionLocation = Arrays.binarySearch(sortedSearchSpace, 0, i, currValue);
                int newSize = i - insertionLocation;
                System.arraycopy(sortedArray, insertionLocation, sortedArray, insertionLocation + 1, newSize);
                sortedArray[insertionLocation] = currValue;
            }
        }
        return sortedArray;
    }
}



