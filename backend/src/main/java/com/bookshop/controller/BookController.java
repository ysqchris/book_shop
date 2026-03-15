package com.bookshop.controller;

import com.bookshop.entity.Book;
import com.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }
    
    @GetMapping("/list")
    public List<Book> getBooks(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "12") int size,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) String keyword) {
        return bookService.getBooks(page, size, categoryId, keyword);
    }
    
    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }
    
    @PutMapping
    public Book updateBook(@RequestBody Book book) {
        return bookService.updateBook(book);
    }
    
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}