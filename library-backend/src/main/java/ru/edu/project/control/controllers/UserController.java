package ru.edu.project.control.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import ru.edu.project.control.dto.UserLoginRequest;
import ru.edu.project.control.dto.UserRegisterRequest;
import ru.edu.project.control.dto.UserResponse;
import ru.edu.project.entity.User;
import ru.edu.project.foundation.security.AuthInterceptor;
import ru.edu.project.mediator.interfaces.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        User user = userService.registerUser(request.getEmail(), request.getPassword(), request.getName());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    // Авторизация (вход): проверка учётных данных и запись в серверную сессию
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginRequest request, HttpSession session) {
        return userService.authenticate(request.getEmail(), request.getPassword())
                .map(user -> {
                    session.setAttribute(AuthInterceptor.SESSION_USER, user.getId());
                    session.setAttribute(AuthInterceptor.SESSION_ROLE, user.getRole());
                    return ResponseEntity.ok(UserResponse.from(user));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // Выход из системы (сброс сессии)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(UserResponse.from(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
