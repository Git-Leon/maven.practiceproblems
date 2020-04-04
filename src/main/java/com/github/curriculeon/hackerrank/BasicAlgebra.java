package com.github.curriculeon.hackerrank;

import org.junit.Test;

/**
 * @author leonhunter
 * @created 04/03/2020 - 9:39 PM
 */
public class BasicAlgebra {

    public static int min(int a, int k, int p) {
        if (a > k) {
            return -1;
        } else {
            int kellyCount = 0;
            int ashaCount = 0;
            int minCount = 0;
            while (kellyCount <= ashaCount) {
                kellyCount += k;
                ashaCount += a;
                minCount++;
            }
            return minCount;
        }
    }

    @Test
    public void test1() {
        test(3, 5, 5);
    }

    @Test
    public void test2() {
        test(1, 2, 3);
    }

    @Test
    public void test3() {
        test(2, 3, 4);
    }

    private void test(int a, int k, int p) {
        System.out.println("a = " + a);
        System.out.println("k = " + k);
        System.out.println("p = " + p);
        System.out.println("min = " + min(a,k,p));
    }


}
