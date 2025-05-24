package com.example.task_java.repository.implementations;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Notification;
import com.example.task_java.repository.NotificationRep;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("inmemory")
public class NotificationRepImplementation implements NotificationRep {

    private final List<Notification> notifications = new ArrayList<>();
    private static final AtomicLong idCounter = new AtomicLong();

    @Override
    public Notification save(Notification notification) throws DoubleRecordException {
        if (notification.getNotificationId() == null) {
            notification.setNotificationId(idCounter.incrementAndGet());
            notifications.add(notification);
        } else {
            boolean removed = notifications
                    .removeIf(n -> n.getNotificationId().equals(notification.getNotificationId()));
            if (removed) {
                notifications.add(notification);
            } else {
                throw new IllegalArgumentException(
                        "cannot update non-existing notification with id " + notification.getNotificationId());
            }
        }
        return notification;
    }

    @Override
    public List<Notification> findByUserId(long userId) {
        return notifications.stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void markAllAsRead(long userId) throws RecordNotFoundException {
        boolean found = false;
        for (Notification notification : notifications) {
            if (notification.getUserId().equals(userId)) {
                notification.setIsRead(true);
                found = true;
            }
        }
        if (!found) {
            throw new RecordNotFoundException("No notifications found for user " + userId);
        }
    }
}
