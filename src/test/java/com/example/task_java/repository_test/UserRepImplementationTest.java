package com.example.task_java.repository_test;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.User;
import com.example.task_java.repository.implementations.UserRepImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepImplementationTest {

    private UserRepImplementation userRep;

    @BeforeEach
    void setUp() {
        userRep = new UserRepImplementation();
    }

    @Test
    void saveUser_ShouldAssignIdAndSave() {
        User user = User.builder()
                .email("test@example.com")
                .build();

        User savedUser = userRep.save(user);

        assertNotNull(savedUser.getUserId());
        assertEquals("test@example.com", savedUser.getEmail());

        List<User> allUsers = userRep.findAll();
        assertEquals(1, allUsers.size());
        assertEquals(savedUser, allUsers.get(0));
    }

    @Test
    void saveUser_Null_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userRep.save(null);
        });
        assertEquals("User can't be null", ex.getMessage());
    }

    @Test
    void save_DuplicateEmail_ThrowsDoubleRecordException() {
        User user1 = User.builder()
                .email("dup@example.com")
                .build();
        userRep.save(user1);

        User user2 = User.builder()
                .email("DUP@example.com") // проверка игнорирования регистра
                .build();

        DoubleRecordException ex = assertThrows(DoubleRecordException.class, () -> {
            userRep.save(user2);
        });
        assertEquals("User with this email already exists.", ex.getMessage());
    }

    @Test
    void findUserById_ShouldReturnIfExists() {
        User user = User.builder()
                .email("findme@example.com")
                .build();
        userRep.save(user);

        Optional<User> found = userRep.findById(user.getUserId());
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    @Test
    void findById_ShouldReturnEmptyIfNotFound() {
        Optional<User> found = userRep.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void deleteById_ShouldDeleteUserIfExists() {
        User user = User.builder()
                .email("todelete@example.com")
                .build();
        userRep.save(user);

        // Метод не возвращает результат, просто вызываем
        userRep.deleteById(user.getUserId());

        // Проверяем, что пользователь удалён
        assertTrue(userRep.findById(user.getUserId()).isEmpty());
    }

    @Test
    void deleteById_ShouldThrowExceptionIfUserNotFound() {
        // Проверяем, что при удалении несуществующего пользователя выбрасывается исключение
        assertThrows(RecordNotFoundException.class, () -> {
            userRep.deleteById(12345L);
        });
    }

//    @Test
//    void deleteById_ShouldReturnTrueIfDeleted() {
//        User user = User.builder()
//                .email("todelete@example.com")
//                .build();
//        userRep.save(user);
//
//        boolean deleted = userRep.deleteById(user.getUserId());
//        assertTrue(deleted);
//
//        assertTrue(userRep.findById(user.getUserId()).isEmpty());
//    }
//
//    @Test
//    void deleteById_ShouldReturnFalseIfNotFound() {
//        boolean deleted = userRep.deleteById(12345L);
//        assertFalse(deleted);
//    }

    @Test
    void existsById_ShouldReturnTrueIfExists() {
        User user = User.builder()
                .email("existsid@example.com")
                .build();
        userRep.save(user);

        assertTrue(userRep.existsById(user.getUserId()));
    }

    @Test
    void existsById_ShouldReturnFalseIfNotExists() {
        assertFalse(userRep.existsById(999L));
    }

    @Test
    void existsByEmail_ShouldReturnTrueIfExistsIgnoreCase() {
        User user = User.builder()
                .email("existsemail@example.com")
                .build();
        userRep.save(user);

        assertTrue(userRep.existsByEmailIgnoreCase("existsemail@example.com"));
        assertTrue(userRep.existsByEmailIgnoreCase("EXISTSEMAIL@example.com")); // проверка регистра
    }

    @Test
    void existsByEmail_ShouldReturnFalseIfNotExistsIgnoreCase() {
        assertFalse(userRep.existsByEmailIgnoreCase("notexists@example.com"));
    }

    @Test
    void findByEmail_IgnoreCase_ShouldReturnUserIfExists() {
        User user = User.builder()
                .email("findbyemail@example.com")
                .build();
        userRep.save(user);

        Optional<User> found = userRep.findByEmailIgnoreCase("findbyemail@example.com");
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    @Test
    void findByEmail_IgnoreCase_ShouldReturnEmptyIfNotExists() {
        Optional<User> found = userRep.findByEmailIgnoreCase("missing@example.com");
        assertTrue(found.isEmpty());
    }
}
