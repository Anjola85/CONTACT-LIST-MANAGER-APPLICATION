package com.example.listmanager.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            // Validate username
            if (user.getUsername() == null || user.getUsername().isBlank()) {
                return ResponseEntity.badRequest().body("Invalid username");
            }

            // Validate password
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                return ResponseEntity.badRequest().body("Invalid password");
            }

            User createdUser = this.userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed");
        }
    }

    @GetMapping("/test")
    public String test() {
        return "it works";
    }
}
