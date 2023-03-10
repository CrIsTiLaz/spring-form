package com.example.demo.service;

import com.example.demo.model.User;

import java.util.List;

public interface UserService {
    public void registerUser(User user);
    public List<User> findAllUsers();

}
