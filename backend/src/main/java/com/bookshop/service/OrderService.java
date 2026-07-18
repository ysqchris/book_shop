package com.bookshop.service;

import com.bookshop.dto.OrderCreateRequest;
import com.bookshop.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Map<String, Object> createOrder(OrderCreateRequest request);

    Map<String, Object> getOrderDetail(Long orderId);

    List<Order> listByUserId(Long userId);
}
