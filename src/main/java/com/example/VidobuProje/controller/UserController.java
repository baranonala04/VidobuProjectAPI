package com.example.VidobuProje.controller;

import com.example.VidobuProje.entity.User;
import com.example.VidobuProje.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/users")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @PutMapping("/update{id}")
    public String updateUser(@PathVariable Long id , @RequestBody User user){
        User existingUser = userRepository.findById(id).orElse(null);

        if(existingUser == null) return "User not found !";

        if(user.getUsername() != null && !user.getUsername().isBlank()){
            existingUser.setUsername(user.getUsername());
        }
        if(user.getPassword() != null && !user.getPassword().isBlank()){
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(existingUser);
        return "User updated successfully! ";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id){
        if(!userRepository.existsById(id)){
            return "User not found !";
        }

        userRepository.deleteById(id);
        return "User deleted successfully !";
    }

}
