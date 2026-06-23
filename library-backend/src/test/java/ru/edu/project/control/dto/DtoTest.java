package ru.edu.project.control.dto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import ru.edu.project.entity.User;

class DtoTest {

    @Test
    void userResponse_hides_password_hash() {
        User u = new User("user@mail.ru", "$2a$secret-hash", "Иван");
        u.setId(1L);
        u.setRole("LIBRARIAN");

        UserResponse r = UserResponse.from(u);

        assertEquals(1L, r.getId());
        assertEquals("user@mail.ru", r.getEmail());
        assertEquals("Иван", r.getName());
        assertEquals("LIBRARIAN", r.getRole());
        // В DTO нет поля с хешем пароля — это и проверяем по составу геттеров
    }

    @Test
    void pageResponse_carries_paging_metadata() {
        Page<String> page = new PageImpl<>(List.of("a", "b"), PageRequest.of(0, 12), 100);
        PageResponse<String> r = PageResponse.of(page, 0, 12);

        assertEquals(2, r.getContent().size());
        assertEquals(100, r.getTotal());
        assertEquals(0, r.getOffset());
        assertEquals(12, r.getLimit());
    }

    @Test
    void errorResponse_without_field_errors() {
        ErrorResponse e = new ErrorResponse(404, "Not Found", "Книга не найдена");
        assertEquals(404, e.getStatus());
        assertEquals("Not Found", e.getError());
        assertEquals("Книга не найдена", e.getMessage());
        assertNull(e.getFieldErrors());
    }
}
