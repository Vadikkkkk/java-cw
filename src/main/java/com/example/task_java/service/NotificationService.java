package com.example.task_java.service;

import com.example.task_java.model.Notification;
import com.example.task_java.exception.RecordNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {
    public List<Notification> getAllUsersNotification(long userId) throws RecordNotFoundException;
    public List<Notification> getUsersPendingNotifications(long userId) throws RecordNotFoundException;
    public Notification addNotification(Notification notification) throws RecordNotFoundException;
}
