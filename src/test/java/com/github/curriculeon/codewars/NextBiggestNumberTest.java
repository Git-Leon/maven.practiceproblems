package com.github.curriculeon.codewars;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author leonhunter
 * @created 03/20/2020 - 1:52 PM
 * https://www.codewars.com/kata/55983863da40caa2c900004e/train/java
 */
public class NextBiggestNumberTest {
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
        NextBiggestNumber nextBiggestNumber = new NextBiggestNumber();

        // when
        Long actual = nextBiggestNumber.nextBiggest(input);

        // then
        Assert.assertEquals(expected, actual);
    }
}
