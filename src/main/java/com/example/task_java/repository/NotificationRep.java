package com.example.task_java.repository;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Notification;

import java.util.List;

public interface NotificationRep {
    Notification save(Notification notification) throws DoubleRecordException;

    List<Notification> findByUserId(long userId);

    void markAllAsRead(long userId) throws RecordNotFoundException;
}
