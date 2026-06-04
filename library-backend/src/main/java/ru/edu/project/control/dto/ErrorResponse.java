package ru.edu.project.control.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Единый формат тела ответа при ошибках.
 */
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String message;
    private final Map<String, String> fieldErrors;

    public ErrorResponse(int status, String error, String message, Map<String, String> fieldErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public ErrorResponse(int status, String error, String message) {
        this(status, error, message, null);
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
}
