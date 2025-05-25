package com.example.task_java.controller;

import com.example.task_java.dto.TaskRequest;
import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Task;
import com.example.task_java.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/users/{userId}/tasks")
@AllArgsConstructor
public class TaskConroller {

    private final TaskService taskService;
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllUserTasks(@PathVariable Long userId) {
        try {
            List<Task> tasks = taskService.getAllByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PostMapping
    public ResponseEntity<Task> createForUserId(@PathVariable Long userId, @RequestBody TaskRequest taskRequest) {
        try {
            if (taskRequest.getTaskText() == null || taskRequest.getTaskText().isBlank()) {
                throw new IllegalArgumentException("Invalid Task!");
            }
            Task newTask = Task.builder()
                    .userId(userId)
                    .taskText(taskRequest.getTaskText())
                    .targetDate(taskRequest.getTargetDate())
                    .build();

            Task createdTask = taskService.createForUserId(userId, newTask);
            URI location = URI.create(String.format("/users/%d/tasks/%d", userId, createdTask.getTaskId()));
            return ResponseEntity.created(location).body(createdTask);

        } catch (RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (DoubleRecordException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingUserTasks(@PathVariable Long userId) {
        try {
            List<Task> tasks = taskService.getPendingTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{taskId}/delete")
    public ResponseEntity<Void> softDeleteTask(@PathVariable Long userId, @PathVariable Long taskId) {
        try {
            taskService.deleteTask(userId, taskId);
            return ResponseEntity.noContent().build();
        } catch (RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable Long userId, @PathVariable Long taskId) {
        try {
            taskService.completeTask(userId, taskId);
            return ResponseEntity.noContent().build();
        } catch (RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
