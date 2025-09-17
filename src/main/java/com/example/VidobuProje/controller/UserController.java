package com.example.VidobuProje.controller;

import com.example.VidobuProje.entity.User;
import com.example.VidobuProje.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

}
