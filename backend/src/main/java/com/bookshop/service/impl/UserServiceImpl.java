package com.bookshop.service.impl;

import com.bookshop.entity.User;
import com.bookshop.mapper.UserMapper;
import com.bookshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        if (checkUsernameExists(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (checkEmailExists(user.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        userMapper.insert(user);
        return user;
    }
    
    @Override
    public User login(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (dbUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!dbUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        return dbUser;
    }
    
    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    @Override
    public User updateUser(User user) {
        userMapper.updateById(user);
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
}