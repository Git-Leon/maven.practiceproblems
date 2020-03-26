package com.github.curriculeon.coderpad;

import java.util.Arrays;

/**
 * @author leonhunter
 * @created 03/20/2020 - 11:17 PM
 * 1. express that `Solution` will run tests
 * 2. express that `TestSolution` will run `Solver`
 * 3. define `Solver`
 * 4. define utility classes
 */
public class Template {
    private static class Solution {
        public static void main(String[] args) {
            TestSolution test = new TestSolution();
            test.test1();
            test.test2();
        }
    }


    private static class TestSolution {
        public void test1() {
            test(new Integer[]{1, 6, 3, 9, 8, 5}, 1, 1);
        }

        public void test2() {
            test(new Integer[]{1, 6, 3, 9, 8, 5}, 2, 3);
        }


        private void test(Integer[] array, int nthSmallest, Integer expected) {
            // given
            Solver solution = new Solver(array);

            // when
            Integer actual = solution.solve(nthSmallest);

            // then
            assert expected == actual;
        }
    }


    private static class Solver {
        private Integer[] input;

        public Solver(Integer[] input) {
            this.input = input;
        }

        public Integer solve(Integer nthSmallest) {
            return new Sorter(input).sort()[nthSmallest];
        }
    }


    private static class Sorter {
        private final Integer[] input;

        public Sorter(Integer[] input) {
            this.input = input;
        }

        public Integer[] sort() {
            Integer[] sortedArray = input.clone();
            for (int i = 1; i < input.length; i++) {
                Integer prevValue = input[i - 1];
                Integer currValue = input[i];
                if (prevValue < currValue) {
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
}
