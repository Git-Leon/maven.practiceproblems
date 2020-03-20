package com.github.curriculeon.codewars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author leonhunter
 * @created 03/20/2020 - 1:57 PM
 * https://www.codewars.com/kata/5a20eeccee1aae3cbc000090/train/java
 */
public class SlidingPuzzle {
    private final int[][] puzzle;
    private final List<Integer> flattenedPuzzle;

    public SlidingPuzzle(int[][] puzzle) {
        this.puzzle = puzzle;
        this.flattenedPuzzle = flattenedPuzzle();
    }

    private List<Integer> flattenedPuzzle() {
        List<Integer> flattenedList = new ArrayList<>();
        int[] flattenedArray = Arrays.stream(puzzle)
                .flatMapToInt(Arrays::stream)
                .toArray();
        for(Integer currentValue : flattenedArray) {
            flattenedList.add(currentValue);
        }
        return flattenedList;
    }

    public List<Integer> solve() {
        flattenedPuzzle.remove(0);
        int width = puzzle[0].length;
        int height = puzzle.length;
        Integer[] segmentedArray = segmentArray(width, height);
        return Arrays.asList(segmentedArray);
    }

    private Integer[] segmentArray(int width, int height) {
        return new Integer[0];
    }
}
