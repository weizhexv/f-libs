package com.jkqj.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 计算字符串字符的类
 *
 */
public class StringLengthUtils {
    private StringLengthUtils() {
    }

    /**
     * 计算字符串"真实字符"的长度，"真实字符" 表示一个unicode字符，在java中String#length是内部char数组的长度，
     * 而unicode在java中可能使用1个char或者2个char来表示，此时String#length比真实的字符数要大
     *
     * @param value
     * @return
     */
    public static int getLengthOfUnicode(String value) {
       if(StringUtils.isEmpty(value)) {
           return 0;
       }
       char[] charArray = value.toCharArray();
       int count = 0;
       for(int i = 0;i < charArray.length;i++){
           char c = charArray[i];
           if(Character.isSurrogate(c)){
               i++;
           }
           count++;
       }
       return count;
    }
}
