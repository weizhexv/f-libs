package com.jkqj.common.collection;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class LongSet extends TreeSet<Long> implements SortedSet<Long> {

    public static LongSet from(Collection<Long> coll) {
        LongSet longSet = new LongSet();

        if (coll == null || coll.size() == 0) {
            return longSet;
        }

        longSet.addAll(coll);

        return longSet;
    }

    public static LongSet from(Long... items) {
        LongSet longSet = new LongSet();

        if (items.length == 0) {
            return longSet;
        }

        for (Long item : items) {
            longSet.add(item);
        }

        return longSet;
    }

}
