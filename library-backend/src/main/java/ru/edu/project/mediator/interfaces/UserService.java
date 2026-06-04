package ru.edu.project.mediator.interfaces;

import java.util.Optional;

import ru.edu.project.entity.User;

public interface UserService {
    User registerUser(String email, String password, String name);

    /**
     * Создаёт пользователя с ролью библиотекаря (LIBRARIAN).
     */
    User registerLibrarian(String email, String password, String name);

    /**
     * Проверяет учётные данные. Возвращает пользователя при совпадении пароля, иначе пустой Optional.
     */
    Optional<User> authenticate(String email, String rawPassword);

    Optional<User> getUserByEmail(String email);
    Optional<User> getUserById(Long id);
    User updateUserName(Long id, String newName);
}