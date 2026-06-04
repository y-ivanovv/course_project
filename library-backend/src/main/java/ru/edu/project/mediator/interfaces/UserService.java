package ru.edu.project.mediator.interfaces;

import java.util.Optional;

import ru.edu.project.entity.User;

public interface UserService {
    User registerUser(String email, String password, String name);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserById(Long id);
    User updateUserName(Long id, String newName);
}