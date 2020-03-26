package com.github.curriculeon.codewars;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonhunter
 * @created 03/20/2020 - 12:11 PM
 */
public class NextSmallestNumber {
    Long nextSmaller() {
        if (isSorted()) {
            return input;
        }

        Integer lowestWeight = getLowestWeightedDigit();
        Integer secondLowestWeight = getSecondLowestWeightedDigit();
        if (lowestWeight < secondLowestWeight) {
            String valueAsString = getInputDigits().toString();
            int nextLowestMagnitudeIndex = getLowestMagnitudeIndex() - 1;
            String integerPrefix = valueAsString.substring(0, nextLowestMagnitudeIndex);
            return Long.parseLong(integerPrefix
                    .concat(lowestWeight.toString())
                    .concat(secondLowestWeight.toString()));
        }


        List<Integer> list = toIntegerList();// for every val v, i:1 -> n
        for (int i = 1; i < list.size(); i++) {
            Integer currentValue = list.get(i-1);
            Integer nextValue = list.get(i);
            // if curVal > nextValue, then
            if(currentValue > nextValue) {
                int nextBiggestValue = getIndexOfGreatestDigitValue(); //   get index of next biggest value
                // insert next biggest val at i-1
            }
        }
        throw new RuntimeException("Method not yet implemented entirely"); // todo
    }

    private final Long input;

    public NextSmallestNumber(Long input) {
        this.input = input;
    }

    public Long getInputDigits() {
        return input;
    }

    private Long getInputDigitsSorted() {
        StringBuilder sb = new StringBuilder();
        List<String> inputDigitList = toStringList();
        inputDigitList.sort(String::compareTo);
        inputDigitList.forEach(sb::append);
        return Long.parseLong(sb.toString());
    }

    private Long reverseDigits(Long digits) {
        return Long.parseLong(
                new StringBuilder(digits.toString())
                        .reverse()
                        .toString());
    }

    private List<String> toStringList() {
        return Arrays.asList(getInputDigits()
                .toString()
                .split(""));
    }

    private List<Integer> toIntegerList() {
        return toStringList()
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private Integer getNthGreatestDigitValue(Integer n) {
        return Integer.parseInt(String.valueOf(
                getInputDigitsSorted()
                        .toString()
                        .charAt(n)));
    }

    private Integer getNthLowestWeightedDigit(Integer n) {
        return Integer.parseInt(String.valueOf(
                getInputDigits()
                        .toString()
                        .charAt(getLowestMagnitudeIndex() - n)));
    }

    private Integer getIndexOfDigitValue(Integer digitValue) {
        return getInputDigits()
                .toString()
                .indexOf(digitValue.toString());
    }

    public Integer getGreatestDigitValue() {
        return getNthGreatestDigitValue(0);
    }

    public Integer getLowestDigitValue() {
        return getNthGreatestDigitValue(getInputDigits().toString().length() - 1);
    }

    public Integer getIndexOfGreatestDigitValue() {
        return getIndexOfDigitValue(getGreatestDigitValue());
    }

    public Integer getIndexOfLowestDigitValue() {
        return getIndexOfDigitValue(getLowestDigitValue());
    }

    private Integer getSecondLowestWeightedDigit() {
        return getNthLowestWeightedDigit(1);
    }

    private Integer getLowestWeightedDigit() {
        return getNthLowestWeightedDigit(0);
    }

    public Integer getGreatestMagnitudeIndex() {
        return 0;
    }

    public Integer getLowestMagnitudeIndex() {
        return getInputDigits().toString().length() - 1;
    }

    public Boolean isSorted() {
        return getInputDigits().equals(getInputDigitsSorted());
    }
}
