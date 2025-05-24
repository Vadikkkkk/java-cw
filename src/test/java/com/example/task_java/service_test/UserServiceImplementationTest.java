package com.example.task_java.service_test;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.User;
import com.example.task_java.repository.UserRep;
import com.example.task_java.service.implementations.UserServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplementationTest {

    @Mock
    private UserRep userRep;

    @InjectMocks
    private UserServiceImplementation userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleUser = User.builder()
                .userId(1L)
                .email("test@example.com")
                .build();
    }

    // Позитивные тесты

    @Test
    void findAllUsers_ReturnsListOfUsers() {
        when(userRep.findAllUsers()).thenReturn(List.of(sampleUser));

        List<User> users = userService.findAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("test@example.com", users.get(0).getEmail());
        verify(userRep, times(1)).findAllUsers();
    }

    @Test
    void registerUser_ValidUser_SavesAndReturnsUser() throws DoubleRecordException {
        when(userRep.saveUser(sampleUser)).thenReturn(sampleUser);

        User registered = userService.registerUser(sampleUser);

        assertNotNull(registered);
        assertEquals(sampleUser.getUserId(), registered.getUserId());
        verify(userRep, times(1)).saveUser(sampleUser);
    }

    @Test
    void findUserById_ExistingUser_ReturnsUser() throws RecordNotFoundException {
        when(userRep.findUserById(1L)).thenReturn(Optional.of(sampleUser));

        User found = userService.findUserById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getUserId());
        verify(userRep, times(1)).findUserById(1L);
    }

    @Test
    void loginUser_ExistingEmail_ReturnsUser() throws RecordNotFoundException {
        when(userRep.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));

        User loggedIn = userService.loginUser("test@example.com");

        assertNotNull(loggedIn);
        assertEquals("test@example.com", loggedIn.getEmail());
        verify(userRep, times(1)).findByEmail("test@example.com");
    }

    @Test
    void deleteUser_ExistingUser_DeletesSuccessfully() {
        when(userRep.existsById(1L)).thenReturn(true);
        when(userRep.deleteUser(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRep, times(1)).existsById(1L);
        verify(userRep, times(1)).deleteUser(1L);
    }

    // Негативные тесты

    @Test
    void registerUser_NullUser_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(null);
        });

        assertEquals("Invalid user! Can't be null", ex.getMessage());
        verifyNoInteractions(userRep);
    }

    @Test
    void findUserById_UserNotFound_ThrowsRecordNotFoundException() {
        when(userRep.findUserById(2L)).thenReturn(Optional.empty());

        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> {
            userService.findUserById(2L);
        });

        assertTrue(ex.getMessage().contains("User 2 not found"));
        verify(userRep, times(1)).findUserById(2L);
    }

    @Test
    void loginUser_EmailNotFound_ThrowsRecordNotFoundException() {
        when(userRep.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> {
            userService.loginUser("unknown@example.com");
        });

        assertTrue(ex.getMessage().contains("User with email 'unknown@example.com' not found."));
        verify(userRep, times(1)).findByEmail("unknown@example.com");
    }

    @Test
    void deleteUser_UserNotFound_ThrowsRecordNotFoundException() {
        when(userRep.existsById(5L)).thenReturn(false);

        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> {
            userService.deleteUser(5L);
        });

        assertTrue(ex.getMessage().contains("User with id 5 not found"));
        verify(userRep, times(1)).existsById(5L);
        verify(userRep, never()).deleteUser(anyLong());
    }
}
