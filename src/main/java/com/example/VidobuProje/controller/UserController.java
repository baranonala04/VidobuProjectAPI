package com.example.VidobuProje.controller;

import com.example.VidobuProje.entity.User;
import com.example.VidobuProje.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/users")
    @Operation(summary = "Get all users", 
               description = "Retrieve all users with their department information")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = User.class),
                 examples = @ExampleObject(value = """
                 [
                   {
                     "id": 1,
                     "username": "john_doe",
                     "password": "$2a$10$...",
                     "department": {
                       "id": 1,
                       "name": "IT Department",
                       "description": "Information Technology Department"
                     }
                   }
                 ]""")))
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @PutMapping("/update{id}")
    @Operation(summary = "Update user", 
               description = "Update user information by ID")
    @ApiResponse(responseCode = "200", description = "User updated successfully",
                 content = @Content(mediaType = "text/plain",
                 examples = @ExampleObject(value = "User updated successfully!")))
    @ApiResponse(responseCode = "404", description = "User not found")
    public String updateUser(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User update details",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(value = """
                    {
                      "username": "john_doe_updated",
                      "password": "newpassword123",
                      "department": {
                        "id": 2,
                        "name": "HR Department",
                        "description": "Human Resources Department"
                      }
                    }""")
                )
            )
            @RequestBody User user){
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
    @Operation(summary = "Delete user", 
               description = "Delete user by ID")
    @ApiResponse(responseCode = "200", description = "User deleted successfully",
                 content = @Content(mediaType = "text/plain",
                 examples = @ExampleObject(value = "User deleted successfully!")))
    @ApiResponse(responseCode = "404", description = "User not found")
    public String deleteUser(@PathVariable Long id){
        if(!userRepository.existsById(id)){
            return "User not found !";
        }

        userRepository.deleteById(id);
        return "User deleted successfully !";
    }

}
