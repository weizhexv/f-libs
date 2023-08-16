package com.jkqj.dubbo.generic.model;

import java.util.List;

public class OrderRequest {
    private String number;
    private List<OrderItem> itemList;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<OrderItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<OrderItem> itemList) {
        this.itemList = itemList;
    }
}
