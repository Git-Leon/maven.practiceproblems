package com.github.curriculeon.codewars;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leonhunter
 * @created 03/20/2020 - 4:43 PM
 */
public class TimeFormatter {

    public static String formatDuration(int i) {
        StringBuilder result = new StringBuilder();

        Integer[] data = parseTime(getTimeFormat(i));
        Integer hours = data[0];
        Integer minutes = data[1];
        Integer seconds = data[2];

        String hourStr = "";
        String minuteStr = "";
        String secondStr = "";

        if (hours != null) {
            hourStr = new StringBuilder()
                    .append(hours)
                    .append(" hour")
                    .append(hours > 1 ? "s" : "")
                    .toString();
        }

        if (minutes != null) {
            minuteStr = new StringBuilder()
                    .append(minutes)
                    .append(" minute")
                    .append(minutes > 1 ? "s" : "")
                    .toString();
        }

        if (seconds != null) {
            secondStr = new StringBuilder()
                    .append(seconds)
                    .append(" second")
                    .append(seconds > 1 ? "s" : "")
                    .toString();
        }

        result.append(hourStr);
        if (!"".equals(hourStr)) {
            result.append(" and ");
        }

        result.append(minuteStr);
        if (!"".equals(minuteStr)) {
            result.append(" and ");
        }

        result.append(secondStr);

        return result.toString();
    }

    private static Integer[] parseTime(String timeFormat) {
        StringBuilder sb = new StringBuilder();
        String[] data = timeFormat.split(":");
        String hours = data[0];
        String minutes = data[1];
        String seconds = data[2];

        Boolean hoursStartsWith0 = hours.startsWith("0");
        Boolean minutesStartsWith0 = minutes.startsWith("0");
        Boolean secondsStartsWith0 = seconds.startsWith("0");

        Boolean hoursEndsWith0 = hours.endsWith("0");
        Boolean minutesEndsWith0 = minutes.endsWith("0");
        Boolean secondsEndsWith0 = seconds.endsWith("0");
        if (hoursStartsWith0) {
            if (hoursEndsWith0) {
                hours = "";
            }
        }

        if (minutesStartsWith0) {
            if (minutesEndsWith0) {
                minutes = "";
            }
        }


        if (secondsStartsWith0) {
            if (secondsEndsWith0) {
                seconds = "";
            }
        }
        return new Integer[]{
                tryParseInt(hours),
                tryParseInt(minutes),
                tryParseInt(seconds)};
    }

    public static Integer tryParseInt(String time) {
        try {
            return Integer.parseInt(time);
        } catch (NumberFormatException nfe0) {
            return null;
        }
    }

    private static String getTimeFormat(Integer numberOfSeconds) {
        Integer numberOfMinutes = numberOfSeconds / 60;
        Integer numberOfHours = numberOfMinutes / 60;
        numberOfSeconds %= 60;
        numberOfMinutes %= 60;

        String seconds = numberOfSeconds.toString();
        String minutes = numberOfMinutes.toString();
        String hours = numberOfHours.toString();

        if (numberOfHours.toString().length() == 1) {
            hours = "0" + hours;
        }

        if (numberOfMinutes.toString().length() == 1) {
            minutes = "0" + minutes;
        }

        if (numberOfSeconds.toString().length() == 1) {
            seconds = "0" + seconds;
        }
        return hours + ":" + minutes + ":" + seconds;
    }
}