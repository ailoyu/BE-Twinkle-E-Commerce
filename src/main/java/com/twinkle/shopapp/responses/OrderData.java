package com.twinkle.shopapp.responses;

import java.util.List;

public class OrderData {
    private String table = "orders";
    private List<OrderRow> rows;

    public OrderData(List<OrderRow> rows) {
        this.rows = rows;
    }

    // Getters and setters for table and rows
}

