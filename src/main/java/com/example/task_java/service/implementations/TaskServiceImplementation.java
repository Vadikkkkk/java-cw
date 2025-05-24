package com.example.task_java.service.implementations;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Notification;
import com.example.task_java.model.Task;
import com.example.task_java.repository.TaskRep;
import com.example.task_java.repository.UserRep;
import com.example.task_java.service.NotificationService;
import com.example.task_java.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class TaskServiceImplementation implements TaskService {

    private final TaskRep taskRep;
    private final UserRep userRep;
    private final NotificationService notificationService;

    private void checkUserExists(Long userId) throws RecordNotFoundException {
        if (!userRep.existsById(userId)) {
            throw new RecordNotFoundException(
                    "User " + userId + " not found.");
        }
    }

    @Override
    public Task findById(long userId, long taskId) throws RecordNotFoundException {
        checkUserExists(userId);//првоеряем существует ли пользователь
        return taskRep.findById(taskId)
                .filter(task -> task.getUserId().equals(userId) && !task.getIsDeleted())
                .orElseThrow(() -> new RecordNotFoundException("Task " + taskId + " not found."));
    }

    @Override
    public List<Task> getAllByUserId(long userId) throws RecordNotFoundException {
        checkUserExists(userId);
        return taskRep.findByUserId(userId).stream()
                .filter(task -> !task.getIsDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getPendingTasksByUserId(long userId) throws RecordNotFoundException {
        checkUserExists(userId);
        return taskRep.findByUserId(userId).stream()
                .filter(task -> !task.getIsDeleted() && !task.getIsComplete())
                .collect(Collectors.toList());
    }

    @Override
    public Task createForUserId(long userId, Task task) throws RecordNotFoundException, DoubleRecordException {
        checkUserExists(userId);
        if (task == null || task.getTaskText().isBlank()) {
            throw new IllegalArgumentException("Invalid Task.");
        }
        task.setIsComplete(false);
        task.setIsDeleted(false);
        Task createdTask = taskRep.save(task);
        notificationService.addNotification(new Notification(//уведомление о создании задачи
                null,
                task.getTaskId(),
                userId,
                "Задача '" + createdTask.getTaskText() + "' создана.",
                LocalDateTime.now(),
                false
        ));
        return createdTask;
    }

    @Override
    public void deleteTask(long userId, long taskId) throws RecordNotFoundException {
        checkUserExists(userId);
        Task task = findById(userId, taskId);
        task.setIsDeleted(true);
        taskRep.save(task);
    }
}
