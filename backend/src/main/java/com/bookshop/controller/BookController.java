package com.bookshop.controller;

import com.bookshop.common.PageResult;
import com.bookshop.common.Result;
import com.bookshop.entity.Book;
import com.bookshop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping({"/books", "/api/books"})
    public Result<PageResult<Book>> getBooks(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "12") int size,
                                             @RequestParam(required = false) Long categoryId,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String sort) {
        return Result.success(bookService.getBooks(page, size, categoryId, keyword, sort));
    }

    @GetMapping({"/admin/books", "/api/admin/books"})
    public Result<PageResult<Book>> getAdminBooks(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) Long categoryId,
                                                  @RequestParam(required = false) Integer status,
                                                  @RequestParam(required = false) String keyword) {
        return Result.success(bookService.getAdminBooks(page, size, categoryId, status, keyword));
    }

    @GetMapping({"/books/search", "/api/books/search"})
    public Result<PageResult<Book>> searchBooks(@RequestParam String keyword,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "12") int size,
                                                @RequestParam(required = false) String sort) {
        return Result.success(bookService.getBooks(page, size, null, keyword, sort));
    }

    @GetMapping({"/books/hot", "/api/books/hot"})
    public Result<List<Book>> getHotBooks(@RequestParam(defaultValue = "8") int limit) {
        return Result.success(bookService.getHotBooks(limit));
    }

    @GetMapping({"/books/recommend", "/api/books/recommend"})
    public Result<List<Book>> getRecommendBooks(@RequestParam(required = false) Long userId,
                                                @RequestParam(defaultValue = "6") int limit) {
        return Result.success(bookService.getRecommendBooks(userId, limit));
    }

    @GetMapping({"/book/{id}", "/api/book/{id}"})
    public Result<Book> getBookById(@PathVariable Long id) {
        return Result.success(bookService.getBookById(id));
    }

    @PostMapping({"/book", "/api/book"})
    public Result<Book> addBook(@RequestBody Book book) {
        return Result.success(bookService.addBook(book));
    }

    @PutMapping({"/book", "/api/book"})
    public Result<Book> updateBook(@RequestBody Book book) {
        return Result.success(bookService.updateBook(book));
    }

    @DeleteMapping({"/book/{id}", "/api/book/{id}"})
    public Result<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return Result.success(null);
    }
}
