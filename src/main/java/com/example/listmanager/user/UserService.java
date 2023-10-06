package com.example.listmanager.user;

import com.example.listmanager.ConfigModel.BaseService;
import com.example.listmanager.util.dto.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements BaseService<UserDto> {
    private final String USER_CREATED_MESSAGE = "User added successfully";
    private final String USER_FAILED_MESSAGE = "Unable to add user";
    private final String USER_EXISTS_MESSAGE = "User already exists";
    private final UserRepository userRepository;
    private UserProcessor userProcessor;
    private ServiceResult<UserDto> response;

    @Autowired
    public UserService(UserRepository userRepository, UserProcessor userProcessor, ServiceResult response) {
        this.userRepository = userRepository;
        this.userProcessor = userProcessor;
        this.response = response;
    }

    public ServiceResult create(UserDto userDto) {

        // validate credentials
        ServiceResult BAD_REQUEST = validateUserCred(userDto);
        if (BAD_REQUEST != null)
            return BAD_REQUEST;

        User newUser = userProcessor.mapUserInfoToEntity(userDto);

        Optional<User> userExists = userRepository.findByUsername(userDto.getUsername());

        if(userExists.isPresent())
            return new ServiceResult(HttpStatus.CONFLICT, "Username already exists");

        User createduser = userRepository.save(newUser);
        userDto = userProcessor.mapUserInfoToDto(createduser);
        return response.setStatus(HttpStatus.CREATED).setMessage(USER_CREATED_MESSAGE).setData(userDto);
    }

    public ServiceResult login(UserDto userDto) {

        // validate credentials
        ServiceResult BAD_REQUEST = validateUserCred(userDto);
        if (BAD_REQUEST != null)
            return BAD_REQUEST;

        Optional<User> user = userRepository.findByUsername(userDto.getUsername());

        if(!user.isPresent())
            return new ServiceResult<>(HttpStatus.NOT_FOUND, "User does not exist");

        // compare passwords
        if(!(userDto.getPassword().equals(user.get().getPassword())))
            return new ServiceResult(HttpStatus.UNAUTHORIZED, "Username or password is wrong");

        // reassign
        userDto = userProcessor.mapUserInfoToDto(user.get());

        return new ServiceResult(HttpStatus.OK, "Logged in successfully", userDto);
    }

    @Override
    public ServiceResult<UserDto> findAll() {
        return null;
    }

    @Override
    public ServiceResult<UserDto> findById(UUID id) {
        Optional<User> respEntity =this.userRepository.findById(id);

        if(respEntity.isPresent()) {
            UserDto user = userProcessor.mapUserInfoToDto(respEntity.get());
            return new ServiceResult(HttpStatus.OK, "Got back user successfully", user);
        }

        return new ServiceResult(HttpStatus.NOT_FOUND, "User not found");
    }

    @Override
    public ServiceResult<UserDto> update(UUID id, UserDto dto) {
        return null;
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


    private static ServiceResult validateUserCred(UserDto userDto) {
        if(userDto.getUsername() == null || userDto.getUsername().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Username is required");
        if(userDto.getPassword() == null || userDto.getPassword().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Password is required");
        return null;
    }
}
