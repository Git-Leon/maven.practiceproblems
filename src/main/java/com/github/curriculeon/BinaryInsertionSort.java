package com.github.curriculeon;

/**
 * @author leonhunter
 * @created 03/19/2020 - 10:05 PM
 */
public class BinaryInsertionSort {
    public static void main(String[] args) {
        final Integer[] arr = {37, 23, 0, 17, 12, 72, 31, 46, 100, 88, 54 };
        new BinaryInsertionSort().sort(arr);

        for(int i=0; i<arr.length; i++)
            System.out.print(arr[i]+" ");
    }

    public Integer[] sort(Integer array[]) {
        array = array.clone();
        for (int currentIndex = 1; currentIndex < array.length; currentIndex++) {
            int currentValue = array[currentIndex];

            // Find location to insert using binary search
            int indexOfValue = new BinarySearcher(array).search(currentValue);
            int insertionLocation = Math.abs(indexOfValue + 1);
            int destinationPosition = insertionLocation + 1;
            int newLength = currentIndex-insertionLocation;

            //Shifting array to one location right
            System.arraycopy(array, insertionLocation, array, destinationPosition, newLength);

            //Placing element at its correct location
            array[insertionLocation] = currentValue;
        }
        return array;
    }
}
