package ru.edu.project.foundation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ru.edu.project.mediator.interfaces.UserService;

/**
 * Создаёт стартовую учётную запись библиотекаря при запуске приложения,
 * если её ещё нет. Пароль хешируется автоматически (BCrypt), поэтому ручная
 * вставка в БД больше не нужна. Управляется свойствами app.admin.* .
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserService userService;

    @Value("${app.admin.enabled:false}")
    private boolean enabled;

    @Value("${app.admin.email:}")
    private String email;

    @Value("${app.admin.password:}")
    private String password;

    @Value("${app.admin.name:Администратор}")
    private String name;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (!enabled || email.isBlank() || password.isBlank()) {
            return;
        }
        if (userService.getUserByEmail(email).isPresent()) {
            log.info("Стартовая учётная запись библиотекаря {} уже существует — пропускаю создание", email);
            return;
        }
        userService.registerLibrarian(email, password, name);
        log.warn("Создана стартовая учётная запись БИБЛИОТЕКАРЯ: {} — ОБЯЗАТЕЛЬНО смените пароль!", email);
    }
}
