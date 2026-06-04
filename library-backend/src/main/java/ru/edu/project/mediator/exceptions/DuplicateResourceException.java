package ru.edu.project.mediator.exceptions;

/**
 * Попытка создать ресурс, который уже существует (HTTP 409).
 * Наследуется от IllegalArgumentException для обратной совместимости с существующим кодом и тестами.
 */
public class DuplicateResourceException extends IllegalArgumentException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
