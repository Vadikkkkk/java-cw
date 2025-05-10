package com.example.task_java.repository;

import com.example.task_java.exception.DuplicateRecordException;
import com.example.task_java.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRep {
    List<Task> findAllTasks();
    Task saveTask(Task task) throws DuplicateRecordException;
    Optional<Task> findTaskById(Long taskId);
    List<Task> findTasksByUserId(Long userId);
    boolean existsById(Long taskId);
    Task updateTask(Task task);
}
