package ru.edu.project.mediator.exceptions;

/**
 * Запрошенный ресурс не найден (HTTP 404).
 * Наследуется от IllegalArgumentException для обратной совместимости с существующим кодом и тестами.
 */
public class ResourceNotFoundException extends IllegalArgumentException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
