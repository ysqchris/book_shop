package com.bookshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bookshop.common.Result;
import com.bookshop.entity.Book;
import com.bookshop.entity.Category;
import com.bookshop.mapper.BookMapper;
import com.bookshop.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BookMapper bookMapper;

    @GetMapping({"/categories", "/api/categories"})
    public Result<List<Category>> getCategories() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0).eq("status", 0).orderByAsc("sort_order");
        return Result.success(categoryMapper.selectList(wrapper));
    }

    @GetMapping({"/admin/categories", "/api/admin/categories"})
    public Result<List<Category>> getAdminCategories() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0).orderByAsc("sort_order").orderByAsc("id");
        return Result.success(categoryMapper.selectList(wrapper));
    }

    @GetMapping({"/categories/hot", "/api/categories/hot"})
    public Result<List<Category>> getHotCategories(@RequestParam(defaultValue = "6") int limit) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0)
                .eq("status", 0)
                .eq("level", 1)
                .orderByAsc("sort_order")
                .last("LIMIT " + Math.max(limit, 1));
        return Result.success(categoryMapper.selectList(wrapper));
    }

    @GetMapping({"/categories/tree", "/api/categories/tree"})
    public Result<List<Category>> getCategoryTree() {
        return getCategories();
    }

    @GetMapping({"/category/{id}", "/api/category/{id}"})
    public Result<Category> getCategoryDetail(@PathVariable Long id) {
        return Result.success(categoryMapper.selectById(id));
    }

    @GetMapping({"/category/{id}/subcategories", "/api/category/{id}/subcategories"})
    public Result<List<Category>> getSubCategories(@PathVariable Long id) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id).eq("deleted", 0).eq("status", 0).orderByAsc("sort_order");
        return Result.success(categoryMapper.selectList(wrapper));
    }

    @GetMapping({"/category/{id}/books", "/api/category/{id}/books"})
    public Result<List<Book>> getBooksByCategory(@PathVariable Long id) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", id).eq("status", 0).eq("deleted", 0).orderByDesc("create_time");
        return Result.success(bookMapper.selectList(wrapper));
    }

    @PostMapping({"/admin/category", "/api/admin/category"})
    public Result<Category> createCategory(@RequestBody Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getLevel() == null) {
            category.setLevel(category.getParentId() == 0L ? 1 : 2);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(0);
        }
        categoryMapper.insert(category);
        return Result.success(category);
    }

    @PutMapping({"/admin/category", "/api/admin/category"})
    public Result<Category> updateCategory(@RequestBody Category category) {
        if (category.getId() == null) {
            return Result.error("分类 ID 不能为空");
        }
        categoryMapper.updateById(category);
        return Result.success(categoryMapper.selectById(category.getId()));
    }

    @DeleteMapping({"/admin/category/{id}", "/api/admin/category/{id}"})
    public Result<Void> deleteCategory(@PathVariable Long id) {
        Long bookCount = bookMapper.selectCount(new QueryWrapper<Book>().eq("category_id", id).eq("deleted", 0));
        if (bookCount != null && bookCount > 0) {
            return Result.error("该分类下仍有图书，无法删除");
        }
        Long childCount = categoryMapper.selectCount(new QueryWrapper<Category>().eq("parent_id", id).eq("deleted", 0));
        if (childCount != null && childCount > 0) {
            return Result.error("该分类下仍有子分类，无法删除");
        }
        categoryMapper.deleteById(id);
        return Result.success(null);
    }
}
