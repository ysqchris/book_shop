package com.bookshop.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private List<Item> items;

    @Data
    public static class Item {
        private Long bookId;
        private Integer quantity;
    }
}
