package ru.edu.project.mediator.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.edu.project.entity.User;
import ru.edu.project.foundation.repositories.UserRepository;
import ru.edu.project.mediator.exceptions.DuplicateResourceException;
import ru.edu.project.mediator.exceptions.ResourceNotFoundException;
import ru.edu.project.mediator.interfaces.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerUser(String email, String password, String name) {
        return createUser(email, password, name, "USER");
    }

    @Override
    @Transactional
    public User registerLibrarian(String email, String password, String name) {
        return createUser(email, password, name, "LIBRARIAN");
    }

    private User createUser(String email, String password, String name, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("Пользователь с таким email уже существует!");
        }
        // Пароль хранится только в виде BCrypt-хеша
        User user = new User(email, passwordEncoder.encode(password), name);
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> authenticate(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User updateUserName(Long id, String newName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        
        // Вызов бизнес-метода из слоя Entity
        user.changeName(newName); 
        
        return userRepository.save(user);
    }
}