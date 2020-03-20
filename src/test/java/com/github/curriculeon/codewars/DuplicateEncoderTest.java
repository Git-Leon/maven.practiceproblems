package com.github.curriculeon.codewars;

import com.github.curriculeon.codewars.DuplicateEncoder;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author leonhunter
 * @created 03/20/2020 - 12:00 PM
 */
public class DuplicateEncoderTest {
    @Test
    public void test1() {
        test("din", "(((");
    }


    @Test
    public void test2() {
        test("recede", "()()()");
    }


    @Test
    public void test3() {
        test("Success", ")())())");
    }

    private void test(String input, String expected) {
        // given
        DuplicateEncoder duplicateEncoder = new DuplicateEncoder();

        // when
        String actual = duplicateEncoder.encode(input);

        // then
        Assert.assertEquals(expected, actual);
    }

}
