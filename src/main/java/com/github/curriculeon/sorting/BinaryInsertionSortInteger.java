package com.github.curriculeon.sorting;

import java.util.Arrays;

/**
 * @author leonhunter
 * @created 03/19/2020 - 10:05 PM
 */
public class BinaryInsertionSortInteger implements BinaryInsertionSortInterface<Integer> {
    public static void main(String[] args) {
        final Integer[] arr = {37, 23, 0, 17, 12, 72, 31, 46, 100, 88, 54 };
        new BinaryInsertionSortInteger().sort(arr);

        for(int i=0; i<arr.length; i++)
            System.out.print(arr[i]+" ");
    }

    @Override
    public Integer[] sort(Comparable<Integer>[] array) {
        for (int currentIndex = 1; currentIndex < array.length; currentIndex++) {
            Comparable<Integer> currentValue = array[currentIndex];

            // Find location to insert using binary search
            int insertionLocation = Math.abs(Arrays.binarySearch(array, 0, currentIndex, currentValue) + 1);
//            j = Math.abs(new BinarySearcher(array).search(x) + 1);

            //Shifting array to one location right
            System.arraycopy(array, insertionLocation, array, insertionLocation + 1, currentIndex - insertionLocation);

            //Placing element at its correct location
            array[insertionLocation] = currentValue;
        }
        return new Integer[0];
    }
}
