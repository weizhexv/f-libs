package com.jkqj.common.collection;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class StringSet extends TreeSet<String> implements SortedSet<String> {

    public static StringSet from(Collection<String> coll) {
        StringSet stringSet = new StringSet();

        if (coll == null || coll.size() == 0) {
            return stringSet;
        }

        stringSet.addAll(coll);

        return stringSet;
    }
    public static StringSet from(String... items) {
        StringSet stringSet = new StringSet();

        if (items.length == 0) {
            return stringSet;
        }

        for (String item : items) {
            stringSet.add(item);
        }

        return stringSet;
    }
}
