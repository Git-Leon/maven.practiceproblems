package com.github.curriculeon.codewars;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author leonhunter
 * @created 03/20/2020 - 4:43 PM
 */
public class TimeFormatterTest {
    @Test
    public void exampleTests() {
        Assert.assertEquals("1 second", TimeFormatter.formatDuration(1));
    }

    @Test
    public void test1() {
        Assert.assertEquals("1 minute and 2 seconds", TimeFormatter.formatDuration(62));
    }

    @Test
    public void test2() {
        Assert.assertEquals("2 minutes", TimeFormatter.formatDuration(120));
    }

    @Test
    public void test3() {
        Assert.assertEquals("1 hour", TimeFormatter.formatDuration(3600));
    }

    @Test
    public void test4() {
        Assert.assertEquals("1 hour, 1 minute and 2 seconds", TimeFormatter.formatDuration(3662));
    }

}
