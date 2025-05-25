package com.example.task_java.service.implementations;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Notification;
import com.example.task_java.repository.NotificationRep;
import com.example.task_java.repository.UserRep;
import com.example.task_java.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationServiceImplementation implements NotificationService {

    private final NotificationRep notificationRep;
    private final UserRep userRep;

    private void checkUserExists(Long userId) throws RecordNotFoundException {
        if (!userRep.existsById(userId)) {
            throw new RecordNotFoundException(
                    "User " + userId + " not found!");
        }
    }

    @Override
    public List<Notification> getAllUsersNotification(long userId) throws RecordNotFoundException {
        checkUserExists(userId);
        return notificationRep.findByUserId(userId);
    }

    @Override
    public List<Notification> getUsersPendingNotifications(long userId) throws RecordNotFoundException {
        checkUserExists(userId);
        List<Notification> pending = notificationRep.findByUserId(userId).stream()
                .filter(notification -> !notification.getIsRead())
                .collect(Collectors.toList());
        notificationRep.markAllAsRead(userId);
        pending.forEach(notification -> notification.setIsRead(true));

        return pending;
    }

    @Override
    public Notification addNotification(Notification notification) throws RecordNotFoundException {
        if (notification == null) {
            throw new IllegalArgumentException(
                    "Invalid notification.");
        }
        checkUserExists(notification.getUserId());
        try {
            return notificationRep.save(notification);
        } catch (DoubleRecordException e) {
            throw new RuntimeException(
                    "Notification already exists! " + e.getMessage(), e);
        }
    }
}
