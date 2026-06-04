package ru.edu.project.control.dto;

import java.time.LocalDateTime;

import ru.edu.project.entity.User;

/**
 * Ответ API о пользователе. НЕ содержит хеш пароля, в отличие от сущности User.
 */
public class UserResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String role;
    private final LocalDateTime createdAt;

    public UserResponse(Long id, String email, String name, String role, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt());
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
