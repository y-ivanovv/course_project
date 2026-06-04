package ru.edu.project.mediator.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.edu.project.entity.User;
import ru.edu.project.foundation.repositories.UserRepository;
import ru.edu.project.mediator.interfaces.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User registerUser(String email, String password, String name) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует!");
        }
        // В реальном проекте тут будет BCrypt, сейчас для простоты пишем хэш напрямую
        User user = new User(email, password, name);
        return userRepository.save(user);
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
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        
        // Вызов бизнес-метода из слоя Entity
        user.changeName(newName); 
        
        return userRepository.save(user);
    }
}