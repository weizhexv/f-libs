package com.jkqj.xlsx.output;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class TemplatedXlsxOutputTestCase {

    //    @Test
    public void test() throws FileNotFoundException {
        XlsxOutput xlsxOutput = new TemplatedXlsxOutput("tpl/tpl.xlsx", "Order");
        ArrayList<Order> list = new ArrayList<Order>();
        for (int i = 0; i < 500; i++) {
            list.add(new Order(23L + i, "客户" + (23L + i), 300L, 1 + 5));
        }
        FileOutputStream outputStream = new FileOutputStream("/Users/hexiufeng/tmp/out11.xlsx");
        xlsxOutput.output(list, outputStream);
    }

    public static class Order {

        private final Long id;
        private final String name;
        private final Long price;
        private final Integer count;

        public Order(Long id, String name, Long price, Integer count) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.count = count;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Long getPrice() {
            return price;
        }

        public Integer getCount() {
            return count;
        }
    }
}
