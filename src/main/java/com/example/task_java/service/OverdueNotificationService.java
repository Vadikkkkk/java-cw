package com.example.task_java.service;

import com.example.task_java.model.Task;

public interface OverdueNotificationService {
    void sendOverdueNotification(Task task);
}
