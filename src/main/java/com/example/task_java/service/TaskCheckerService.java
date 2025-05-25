package com.example.task_java.service;

import org.springframework.stereotype.Service;

@Service
public interface TaskCheckerService {
    void checkAndNotifyOverdueTasks();
}
