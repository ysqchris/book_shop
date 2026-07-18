package com.bookshop.controller;

import com.bookshop.common.PageResult;
import com.bookshop.common.Result;
import com.bookshop.entity.User;
import com.bookshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping({"/user/register", "/api/user/register"})
    public Result<User> register(@RequestBody User user) {
        try {
            return Result.success("注册成功", userService.register(user));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping({"/user/login", "/api/user/login"})
    public Result<User> login(@RequestBody User user) {
        try {
            User loggedIn = userService.login(user);
            Result<User> result = Result.success("登录成功", loggedIn);
            result.setToken(UUID.randomUUID().toString().replace("-", ""));
            result.setUser(loggedIn);
            return result;
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping({"/user/info/{id}", "/api/user/info/{id}"})
    public Result<User> getUserInfo(@PathVariable Long id) {
        Result<User> result = Result.success(userService.getUserById(id));
        result.setUser(result.getData());
        return result;
    }

    @PutMapping({"/user/update", "/api/user/update"})
    public Result<User> updateUser(@RequestBody User user) {
        return Result.success(userService.updateUser(user));
    }

    @DeleteMapping({"/user/{id}", "/api/user/{id}"})
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success(null);
    }

    @PostMapping({"/user/logout", "/api/user/logout"})
    public Result<Void> logout() {
        return Result.success(null);
    }

    @GetMapping({"/admin/users", "/api/admin/users"})
    public Result<PageResult<User>> listUsers(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Integer role,
                                              @RequestParam(required = false) Integer status) {
        return Result.success(userService.getUsers(page, size, keyword, role, status));
    }
}
