package com.bookshop.service;

import com.bookshop.common.PageResult;
import com.bookshop.entity.Book;

import java.util.List;

public interface BookService {

    Book getBookById(Long id);

    PageResult<Book> getBooks(int page, int size, Long categoryId, String keyword, String sort);

    PageResult<Book> getAdminBooks(int page, int size, Long categoryId, Integer status, String keyword);

    Book addBook(Book book);

    Book updateBook(Book book);

    void deleteBook(Long id);

    List<Book> getHotBooks(int limit);

    List<Book> getRecommendBooks(Long userId, int limit);
}
