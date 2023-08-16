package com.jkqj.common.utils;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MyLocalDateTimeUtilsTest {

    public static void main(String[] args) {
        System.out.println(MyLocalDateTimeUtils.convertToStandDateFormat("2019/1/1"));
    }

    @Test
    public void test_compare() {
        LocalDateTime first = LocalDateTime.of(2022,11,30, 15,35,22);
        LocalDateTime second = LocalDateTime.of(2011,11,30, 15,35,22);

        List<LocalDateTime> list = new ArrayList<>();
        list.add(first);
        list.add(second);

        list.sort(new Comparator<LocalDateTime>() {
            @Override
            public int compare(LocalDateTime o1, LocalDateTime o2) {
                return MyLocalDateTimeUtils.compare(o1,o2);
            }
        });

        int cmp = MyLocalDateTimeUtils.compare(first,second);
        System.out.println(cmp);
    }
}
