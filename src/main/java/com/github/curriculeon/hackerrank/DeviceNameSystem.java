package com.github.curriculeon.hackerrank;


import javafx.util.Pair;

import java.util.*;

/**
 * @author leonhunter
 * @created 04/03/2020 - 9:50 PM
 * create unique devices names to be used inresidential iot if item already exists, add integer to make it unuiouqe
 * integer starts with 1 and is incremented by 1 tfor each request of new existing name
 * gtiven a list of device name request procfess all requestrs and treturn array of unique devices
 */
public class DeviceNameSystem {

    public static List<String> deviceSystem(List<String> names) {

        List<String> result = new ArrayList<>();
        Map<Integer, List<String>> map = new HashMap<>();
        Set<String> uniqueNames = new LinkedHashSet<>(names);
        for (String name : uniqueNames) {
            Integer index = Collections.frequency(names, name);
            map.put(index, new ArrayList<>());
        }
        for (String name : uniqueNames) {
            Integer index = Collections.frequency(names, name);
            List<String> nameOccurringNTime = map.get(index);
            nameOccurringNTime.add(name);
            map.put(index, nameOccurringNTime);
        }

        for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
            Integer index = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                result.add(value);
            }
        }
        return null;
    }


    public static List<String> deviceSystem1(List<String> names) {
        List<String> result = new ArrayList<>();
        List<Pair<Integer, String>> pairList = new ArrayList<>();
        for (String name : names) {
            int occurrences = getNumberOfOccurrences(pairList, name);
            pairList.add(new Pair<>(occurrences, name));
        }

        for(Pair<Integer, String> pair : pairList) {
            result.add(pair.getValue() + pair.getKey());
        }
        return result;
    }

    private static int getNumberOfOccurrences(List<Pair<Integer, String>> result, String name) {
        int count = 0;
        for (Pair<Integer, String> pair : result) {
            if (pair.getValue().equals(name)) {
                count++;
            }
        }
        return count;
    }
}
