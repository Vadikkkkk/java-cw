package com.example.task_java.repository;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRep {
    Notification saveNotification(Notification notification) throws DoubleRecordException;

    List<Notification> findNotificationsByUserId(long userId);

    void markAllAsRead(long userId) throws RecordNotFoundException;
}
