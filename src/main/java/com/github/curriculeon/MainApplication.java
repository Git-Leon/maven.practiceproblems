package com.github.curriculeon;

public class MainApplication {
    public static void main(String args[])
    {
        BinarySearchG4G ob = new BinarySearchG4G();
        Integer arr[] = { 2, 3, 4, 10, 40 };
        int n = arr.length;
        int x = 10;
        int result = ob.binarySearch(arr, 0, n - 1, x);
        BinarySearcher binarySearcher = new BinarySearcher(arr);
        int resultt = binarySearcher.search(x);
        if (result == -1)
            System.out.println("Element not present");
        else {
            System.out.println("Element found at index " + result);
            System.out.println("Element found at index " + resultt);
        }
    }
}
