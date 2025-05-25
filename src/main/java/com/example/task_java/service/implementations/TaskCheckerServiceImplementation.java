package com.example.task_java.service.implementations;

import com.example.task_java.model.Task;
import com.example.task_java.repository.TaskRep;
import com.example.task_java.service.OverdueNotificationService;
import com.example.task_java.service.TaskCheckerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskCheckerServiceImplementation implements TaskCheckerService {

    private final TaskRep taskRepository;
    private final OverdueNotificationService overdueNotificationService;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void checkAndNotifyOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();

        List<Task> activeTasks = taskRepository.findByIsDeletedFalseAndIsCompleteFalse();

        for (Task task : activeTasks) {
            if (task.getTargetDate() != null && task.getTargetDate().isBefore(now) && !task.getIsOverdueNotified()) {

                overdueNotificationService.sendOverdueNotification(task);

                task.setIsOverdueNotified(true);
                taskRepository.save(task);
            }
        }
    }
}
