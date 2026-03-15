package com.bookshop.service;

import com.bookshop.entity.Book;
import java.util.List;

public interface BookService {
    
    Book getBookById(Long id);
    
    List<Book> getBooks(int page, int size, Long categoryId, String keyword);
    
    Book addBook(Book book);
    
    Book updateBook(Book book);
    
    void deleteBook(Long id);
    
    List<Book> getHotBooks(int limit);
    
    List<Book> getRecommendBooks(Long userId, int limit);
}