package com.example.listmanager.user;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        // encoding the password before saving
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPassword(user.getPassword());

        if(user.isAdmin())
            user.setRole(UserRole.ADMIN);

        return userRepository.save(user);
    }

    public User changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

//        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
//            throw new IllegalArgumentException("Current password is incorrect");
//        }

//        String encodedNewPassword = passwordEncoder.encode(newPassword);
//        user.setPassword(encodedNewPassword);
        user.setPassword(user.getPassword());

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String userId, String username) {
        Optional<User> user = this.userRepository.findByUsername(userId);

        if(!user.get().isAdmin())
            throw new RuntimeException("Unauthorized: User is not authorized to perform this request.");

        return userRepository.findByUsername(username);
    }

    // TODO: method to delete a user
}
