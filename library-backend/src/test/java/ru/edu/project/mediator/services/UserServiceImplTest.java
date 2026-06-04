package ru.edu.project.mediator.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.edu.project.entity.User;
import ru.edu.project.foundation.repositories.UserRepository;
import ru.edu.project.mediator.exceptions.DuplicateResourceException;
import ru.edu.project.mediator.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User("user@mail.ru", "$2a$hashed", "Иван");
    }

    // 1. Регистрация: пароль сохраняется в виде хеша, а не в открытом виде
    @Test
    void registerUser_HashesPassword() {
        when(userRepository.findByEmail("new@mail.ru")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain123")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.registerUser("new@mail.ru", "plain123", "Пётр");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("ENCODED", captor.getValue().getPasswordHash());
        assertFalse("plain123".equals(captor.getValue().getPasswordHash()));
    }

    // 1b. Регистрация библиотекаря: роль LIBRARIAN, пароль захеширован
    @Test
    void registerLibrarian_SetsLibrarianRole() {
        when(userRepository.findByEmail("lib@mail.ru")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain123")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.registerLibrarian("lib@mail.ru", "plain123", "Библиотекарь");

        assertEquals("LIBRARIAN", created.getRole());
        assertEquals("ENCODED", created.getPasswordHash());
    }

    // 2. Регистрация с уже занятым email
    @Test
    void registerUser_ThrowsException_WhenEmailExists() {
        when(userRepository.findByEmail("user@mail.ru")).thenReturn(Optional.of(existingUser));

        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> userService.registerUser("user@mail.ru", "plain123", "Иван"));

        assertEquals("Пользователь с таким email уже существует!", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // 3. Успешная аутентификация при совпадении пароля
    @Test
    void authenticate_Success() {
        when(userRepository.findByEmail("user@mail.ru")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("plain123", "$2a$hashed")).thenReturn(true);

        Optional<User> result = userService.authenticate("user@mail.ru", "plain123");

        assertTrue(result.isPresent());
        assertEquals("user@mail.ru", result.get().getEmail());
    }

    // 4. Неверный пароль — пустой результат
    @Test
    void authenticate_Fails_WhenWrongPassword() {
        when(userRepository.findByEmail("user@mail.ru")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong", "$2a$hashed")).thenReturn(false);

        assertTrue(userService.authenticate("user@mail.ru", "wrong").isEmpty());
    }

    // 5. Несуществующий email — пустой результат
    @Test
    void authenticate_Fails_WhenUserNotFound() {
        when(userRepository.findByEmail("ghost@mail.ru")).thenReturn(Optional.empty());

        assertTrue(userService.authenticate("ghost@mail.ru", "any").isEmpty());
    }

    // 6. Смена имени несуществующему пользователю
    @Test
    void updateUserName_ThrowsException_WhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUserName(99L, "Новое имя"));

        assertEquals("Пользователь не найден", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
