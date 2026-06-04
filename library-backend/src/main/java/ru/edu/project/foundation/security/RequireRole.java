package ru.edu.project.foundation.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает эндпоинт как доступный только пользователям с указанной ролью.
 * Проверяется в {@link AuthInterceptor}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String value();
}
