package com.example.task_java.repository_test;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Notification;
import com.example.task_java.repository.implementations.NotificationRepImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationRepImplementationTest {

    private NotificationRepImplementation notificationRepo;

    private Notification sampleNotification;

    @BeforeEach
    void setUp() {
        notificationRepo = new NotificationRepImplementation();

        sampleNotification = Notification.builder()
                .notificationId(null)
                .taskId(1L)
                .userId(1L)
                .text("Test notification")
                .date(LocalDateTime.now())
                .isRead(false)
                .build();
    }

    // Позитивные тесты

    @Test
    void saveNotification_NewNotification_AssignsIdAndSaves() throws DoubleRecordException {
        Notification saved = notificationRepo.saveNotification(sampleNotification);
        assertNotNull(saved.getNotificationId());
        List<Notification> notifications = notificationRepo.findNotificationsByUserId(1L);
        assertEquals(1, notifications.size());
        assertEquals("Test notification", notifications.get(0).getText());
    }

    @Test
    void saveNotification_UpdateExistingNotification_UpdatesSuccessfully() throws DoubleRecordException {
        Notification saved = notificationRepo.saveNotification(sampleNotification);
        Notification updated = Notification.builder()
                .notificationId(saved.getNotificationId())
                .taskId(saved.getTaskId())
                .userId(saved.getUserId())
                .text("Updated text")
                .date(saved.getDate())
                .isRead(true)
                .build();

        Notification result = notificationRepo.saveNotification(updated);
        assertEquals(saved.getNotificationId(), result.getNotificationId());
        assertEquals("Updated text", result.getText());

        List<Notification> notifications = notificationRepo.findNotificationsByUserId(1L);
        assertEquals(1, notifications.size());
        assertTrue(notifications.get(0).getIsRead());
    }

    @Test
    void findNotificationsByUserId_ReturnsOnlyUserNotifications() throws DoubleRecordException {
        notificationRepo.saveNotification(sampleNotification);
        Notification otherUserNotification = Notification.builder()
                .taskId(2L)
                .userId(2L)
                .text("Other user notification")
                .date(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationRepo.saveNotification(otherUserNotification);

        List<Notification> user1Notifications = notificationRepo.findNotificationsByUserId(1L);
        List<Notification> user2Notifications = notificationRepo.findNotificationsByUserId(2L);

        assertEquals(1, user1Notifications.size());
        assertEquals(1, user2Notifications.size());
        assertEquals("Test notification", user1Notifications.get(0).getText());
        assertEquals("Other user notification", user2Notifications.get(0).getText());
    }

    @Test
    void markAllAsRead_WithExistingNotifications_MarksThemAsRead() throws DoubleRecordException, RecordNotFoundException {
        notificationRepo.saveNotification(sampleNotification);
        notificationRepo.markAllAsRead(1L);

        List<Notification> notifications = notificationRepo.findNotificationsByUserId(1L);
        assertTrue(notifications.stream().allMatch(Notification::getIsRead));
    }

    // Негативные тесты

    @Test
    void saveNotification_UpdateNonExistingNotification_ThrowsIllegalArgumentException() {
        Notification nonExistingNotification = Notification.builder()
                .notificationId(999L)
                .taskId(1L)
                .userId(1L)
                .text("Non-existing")
                .date(LocalDateTime.now())
                .isRead(false)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            notificationRepo.saveNotification(nonExistingNotification);
        });

        assertTrue(ex.getMessage().contains("cannot update non-existing notification"));
    }

    @Test
    void markAllAsRead_NoNotificationsForUser_ThrowsRecordNotFoundException() {
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> {
            notificationRepo.markAllAsRead(42L);
        });
        assertTrue(ex.getMessage().contains("No notifications found for user"));
    }
}
