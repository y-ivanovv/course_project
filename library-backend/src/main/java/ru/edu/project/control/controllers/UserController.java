package ru.edu.project.control.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import ru.edu.project.entity.User;
import ru.edu.project.mediator.interfaces.UserService;

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
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
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterRequest request) {
        User user = userService.registerUser(request.getEmail(), request.getPassword(), request.getName());
        return ResponseEntity.ok(user);
    }

    // Авторизация (Вход) с использованием твоего бизнес-метода checkPassword
    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody UserLoginRequest request, HttpSession session) {
        Optional<User> userOpt = userService.getUserByEmail(request.getEmail());
        
        if (userOpt.isPresent() && userOpt.get().checkPassword(request.getPassword())) {
            User user = userOpt.get();
            
            // Записываем ID и Роль в сессию на сервере
            session.setAttribute("currentUser", user.getId());
            session.setAttribute("userRole", user.getRole());
            
            return ResponseEntity.ok(user);
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Если не совпал пароль/email
    }

    // Выход из системы (сброс сессии)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}