package com.github.curriculeon.coderpad;

/**
 * @author leonhunter
 * @created 03/26/2020 - 11:26 AM
 */


class TestSolution {
    public void test1() {
        test(new Integer[]{1,6,3,9,8,5}, 1, 1);
    }

    public void test2() {
        test(new Integer[]{1,6,3,9,8,5}, 2, 3);
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


