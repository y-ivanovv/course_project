package ru.edu.project.mediator.exceptions;

/**
 * Операция запрещена для текущего пользователя (HTTP 403).
 */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
