package com.jkqj.common.collection;

import com.jkqj.common.pojo.CodeNamePair;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 编码名称对列表
 *
 * @author cb
 * @date 2022-07-19
 */
public class CodeNamePairs extends ArrayList<CodeNamePair> {

    public static CodeNamePairs from(Collection<CodeNamePair> coll) {
        CodeNamePairs codeNamePairs = new CodeNamePairs();

        if (coll == null || coll.size() == 0) {
            return codeNamePairs;
        }

        codeNamePairs.addAll(coll);

        return codeNamePairs;
    }

}
