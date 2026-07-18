package com.bookshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bookshop.common.Result;
import com.bookshop.entity.Book;
import com.bookshop.entity.Category;
import com.bookshop.entity.Order;
import com.bookshop.entity.User;
import com.bookshop.mapper.BookMapper;
import com.bookshop.mapper.CategoryMapper;
import com.bookshop.mapper.OrderItemMapper;
import com.bookshop.mapper.OrderMapper;
import com.bookshop.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @GetMapping({"/admin/stats", "/api/admin/stats"})
    public Result<Map<String, Object>> stats() {
        return Result.success(buildStats());
    }

    @GetMapping({"/stats", "/api/stats"})
    public Result<Map<String, Object>> publicStats() {
        return Result.success(buildStats());
    }

    private Map<String, Object> buildStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.selectCount(new QueryWrapper<User>().eq("deleted", 0)));
        data.put("bookCount", bookMapper.selectCount(new QueryWrapper<Book>().eq("deleted", 0)));
        data.put("categoryCount", categoryMapper.selectCount(new QueryWrapper<Category>().eq("deleted", 0)));
        data.put("orderCount", orderMapper.selectCount(new QueryWrapper<Order>().eq("deleted", 0)));
        data.put("onSaleBookCount", bookMapper.selectCount(new QueryWrapper<Book>().eq("deleted", 0).eq("status", 0)));
        data.put("pendingOrderCount", orderMapper.selectCount(new QueryWrapper<Order>().eq("deleted", 0).eq("status", 1)));
        Long soldCount = orderItemMapper.sumSoldQuantity();
        data.put("soldCount", soldCount == null ? 0L : soldCount);
        return data;
    }
}
