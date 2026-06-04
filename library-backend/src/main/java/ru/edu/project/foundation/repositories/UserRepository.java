package ru.edu.project.foundation.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.edu.project.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Поиск пользователя по его уникальному email.
     * Spring Data JPA автоматически сгенерирует SQL-запрос на основе имени метода.
     */
    Optional<User> findByEmail(String email);
}