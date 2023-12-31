package com.example.listmanager.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    @JsonProperty("id")
    private String id = null;
    @JsonProperty("username")
    private String username;
    @JsonProperty("role")
    private UserRole userRole = UserRole.MANAGER;
    @JsonProperty("password")
    private String password;
    @JsonProperty("dateCreated")
    private String dateCreated;
    @JsonProperty("dateUpdated")
    private String dateUpdated;
    @JsonProperty("isAdmin")
    private Boolean isAdmin = false;

    public UserDto() {}

    public UserDto(String username,String password) {
        this.setUsername(username);
        this.setPassword(password);
    }
}
