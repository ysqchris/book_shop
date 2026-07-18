package com.bookshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.PageResult;
import com.bookshop.entity.User;
import com.bookshop.mapper.UserMapper;
import com.bookshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User register(User user) {
        if (checkUsernameExists(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (checkEmailExists(user.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        user.setPassword(hashPassword(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(0);
        }
        if (user.getStatus() == null) {
            user.setStatus(0);
        }
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public User login(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (dbUser == null) {
            throw new RuntimeException("用户不存在");
        }

        String raw = user.getPassword();
        String hashed = hashPassword(raw);
        if (!dbUser.getPassword().equals(raw) && !dbUser.getPassword().equals(hashed)) {
            throw new RuntimeException("密码错误");
        }

        dbUser.setPassword(null);
        return dbUser;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        userMapper.updateById(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userMapper.selectByUsername(username) != null;
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userMapper.selectByEmail(email) != null;
    }

    @Override
    public PageResult<User> getUsers(int page, int size, String keyword, Integer role, Integer status) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (role != null) {
            wrapper.eq("role", role);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like("username", keyword)
                    .or().like("email", keyword)
                    .or().like("real_name", keyword)
                    .or().like("phone", keyword));
        }
        wrapper.orderByDesc("create_time");
        Page<User> pageData = userMapper.selectPage(new Page<>(page, size), wrapper);
        List<User> users = pageData.getRecords();
        users.forEach(u -> u.setPassword(null));
        return new PageResult<>(users, pageData.getTotal(), page, size);
    }

    private String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
