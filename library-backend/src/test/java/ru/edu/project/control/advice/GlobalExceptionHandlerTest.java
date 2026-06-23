package ru.edu.project.control.advice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ru.edu.project.control.dto.ErrorResponse;
import ru.edu.project.mediator.exceptions.DuplicateResourceException;
import ru.edu.project.mediator.exceptions.ForbiddenOperationException;
import ru.edu.project.mediator.exceptions.ResourceNotFoundException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFound_maps_to_404() {
        ResponseEntity<ErrorResponse> r = handler.handleNotFound(new ResourceNotFoundException("Книга не найдена"));
        assertEquals(HttpStatus.NOT_FOUND.value(), r.getStatusCode().value());
        assertEquals("Книга не найдена", r.getBody().getMessage());
    }

    @Test
    void duplicate_maps_to_409() {
        ResponseEntity<ErrorResponse> r = handler.handleDuplicate(new DuplicateResourceException("ISBN существует"));
        assertEquals(HttpStatus.CONFLICT.value(), r.getStatusCode().value());
    }

    @Test
    void forbidden_maps_to_403() {
        ResponseEntity<ErrorResponse> r = handler.handleForbidden(new ForbiddenOperationException("Нет прав"));
        assertEquals(HttpStatus.FORBIDDEN.value(), r.getStatusCode().value());
    }

    @Test
    void illegalState_maps_to_409() {
        ResponseEntity<ErrorResponse> r = handler.handleIllegalState(new IllegalStateException("Книга уже выдана"));
        assertEquals(HttpStatus.CONFLICT.value(), r.getStatusCode().value());
    }

    @Test
    void illegalArgument_maps_to_400() {
        ResponseEntity<ErrorResponse> r = handler.handleIllegalArgument(new IllegalArgumentException("Плохой аргумент"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), r.getStatusCode().value());
    }

    @Test
    void unexpected_maps_to_500() {
        ResponseEntity<ErrorResponse> r = handler.handleUnexpected(new RuntimeException("boom"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), r.getStatusCode().value());
    }

    @Test
    void validation_maps_to_400_with_field_errors() {
        BindingResult br = mock(BindingResult.class);
        when(br.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "email", "Некорректный email")));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mock(MethodParameter.class), br);

        ResponseEntity<ErrorResponse> r = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), r.getStatusCode().value());
        assertNotNull(r.getBody().getFieldErrors());
        assertEquals("Некорректный email", r.getBody().getFieldErrors().get("email"));
    }
}
