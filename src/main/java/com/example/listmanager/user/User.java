package com.example.listmanager.user;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column
    private String username;
    @Column
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Column
    private String dateCreated;
    @Column
    private String dateUpdated;

    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


    public User() {}

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        dateCreated = now.format(ISO_DATE_TIME_FORMATTER);
        dateUpdated = now.format(ISO_DATE_TIME_FORMATTER);
    }

    @PreUpdate
    protected void onUpdate() {
        dateUpdated = LocalDateTime.now().format(ISO_DATE_TIME_FORMATTER);
    }
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public boolean isAdmin() {
        return this.getRole().equals(UserRole.ADMIN);
    }

}
