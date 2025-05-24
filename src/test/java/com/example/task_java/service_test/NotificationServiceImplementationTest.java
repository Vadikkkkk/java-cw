package com.example.task_java.service_test;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Notification;
import com.example.task_java.repository.NotificationRep;
import com.example.task_java.repository.UserRep;
import com.example.task_java.service.implementations.NotificationServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceImplementationTest {

    private NotificationRep notificationRep;
    private UserRep userRep;
    private NotificationServiceImplementation notificationService;

    @BeforeEach
    void setUp() {
        notificationRep = mock(NotificationRep.class);
        userRep = mock(UserRep.class);
        notificationService = new NotificationServiceImplementation(notificationRep, userRep);
    }

    @Test
    void getAllUsersNotification_UserExists_ReturnsNotifications() throws RecordNotFoundException {
        long userId = 1L;
        List<Notification> notifications = List.of(new Notification(1L, 1L, userId, "Test", LocalDateTime.now(), false));

        when(userRep.existsById(userId)).thenReturn(true);
        when(notificationRep.findByUserId(userId)).thenReturn(notifications);

        List<Notification> result = notificationService.getAllUsersNotification(userId);

        assertEquals(notifications, result);
        verify(userRep).existsById(userId);
        verify(notificationRep).findByUserId(userId);
    }

    @Test
    void getAllUsersNotification_UserDoesNotExist_ThrowsException() {
        long userId = 1L;
        when(userRep.existsById(userId)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> notificationService.getAllUsersNotification(userId));
    }

    @Test
    void getUsersPendingNotifications_UserExists_ReturnsPendingAndMarksAsRead() throws RecordNotFoundException {
        long userId = 1L;
        Notification n1 = new Notification(1L, 1L, userId, "Unread", LocalDateTime.now(), false);
        Notification n2 = new Notification(2L, 1L, userId, "Read", LocalDateTime.now(), true);

        when(userRep.existsById(userId)).thenReturn(true);
        when(notificationRep.findByUserId(userId)).thenReturn(Arrays.asList(n1, n2));

        List<Notification> result = notificationService.getUsersPendingNotifications(userId);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsRead());
        verify(notificationRep).markAllAsRead(userId);
    }

    @Test
    void getUsersPendingNotifications_UserDoesNotExist_ThrowsException() {
        long userId = 1L;
        when(userRep.existsById(userId)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> notificationService.getUsersPendingNotifications(userId));
    }

    @Test
    void addNotification_ValidNotification_ReturnsSavedNotification() throws DoubleRecordException, RecordNotFoundException {
        Notification notification = new Notification(null, 1L, 1L, "Test", LocalDateTime.now(), false);

        when(userRep.existsById(1L)).thenReturn(true);
        when(notificationRep.save(notification)).thenReturn(notification);

        Notification result = notificationService.addNotification(notification);

        assertEquals(notification, result);
        verify(notificationRep).save(notification);
    }

    @Test
    void addNotification_NullNotification_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> notificationService.addNotification(null));
    }

    @Test
    void addNotification_UserDoesNotExist_ThrowsRecordNotFoundException() {
        Notification notification = new Notification(null, 1L, 1L, "Test", LocalDateTime.now(), false);
        when(userRep.existsById(1L)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> notificationService.addNotification(notification));
    }

    @Test
    void addNotification_DoubleRecordException_ThrowsRuntimeException() throws DoubleRecordException {
        Notification notification = new Notification(null, 1L, 1L, "Test", LocalDateTime.now(), false);

        when(userRep.existsById(1L)).thenReturn(true);
        when(notificationRep.save(notification)).thenThrow(new DoubleRecordException("Duplicate"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> notificationService.addNotification(notification));
        assertTrue(ex.getMessage().contains("Notification already exists!"));
    }
}
