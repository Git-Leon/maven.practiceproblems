package com.github.curriculeon.codewars;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author leonhunter
 * @created 03/20/2020 - 12:11 PM
 */
public class NextSmallestNumberTest {
    @Test
    public void test1() {
        test(21L, 12L);
    }

    @Test
    public void test2() {
        test(531L, 513L);
    }

    @Test
    public void test3() {
        test(2071L, 2017L);
    }

    private void test(Long input, Long expected) {
        // given
        NextSmallestNumber nextSmallestNumber = new NextSmallestNumber(input);

        // when
        Long actual = nextSmallestNumber.nextSmaller();

        // then
        Assert.assertEquals(expected, actual);
    }
}
