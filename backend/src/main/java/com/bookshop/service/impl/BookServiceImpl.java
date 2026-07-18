package com.bookshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.PageResult;
import com.bookshop.entity.Book;
import com.bookshop.mapper.BookMapper;
import com.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public Book getBookById(Long id) {
        return bookMapper.selectById(id);
    }

    @Override
    public PageResult<Book> getBooks(int page, int size, Long categoryId, String keyword, String sort) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0).eq("deleted", 0);
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like("title", keyword)
                    .or().like("author", keyword)
                    .or().like("publisher", keyword));
        }
        if ("price_asc".equals(sort)) {
            wrapper.orderByAsc("price");
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc("price");
        } else {
            wrapper.orderByDesc("create_time");
        }
        Page<Book> pageData = bookMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageData.getRecords(), pageData.getTotal(), page, size);
    }

    @Override
    public PageResult<Book> getAdminBooks(int page, int size, Long categoryId, Integer status, String keyword) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like("title", keyword)
                    .or().like("author", keyword)
                    .or().like("isbn", keyword)
                    .or().like("publisher", keyword));
        }
        wrapper.orderByDesc("create_time");
        Page<Book> pageData = bookMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(pageData.getRecords(), pageData.getTotal(), page, size);
    }

    @Override
    public Book addBook(Book book) {
        normalizeBook(book);
        bookMapper.insert(book);
        return book;
    }

    @Override
    public Book updateBook(Book book) {
        if (book.getAuthor() == null) {
            book.setAuthor("");
        }
        bookMapper.updateById(book);
        return book;
    }

    private void normalizeBook(Book book) {
        if (book.getTitle() != null) {
            book.setTitle(book.getTitle().trim());
        }
        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            book.setAuthor("");
        } else {
            book.setAuthor(book.getAuthor().trim());
        }
        if (book.getStatus() == null) {
            book.setStatus(0);
        }
        if (book.getStock() == null) {
            book.setStock(1);
        }
        if (book.getSellerId() == null) {
            book.setSellerId(1L);
        }
    }

    @Override
    public void deleteBook(Long id) {
        bookMapper.deleteById(id);
    }

    @Override
    public List<Book> getHotBooks(int limit) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0)
                .eq("deleted", 0)
                .orderByDesc("create_time")
                .last("LIMIT " + Math.max(limit, 1));
        return bookMapper.selectList(wrapper);
    }

    @Override
    public List<Book> getRecommendBooks(Long userId, int limit) {
        return getHotBooks(limit);
    }
}
