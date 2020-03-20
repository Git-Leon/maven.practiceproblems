package com.github.curriculeon.codewars;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author leonhunter
 * @created 03/20/2020 - 3:26 PM
 */
public class HumanReadableTimeTest {
    private void test(String message, String expected, String actual) {
        Assert.assertEquals(message, expected, actual);
    }

    @Test
    public void test1() {
        test("makeReadable(0)", "00:00:00", HumanReadableTime.makeReadable(0));
    }

    @Test
    public void test2() {
        test("makeReadable(5)", "00:00:05", HumanReadableTime.makeReadable(5));
    }

    @Test
    public void test3() {
        test("makeReadable(60)", "00:01:00", HumanReadableTime.makeReadable(60));
    }

    @Test
    public void test4() {
        test("makeReadable(86399)", "23:59:59", HumanReadableTime.makeReadable(86399));
    }

    @Test
    public void test5() {
        test("makeReadable(359999)", "99:59:59", HumanReadableTime.makeReadable(359999));
    }
}
