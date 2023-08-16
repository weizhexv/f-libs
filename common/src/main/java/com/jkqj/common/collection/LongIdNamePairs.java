package com.jkqj.common.collection;

import com.jkqj.common.pojo.LongIdNamePair;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Long型ID和名称对列表
 *
 * @author cb
 * @date 2022-01-18
 */
public class LongIdNamePairs extends ArrayList<LongIdNamePair> {

    public static LongIdNamePairs from(Collection<LongIdNamePair> coll) {
        LongIdNamePairs longIdNamePairs = new LongIdNamePairs();

        if (coll == null || coll.size() == 0) {
            return longIdNamePairs;
        }

        longIdNamePairs.addAll(coll);

        return longIdNamePairs;
    }

}
