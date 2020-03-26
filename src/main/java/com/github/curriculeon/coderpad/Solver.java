package com.github.curriculeon.coderpad;

/**
 * @author leonhunter
 * @created 03/26/2020 - 11:26 AM
 */



class Solver {
    private Integer[] input;

    public Solver(Integer[] input) {
        this.input = input;
    }

    public Integer solve(Integer nthSmallest) {
        return new Sorter(input).sort()[nthSmallest];
    }
}

