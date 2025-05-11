package com.example.task_java.repository;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRep {
    Task saveTask(Task task) throws DoubleRecordException;
    Optional<Task> findTaskById(Long taskId);
    List<Task> findTasksByUserId(Long userId);
    Task updateTask(Task task);
}
