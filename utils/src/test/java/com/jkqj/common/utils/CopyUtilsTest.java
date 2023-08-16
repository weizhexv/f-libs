package com.jkqj.common.utils;

import com.jkqj.common.pojo.CodeNamePair;
import com.jkqj.common.pojo.LongIdNamePair;
import org.junit.Test;

public class CopyUtilsTest {

    @Test
    public void copy() {
        LongIdNamePair longIdNamePair = new LongIdNamePair(1L, "name");
        CodeNamePair codeNamePair = CopyUtils.copy(longIdNamePair, CodeNamePair::new);
        System.out.println(codeNamePair);
    }

}
