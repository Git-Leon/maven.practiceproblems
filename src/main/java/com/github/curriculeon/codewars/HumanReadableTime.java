package com.github.curriculeon.codewars;

/**
 * @author leonhunter
 * @created 03/20/2020 - 3:25 PM
 * https://www.codewars.com/kata/52685f7382004e774f0001f7/train/java
 */
public class HumanReadableTime {
    public static String makeReadable(Integer numberOfSeconds) {
        Integer numberOfMinutes = numberOfSeconds / 60;
        Integer numberOfHours = numberOfMinutes / 60;
        numberOfSeconds %= 60;
        numberOfMinutes %= 60;

        String seconds = numberOfSeconds.toString();
        String minutes = numberOfMinutes.toString();
        String hours = numberOfHours.toString();

        if(numberOfHours.toString().length() == 1) {
            hours = "0" + hours;
        }

        if(numberOfMinutes.toString().length() == 1) {
            minutes = "0" + minutes;
        }

        if(numberOfSeconds.toString().length() == 1) {
            seconds = "0" + seconds;
        }
        return hours + ":" + minutes + ":" + seconds;
    }
}
