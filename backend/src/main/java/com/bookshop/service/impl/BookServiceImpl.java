package com.bookshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bookshop.entity.Book;
import com.bookshop.mapper.BookMapper;
import com.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 图书服务实现类
 */
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public boolean addBook(Book book) {
        // 设置默认状态为上架
        book.setStatus(0);
        return bookMapper.insert(book) > 0;
    }

    @Override
    public boolean updateBook(Book book) {
        return bookMapper.updateById(book) > 0;
    }

    @Override
    public Book getBookById(Long bookId) {
        return bookMapper.selectById(bookId);
    }

    @Override
    public List<Book> getBooksByCategoryId(Long categoryId) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", categoryId)
               .eq("status", 0)
               .eq("deleted", 0)
               .orderByDesc("create_time");
        return bookMapper.selectList(wrapper);
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAvailableBooks();
        }
        
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.like("title", keyword)
               .or().like("author", keyword)
               .or().like("publisher", keyword)
               .eq("status", 0)
               .eq("deleted", 0)
               .orderByDesc("create_time");
        return bookMapper.selectList(wrapper);
    }

    @Override
    public List<Book> getBooksBySellerId(Long sellerId) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.eq("seller_id", sellerId)
               .eq("deleted", 0)
               .orderByDesc("create_time");
        return bookMapper.selectList(wrapper);
    }

    @Override
    public List<Book> getAllAvailableBooks() {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0)
               .eq("deleted", 0)
               .orderByDesc("create_time");
        return bookMapper.selectList(wrapper);
    }

    @Override
    public boolean disableBook(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            return false;
        }
        book.setStatus(1); // 下架
        return bookMapper.updateById(book) > 0;
    }

    @Override
    public boolean enableBook(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            return false;
        }
        book.setStatus(0); // 上架
        return bookMapper.updateById(book) > 0;
    }

    @Override
    public boolean updateStock(Long bookId, Integer quantity) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            return false;
        }
        book.setStock(quantity);
        return bookMapper.updateById(book) > 0;
    }
}