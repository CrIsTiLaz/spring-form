package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepo repo;
    @Override
    public void registerUser(User user) {
        repo.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        return (List<User>) repo.findAll();
    }
}
