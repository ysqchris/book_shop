package com.bookshop.service;

import com.bookshop.entity.User;

public interface UserService {
    
    User register(User user);
    
    User login(User user);
    
    User getUserById(Long id);
    
    User updateUser(User user);
    
    void deleteUser(Long id);
    
    boolean checkUsernameExists(String username);
    
    boolean checkEmailExists(String email);
}