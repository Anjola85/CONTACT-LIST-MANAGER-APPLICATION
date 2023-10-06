package com.example.listmanager.user;


import org.springframework.context.annotation.Configuration;

/**
 * This class handles the business use case mapping of user
 */
@Configuration
public class UserProcessor {

    /**
     * Maps the information from userDto to User Entity
     * @param user
     * @return
     */
    public User mapUserInfoToEntity(UserDto user) {
        User newUser = new User();
       newUser.setUsername(user.getUsername());
        // TODO: encoding the password before saving
//        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
       newUser.setPassword(user.getPassword());
       newUser.setRole(user.getUserRole());

       return newUser;
    }

    public UserDto mapUserInfoToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());

        // TODO: only show id and password to Admin - using Auth Role - Auth params
        if(true || user.isAdmin()) {
            userDto.setId(user.getId().toString());
            userDto.setPassword(user.getPassword());
        }
        userDto.setUserRole(user.getRole());
        userDto.setDateCreated(user.getDateCreated());
        userDto.setDateUpdated(user.getDateUpdated());
        return userDto;
    }
}
