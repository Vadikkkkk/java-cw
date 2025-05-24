package com.example.task_java.repository_test;

import com.example.task_java.exception.DoubleRecordException;
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
    void saveUser_ShouldAssignIdAndSaveUser() {
        User user = User.builder()
                .email("test@example.com")
                .build();

        User savedUser = userRep.saveUser(user);

        assertNotNull(savedUser.getUserId());
        assertEquals("test@example.com", savedUser.getEmail());

        List<User> allUsers = userRep.findAllUsers();
        assertEquals(1, allUsers.size());
        assertEquals(savedUser, allUsers.get(0));
    }

    @Test
    void saveUser_NullUser_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userRep.saveUser(null);
        });
        assertEquals("User can't be null", ex.getMessage());
    }

    @Test
    void saveUser_DuplicateEmail_ThrowsDoubleRecordException() {
        User user1 = User.builder()
                .email("dup@example.com")
                .build();
        userRep.saveUser(user1);

        User user2 = User.builder()
                .email("DUP@example.com") // проверка игнорирования регистра
                .build();

        DoubleRecordException ex = assertThrows(DoubleRecordException.class, () -> {
            userRep.saveUser(user2);
        });
        assertEquals("User with this email already exists.", ex.getMessage());
    }

    @Test
    void findUserById_ShouldReturnUserIfExists() {
        User user = User.builder()
                .email("findme@example.com")
                .build();
        userRep.saveUser(user);

        Optional<User> found = userRep.findUserById(user.getUserId());
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    @Test
    void findUserById_ShouldReturnEmptyIfNotFound() {
        Optional<User> found = userRep.findUserById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void deleteUser_ShouldReturnTrueIfDeleted() {
        User user = User.builder()
                .email("todelete@example.com")
                .build();
        userRep.saveUser(user);

        boolean deleted = userRep.deleteUser(user.getUserId());
        assertTrue(deleted);

        assertTrue(userRep.findUserById(user.getUserId()).isEmpty());
    }

    @Test
    void deleteUser_ShouldReturnFalseIfNotFound() {
        boolean deleted = userRep.deleteUser(12345L);
        assertFalse(deleted);
    }

    @Test
    void existsById_ShouldReturnTrueIfExists() {
        User user = User.builder()
                .email("existsid@example.com")
                .build();
        userRep.saveUser(user);

        assertTrue(userRep.existsById(user.getUserId()));
    }

    @Test
    void existsById_ShouldReturnFalseIfNotExists() {
        assertFalse(userRep.existsById(999L));
    }

    @Test
    void existsByEmail_ShouldReturnTrueIfExists() {
        User user = User.builder()
                .email("existsemail@example.com")
                .build();
        userRep.saveUser(user);

        assertTrue(userRep.existsByEmail("existsemail@example.com"));
        assertTrue(userRep.existsByEmail("EXISTSEMAIL@example.com")); // проверка регистра
    }

    @Test
    void existsByEmail_ShouldReturnFalseIfNotExists() {
        assertFalse(userRep.existsByEmail("notexists@example.com"));
    }

    @Test
    void findByEmail_ShouldReturnUserIfExists() {
        User user = User.builder()
                .email("findbyemail@example.com")
                .build();
        userRep.saveUser(user);

        Optional<User> found = userRep.findByEmail("findbyemail@example.com");
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    @Test
    void findByEmail_ShouldReturnEmptyIfNotExists() {
        Optional<User> found = userRep.findByEmail("missing@example.com");
        assertTrue(found.isEmpty());
    }
}
