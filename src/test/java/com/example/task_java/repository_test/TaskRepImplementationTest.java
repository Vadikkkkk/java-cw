package com.example.task_java.repository_test;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.model.Task;
import com.example.task_java.repository.implementations.TaskRepImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepImplementationTest {

    private TaskRepImplementation taskRepo;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        taskRepo = new TaskRepImplementation();

        sampleTask = Task.builder()
                .taskId(null)
                .userId(1L)
                .taskText("Sample Task")
                .creationDate(LocalDateTime.now())
                .targetDate(LocalDateTime.now().plusDays(3))
                .isDeleted(false)
                .build();
    }

    // Позитивные тесты

    @Test
    void saveTask_New_AssignsIdAndSaves() throws DoubleRecordException {
        Task saved = taskRepo.save(sampleTask);
        assertNotNull(saved.getTaskId());
        assertEquals(1, taskRepo.findByUserId(1L).size());
    }

    @Test
    void findTaskById_FindsSaved() throws DoubleRecordException {
        Task saved = taskRepo.save(sampleTask);
        Optional<Task> found = taskRepo.findById(saved.getTaskId());
        assertTrue(found.isPresent());
        assertEquals(saved.getTaskText(), found.get().getTaskText());
    }

    @Test
    void findTasksByUserId_ReturnsOnlyUser() throws DoubleRecordException {
        Task t1 = taskRepo.save(sampleTask);
        Task t2 = Task.builder()
                .userId(2L)
                .taskText("Other user task")
                .creationDate(LocalDateTime.now())
                .targetDate(LocalDateTime.now().plusDays(2))
                .isDeleted(false)
                .build();
        taskRepo.save(t2);

        assertEquals(1, taskRepo.findByUserId(1L).size());
        assertEquals(1, taskRepo.findByUserId(2L).size());
    }

    @Test
    void updateTask_ExistingTask_UpdatesSuccessfully() throws DoubleRecordException {
        Task saved = taskRepo.save(sampleTask);
        Task updated = Task.builder()
                .taskId(saved.getTaskId())
                .userId(saved.getUserId())
                .taskText("Updated text")
                .creationDate(saved.getCreationDate())
                .targetDate(saved.getTargetDate())
                .isDeleted(false)
                .build();

        Task result = taskRepo.updateTask(updated);
        assertEquals("Updated text", result.getTaskText());
        assertEquals(1, taskRepo.findByUserId(saved.getUserId()).size());
    }

    // Негативные тесты

    @Test
    void saveTask_Null_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            taskRepo.save(null);
        });
        assertEquals("Invalid Task!", ex.getMessage());
    }

    @Test
    void saveTask_UpdateNonExisting_ThrowsIllegalArgumentException() {
        Task taskWithId = Task.builder()
                .taskId(999L)
                .userId(1L)
                .taskText("Non-existing task")
                .creationDate(LocalDateTime.now())
                .targetDate(LocalDateTime.now().plusDays(1))
                .isDeleted(false)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            taskRepo.save(taskWithId);
        });
        assertTrue(ex.getMessage().contains("doesn't exists"));
    }

    @Test
    void updateTask_NullTask_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            taskRepo.updateTask(null);
        });
        assertEquals("Invalid Task", ex.getMessage());
    }

    @Test
    void updateTask_TaskWithNullId_ThrowsIllegalArgumentException() {
        Task taskWithoutId = Task.builder()
                .taskId(null)
                .userId(1L)
                .taskText("No ID task")
                .creationDate(LocalDateTime.now())
                .targetDate(LocalDateTime.now().plusDays(1))
                .isDeleted(false)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            taskRepo.updateTask(taskWithoutId);
        });
        assertEquals("Invalid Task", ex.getMessage());
    }

    @Test
    void updateTask_NonExistingTask_ThrowsIllegalArgumentException() {
        Task nonExistingTask = Task.builder()
                .taskId(999L)
                .userId(1L)
                .taskText("Non-existing")
                .creationDate(LocalDateTime.now())
                .targetDate(LocalDateTime.now().plusDays(1))
                .isDeleted(false)
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            taskRepo.updateTask(nonExistingTask);
        });
        assertTrue(ex.getMessage().contains("not found"));
    }
}
