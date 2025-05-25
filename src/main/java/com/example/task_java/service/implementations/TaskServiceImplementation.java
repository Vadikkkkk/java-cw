package com.example.task_java.service.implementations;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Task;
import com.example.task_java.repository.TaskRep;
import com.example.task_java.repository.UserRep;
import com.example.task_java.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class TaskServiceImplementation implements TaskService {

    private final TaskRep taskRep;
    private final UserRep userRep;

    private final RabbitTemplate rabbitTemplate;


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
    @Cacheable(value = "tasksAll", key = "#userId")
    public List<Task> getAllByUserId(long userId) throws RecordNotFoundException {
        System.out.println("Loading tasks from DB for user: " + userId);
        checkUserExists(userId);
        return taskRep.findByUserId(userId).stream()
                .filter(task -> !task.getIsDeleted())
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "tasksPending", key = "#userId")
    public List<Task> getPendingTasksByUserId(long userId) throws RecordNotFoundException {
        System.out.println("Loading tasks from DB for user: " + userId);
        checkUserExists(userId);
        return taskRep.findByUserId(userId).stream()
                .filter(task -> !task.getIsDeleted() && !task.getIsComplete())
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"tasksAll", "tasksPending"}, key = "#userId")
    public Task createForUserId(long userId, Task task) throws RecordNotFoundException, DoubleRecordException {
        checkUserExists(userId);
        if (task == null || task.getTaskText().isBlank()) {
            throw new IllegalArgumentException("Invalid Task.");
        }

        task.setIsComplete(false);
        task.setIsDeleted(false);
        task.setUserId(userId); // <-- важно, ты это не делал
        Task createdTask = taskRep.save(task);

        Map<String, Object> message = new HashMap<>();
        message.put("userId", createdTask.getUserId());
        message.put("taskId", createdTask.getTaskId());
        message.put("taskText", createdTask.getTaskText());

        rabbitTemplate.convertAndSend("task.exchange", "task.created", message);

        return createdTask;
    }

    @Override
    @CacheEvict(value = {"tasksAll", "tasksPending"}, key = "#userId")
    public void deleteTask(long userId, long taskId) throws RecordNotFoundException {
        checkUserExists(userId);
        Task task = findById(userId, taskId);
        task.setIsDeleted(true);
        taskRep.save(task);
    }
}
