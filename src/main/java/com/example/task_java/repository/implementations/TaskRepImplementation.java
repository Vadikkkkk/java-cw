package com.example.task_java.repository.implementations;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.model.Task;
import com.example.task_java.repository.TaskRep;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class TaskRepImplementation implements TaskRep {

    private final List<Task> tasks = new ArrayList<>();
    private static final AtomicLong idCounter = new AtomicLong();

    @Override
    public Task saveTask(Task task) throws DoubleRecordException {
        if (task == null) {
            throw new IllegalArgumentException("Invalid Task!");
        }

        if (task.getTaskId() == null) {
            task.setTaskId(idCounter.incrementAndGet());
            tasks.add(task);
        } else {
            boolean removed = tasks
                    .removeIf(t -> t.getTaskId().equals(task.getTaskId()));//для обновления задачи удаляем если уже есть
            if (removed) {
                tasks.add(task);//и добавляем

            } else {
                throw new IllegalArgumentException(
                        "Task with id " + task.getTaskId() + "doesn't exists!");
            }
        }
        return task;
    }

    @Override
    public Optional<Task> findTaskById(Long taskId) {
        return tasks.stream()
                .filter(task -> task.getTaskId().equals(taskId)
                        && !task.getIsDeleted())
                .findFirst();
    }

    @Override
    public List<Task> findTasksByUserId(Long userId) {
        return tasks.stream()
                .filter(task -> task.getUserId().equals(userId)
                        && !task.getIsDeleted())
                .toList();
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null || task.getTaskId() == null) {
            throw new IllegalArgumentException("Invalid Task");
        }
        Optional<Task> existingTask = tasks.stream()
                .filter(t -> t.getTaskId().equals(task.getTaskId()))
                .findFirst();

        if (existingTask.isPresent()) {
            tasks.removeIf(t -> t.getTaskId().equals(task.getTaskId()));
            tasks.add(task);
            return task;
        }
        throw new IllegalArgumentException(
                "Task " + task.getTaskId() + " not found.");
    }
}
