package com.bookshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.PageResult;
import com.bookshop.common.Result;
import com.bookshop.entity.Order;
import com.bookshop.entity.OrderItem;
import com.bookshop.mapper.OrderItemMapper;
import com.bookshop.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @GetMapping({"/admin/orders", "/api/admin/orders"})
    public Result<PageResult<Order>> listOrders(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) Integer status,
                                                @RequestParam(required = false) String keyword) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like("order_no", keyword)
                    .or().like("receiver_name", keyword)
                    .or().like("receiver_phone", keyword));
        }
        wrapper.orderByDesc("create_time");
        Page<Order> pageData = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(new PageResult<>(pageData.getRecords(), pageData.getTotal(), page, size));
    }

    @GetMapping({"/admin/order/{id}", "/api/admin/order/{id}"})
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectByOrderId(id);
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("items", items);
        return Result.success(data);
    }

    @PutMapping({"/admin/order/{id}/ship", "/api/admin/order/{id}/ship"})
    public Result<Order> shipOrder(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 1) {
            return Result.error("仅已支付订单可发货");
        }
        order.setStatus(2);
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success(order);
    }

    @PutMapping({"/admin/order/{id}/complete", "/api/admin/order/{id}/complete"})
    public Result<Order> completeOrder(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() == null || order.getStatus() != 2) {
            return Result.error("仅已发货订单可完成");
        }
        order.setStatus(3);
        orderMapper.updateById(order);
        return Result.success(order);
    }

    @PutMapping({"/admin/order/{id}/cancel", "/api/admin/order/{id}/cancel"})
    public Result<Order> cancelOrder(@PathVariable Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != null && order.getStatus() >= 3) {
            return Result.error("当前订单状态不可取消");
        }
        order.setStatus(4);
        orderMapper.updateById(order);
        return Result.success(order);
    }
}
