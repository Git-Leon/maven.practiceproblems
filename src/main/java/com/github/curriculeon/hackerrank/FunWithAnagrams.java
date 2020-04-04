package com.github.curriculeon.hackerrank;

import java.util.*;

/**
 * @author leonhunter
 * @created 04/03/2020 - 10:41 PM
 */
public class FunWithAnagrams {
    public static List<String> funWithAnagrams(List<String> inputList) {
        for(String string : inputList) {
            inputList.removeAll(getAnagrams(inputList, string));
        }
        return inputList;
    }

    private static List<String> getAnagrams(List<String> inputList, String string) {
        List<String> result = new ArrayList<>();
        for(String val : inputList) {
            if(isAnagram(string, val)) {
                result.add(val);
            }
        }
        result.remove(string);
        return result;
    }

    private static boolean isAnagram(String baseString, String stringToCompareAgainst) {
        Collection<String> characterList = new HashSet<>(Arrays.asList(baseString.split("")));
        for(String character : characterList) {
            if(!stringToCompareAgainst.contains(character)) {
                return false;
            }
        }
        return baseString.length() == stringToCompareAgainst.length();
    }
}
