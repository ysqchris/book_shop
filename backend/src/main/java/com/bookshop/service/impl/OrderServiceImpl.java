package com.bookshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bookshop.dto.OrderCreateRequest;
import com.bookshop.entity.Book;
import com.bookshop.entity.Order;
import com.bookshop.entity.OrderItem;
import com.bookshop.mapper.BookMapper;
import com.bookshop.mapper.OrderItemMapper;
import com.bookshop.mapper.OrderMapper;
import com.bookshop.service.OrderService;
import com.bookshop.service.ShopSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private ShopSettingService shopSettingService;

    @Override
    @Transactional
    public Map<String, Object> createOrder(OrderCreateRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getItems())) {
            throw new RuntimeException("请选择要购买的图书");
        }
        if (!StringUtils.hasText(request.getReceiverName()) || !StringUtils.hasText(request.getReceiverPhone())) {
            throw new RuntimeException("请填写联系人姓名和电话");
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderCreateRequest.Item itemReq : request.getItems()) {
            if (itemReq.getBookId() == null || itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new RuntimeException("商品数量无效");
            }
            Book book = bookMapper.selectById(itemReq.getBookId());
            if (book == null || (book.getStatus() != null && book.getStatus() != 0)) {
                throw new RuntimeException("图书不存在或已下架");
            }
            if (book.getStock() == null || book.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("《" + book.getTitle() + "》库存不足");
            }

            BigDecimal price = book.getPrice() == null ? BigDecimal.ZERO : book.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(subtotal);

            OrderItem item = new OrderItem();
            item.setBookId(book.getId());
            item.setBookTitle(book.getTitle());
            item.setBookCover(book.getCoverImage());
            item.setBookPrice(price);
            item.setQuantity(itemReq.getQuantity());
            item.setSubtotal(subtotal);
            items.add(item);

            book.setStock(book.getStock() - itemReq.getQuantity());
            bookMapper.updateById(book);
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(request.getUserId() == null ? 0L : request.getUserId());
        order.setTotalAmount(total);
        order.setPayAmount(total);
        // 0 = 待线下确认付款（暂不支持线上支付）
        order.setStatus(0);
        order.setReceiverName(request.getReceiverName().trim());
        order.setReceiverPhone(request.getReceiverPhone().trim());
        order.setReceiverAddress(StringUtils.hasText(request.getReceiverAddress())
                ? request.getReceiverAddress().trim()
                : "待与店家确认");
        order.setRemark(request.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        return buildOrderResult(order, items);
    }

    @Override
    public Map<String, Object> getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        return buildOrderResult(order, items);
    }

    @Override
    public List<Order> listByUserId(Long userId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0).eq("user_id", userId).orderByDesc("create_time");
        return orderMapper.selectList(wrapper);
    }

    private Map<String, Object> buildOrderResult(Order order, List<OrderItem> items) {
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("items", items);
        data.put("shopContact", shopSettingService.toContactMap());
        return data;
    }

    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "BS" + time + random;
    }
}
