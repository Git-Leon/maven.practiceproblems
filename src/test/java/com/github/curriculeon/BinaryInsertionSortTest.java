package com.github.curriculeon;

import com.github.curriculeon.sorting.BinarySearcherGPT;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class BinaryInsertionSortTest {
    @Test
    public void test1() {
        test(0, 25);
    }

    @Test
    public void test2() {
        test(0, 50);
    }

    @Test
    public void test3() {
        test(0, 100);
    }

    private void test(int start, int max) {
        // Given
        Integer[] input = getArray(start, max);
        BinarySearcherGPT myObject = new BinarySearcherGPT(input);

        // when
        myObject.search(10);
    }

    private Integer[] getArray(int start, int max) {
        List<Integer> list = new ArrayList<>();
        IntStream.range(start, max).forEach(list::add);
        Collections.shuffle(list);
        return list.toArray(new Integer[0]);
    }
}
