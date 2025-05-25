package com.example.task_java.repository;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRep {
    Task save(Task task) throws DoubleRecordException;
    Optional<Task> findById(Long taskId);
    List<Task> findByUserId(Long userId);
    List<Task> findByIsDeletedFalseAndIsCompleteFalse();
}
