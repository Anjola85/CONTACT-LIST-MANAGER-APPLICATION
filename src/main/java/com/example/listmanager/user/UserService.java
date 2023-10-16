package com.example.listmanager.user;

import com.example.listmanager.configModel.BaseService;
import com.example.listmanager.contact.ContactDto;
import com.example.listmanager.contact.ContactService;
import com.example.listmanager.util.dto.ServiceResult;
import com.example.listmanager.util.helper.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements BaseService<UserDto>, UserDetailsService {
    private final String USER_CREATED_MESSAGE = "User added successfully";
    private final String USER_FAILED_MESSAGE = "Unable to add user";
    private final String USER_EXISTS_MESSAGE = "User already exists";

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProcessor userProcessor;
    @Autowired
    private ContactService contactService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ServiceResult create(UserDto userDto) {


        // validate credentials
        ServiceResult BAD_REQUEST = validateUserCred(userDto);
        if (BAD_REQUEST != null)
            return BAD_REQUEST;

        // encrypt password here
        String rawPassword = userDto.getPassword();
        String encryptedPassword = passwordEncoder.encode(rawPassword);

        userDto.setPassword(encryptedPassword);

        User newUser = userProcessor.mapUserInfoToEntity(userDto);

        Optional<User> userExists = userRepository.findByUsername(userDto.getUsername());

        if(userExists.isPresent())
            return new ServiceResult(HttpStatus.CONFLICT, "Username already exists");

        User createduser = userRepository.save(newUser);
        userDto = userProcessor.mapUserInfoToDto(createduser);

        return new ServiceResult(HttpStatus.CREATED, USER_CREATED_MESSAGE, userDto);
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
        boolean isPasswordMatch = passwordEncoder.matches(userDto.getPassword(), user.get().getPassword());

        if(!isPasswordMatch)
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
    public ServiceResult<UserDto> update(UserDto dto) {
        return null;
    }

    @Override
    public ServiceResult<UserDto> delete(UUID id) {

        Optional<User> existingUser = userRepository.findById(id);

        if (existingUser.isEmpty())
            return new ServiceResult(HttpStatus.NOT_FOUND, "User not found");


        // Delete all contacts associated with the user
        ServiceResult<List<ContactDto>> resp = contactService.findContactByUserId(id, null, null, null, null);
        List<ContactDto> userContacts = resp.getData().get(0);
        userContacts.forEach(contact -> contactService.delete(UUID.fromString(contact.getId())));

        // Delete the user
        userRepository.deleteById(id);

        return new ServiceResult(HttpStatus.OK, "User, contacts, and notes deleted successfully");
    }

    public User changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        user.setPassword(user.getPassword());

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        try {
            return this.userRepository.findByUsername(username);
        } catch(Exception e) {
            throw e;
        }
    }

    private static ServiceResult validateUserCred(UserDto userDto) {
        if(userDto.getUsername() == null || userDto.getUsername().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Username is required");
        if(userDto.getPassword() == null || userDto.getPassword().isEmpty())
            return new ServiceResult(HttpStatus.BAD_REQUEST, "Password is required");
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty())
            throw new UsernameNotFoundException("No user found with username: " + username);

        return new CustomUserDetails(user.get());
    }

    public ServiceResult getManagers(Integer pageNumber, Integer pageSize, String sortProperty) {
        try {
            Pageable pageable;

            if(sortProperty != null)
                pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, sortProperty);
            else
                pageable = PageRequest.of(pageNumber, pageSize);

            Page<User> managers = this.userRepository.getAllManagers(pageable);

            if(managers.hasContent()) {
                List<User> managersList = managers.get().toList();
                return new ServiceResult(HttpStatus.OK, String.format("Got back %d result(s)", managersList.size()), managersList);
            }

            return new ServiceResult(HttpStatus.OK, "Zero records");
        } catch(Exception e) {
            logger.debug(String.format("Something went wrong in %s or %s", this.getClass().getName(), this.userRepository.getClass().getName()));
            throw e;
        }
    }
}
