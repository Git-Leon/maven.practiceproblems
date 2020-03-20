package com.github.curriculeon;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class BinarySearcherTest {
    @Test
    public void test1() {
        test(0, 999);
    }

    @Test
    public void test2() {
        test(0, 10);
    }

    private void test(int start, int max) {
        // Given
        int[] expected = IntStream.range(start,max).toArray();
        Integer[] input = getArray(start, max);
        BinarySearcher myObject = new BinarySearcher(input);

        // when
//        myObject.sort();

        //then
//        Assert.assertEquals(Arrays.toString(expected), Arrays.toString(myObject.toArray()));
    }

    private Integer[] getArray(int start, int max) {
        List<Integer> list = new ArrayList<>();
        IntStream.range(start,max).forEach(list::add);
        Collections.shuffle(list);
        return list.toArray(new Integer[0]);
    }
}
