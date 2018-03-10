package com.bsd.tutor.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kewalins on 2/25/2018 AD.
 */
public class ArrayListUtils {
    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
}
