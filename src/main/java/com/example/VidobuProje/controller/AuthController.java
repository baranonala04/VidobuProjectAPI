package com.example.VidobuProje.controller;


import com.example.VidobuProje.entity.Department;
import com.example.VidobuProje.entity.User;
import com.example.VidobuProje.repository.DepartmentRepository;
import com.example.VidobuProje.repository.UserRepository;
import com.example.VidobuProje.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtils;

    @PostMapping("/signin")
    @Operation(summary = "User login", 
               description = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful",
                 content = @Content(mediaType = "text/plain",
                 examples = @ExampleObject(value = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")))
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public String AuthenticateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User login credentials",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(value = """
                    {
                      "username": "john_doe",
                      "password": "password123"
                    }""")
                )
            )
            @RequestBody User user){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtils.generateToken(userDetails.getUsername());
    }

    @PostMapping("/signup")
    @Operation(summary = "User registration", 
               description = "Register a new user with username, password and department")
    @ApiResponse(responseCode = "200", description = "User registered successfully",
                 content = @Content(mediaType = "text/plain",
                 examples = @ExampleObject(value = "User registered successfully")))
    @ApiResponse(responseCode = "400", description = "Username is already taken")
    public String registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User registration details",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class),
                    examples = @ExampleObject(value = """
                    {
                      "username": "jane_doe",
                      "password": "password123",
                      "department": {
                        "name": "IT Department",
                        "description": "Information Technology Department"
                      }
                    }""")
                )
            )
            @RequestBody User user){
        if (userRepository.existsByUsername(user.getUsername())){
            return "Username is already taken";
        }
        

        Department department = user.getDepartment();
        if (department != null) {
            if (department.getId() != null) {
                department = departmentRepository.findById(department.getId()).orElse(null);
            } 
            else if (department.getName() != null && !department.getName().trim().isEmpty()) {
                final String departmentName = department.getName();
                final String departmentDescription = department.getDescription();
                department = departmentRepository.findByName(departmentName)
                    .orElseGet(() -> {

                        Department newDepartment = new Department();
                        newDepartment.setName(departmentName);
                        newDepartment.setDescription(departmentDescription != null ? 
                            departmentDescription : "Auto-created department");
                        return departmentRepository.save(newDepartment);
                    });
            }
        }
        
        User newUser = new User(null, user.getUsername(), passwordEncoder.encode(user.getPassword()), department);
        userRepository.save(newUser);
        return "User registered successfully";
    }

}
