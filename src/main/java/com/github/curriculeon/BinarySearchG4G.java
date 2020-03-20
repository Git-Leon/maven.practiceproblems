package com.github.curriculeon;

/**
 * @author leonhunter
 * @created 03/19/2020 - 9:17 PM
 */
public class BinarySearchG4G {

    // Returns index of x if it is present in arr[l.. r], else return -1
    int binarySearch(Integer input[], int startingIndex, int endingIndex, int itemToFind)
    {
        if (endingIndex >= startingIndex) {
            int searchSpaceSize = endingIndex - startingIndex;
            int offset = searchSpaceSize / 2;
            int midPointIndex = startingIndex + offset;
            int midPointValue = input[midPointIndex];

            boolean isPresentAtMidPoint = midPointValue == itemToFind;
            boolean isSmallerThanMidPoint = midPointValue > itemToFind;

            // If the element is present at the middle itself
            if (isPresentAtMidPoint)
                return midPointIndex;

            // If element is smaller than mid, then search left
            if (isSmallerThanMidPoint)
                return binarySearch(input, startingIndex, midPointIndex - 1, itemToFind);

            // Else search right
            return binarySearch(input, midPointIndex + 1, endingIndex, itemToFind);
        }

        // We reach here when element is not present in array
        return -1;
    }
}
