package com.example.task_java.service;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskService {

    public Task findById(long userId, long taskId) throws RecordNotFoundException;
    public List<Task> getAllByUserId(long userId) throws RecordNotFoundException;
    public List<Task> getPendingTasksByUserId(long userId) throws RecordNotFoundException;
    public Task createForUserId(long userId, Task task) throws RecordNotFoundException, DoubleRecordException;
    public void deleteTask(long userId, long taskId) throws RecordNotFoundException;
    public void completeTask(long userId, long taskId) throws RecordNotFoundException;
}
