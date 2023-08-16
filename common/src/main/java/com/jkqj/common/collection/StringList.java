package com.jkqj.common.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class StringList extends ArrayList<String> {

    public static StringList from(Collection<String> coll) {
        StringList stringList = new StringList();

        if (coll == null || coll.size() == 0) {
            return stringList;
        }

        stringList.addAll(coll);

        return stringList;
    }
    public static StringList from(String... items) {
        StringList stringList = new StringList();

        if (items.length == 0) {
            return stringList;
        }

        for (String item : items) {
            stringList.add(item);
        }

        return stringList;
    }
}
