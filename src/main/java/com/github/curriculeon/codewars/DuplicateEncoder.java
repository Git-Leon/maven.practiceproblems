package com.github.curriculeon.codewars;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author leonhunter
 * @created 03/20/2020 - 11:58 AM
 */
public class DuplicateEncoder {


    String encode(String word){
        List<String> list = Arrays.asList(word.toLowerCase().split(""));
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<list.size(); i++) {
            String character = list.get(i);
            int numberOfOccurrences = Collections.frequency(list, character);
            if(numberOfOccurrences == 1) {
                sb.append("(");
            } else {
                sb.append(")");
            }
        }
        return sb.toString();
    }
}
