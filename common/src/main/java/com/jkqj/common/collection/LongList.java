package com.jkqj.common.collection;

import java.util.ArrayList;
import java.util.Collection;

public class LongList extends ArrayList<Long> {

    public static LongList from(Collection<Long> coll) {
        LongList longList = new LongList();

        if (coll == null || coll.size() == 0) {
            return longList;
        }

        longList.addAll(coll);

        return longList;
    }

    public static LongList from(Long... items) {
        LongList longList = new LongList();

        if (items.length == 0) {
            return longList;
        }

        for (Long item : items) {
            longList.add(item);
        }

        return longList;
    }

}
