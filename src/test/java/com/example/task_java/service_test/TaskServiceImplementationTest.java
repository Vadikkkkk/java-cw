package com.example.task_java.service_test;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Notification;
import com.example.task_java.model.Task;
import com.example.task_java.repository.TaskRep;
import com.example.task_java.repository.UserRep;
import com.example.task_java.service.NotificationService;
import com.example.task_java.service.implementations.TaskServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplementationTest {

    @Mock
    private TaskRep taskRep;

    @Mock
    private UserRep userRep;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskServiceImplementation taskService;

    private Task sampleTask;
    private final Long userId = 1L;
    private final Long taskId = 10L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleTask = Task.builder()
                .taskId(taskId)
                .userId(userId)
                .taskText("Sample task")
                .isComplete(false)
                .isDeleted(false)
                .build();
    }

    // Позитивные тесты

    @Test
    void findById_UserExists_TaskFound_ReturnsTask() throws RecordNotFoundException {
        when(userRep.existsById(userId)).thenReturn(true);
        when(taskRep.findById(taskId)).thenReturn(Optional.of(sampleTask));

        Task task = taskService.findById(userId, taskId);

        assertNotNull(task);
        assertEquals(taskId, task.getTaskId());
        verify(userRep).existsById(userId);
        verify(taskRep).findById(taskId);
    }

    @Test
    void getAllByUserId_UserExists_ReturnsListOfTasks() throws RecordNotFoundException {
        when(userRep.existsById(userId)).thenReturn(true);
        when(taskRep.findByUserId(userId)).thenReturn(List.of(sampleTask));

        List<Task> tasks = taskService.getAllByUserId(userId);

        assertEquals(1, tasks.size());
        assertFalse(tasks.get(0).getIsDeleted());
        verify(userRep).existsById(userId);
        verify(taskRep).findByUserId(userId);
    }

    @Test
    void getPendingTasksByUserId_UserExists_ReturnsPendingTasks() throws RecordNotFoundException {
        Task pendingTask = Task.builder()
                .taskId(11L)
                .userId(userId)
                .taskText("Pending task")
                .isComplete(false)
                .isDeleted(false)
                .build();

        Task completedTask = Task.builder()
                .taskId(12L)
                .userId(userId)
                .taskText("Completed task")
                .isComplete(true)
                .isDeleted(false)
                .build();

        when(userRep.existsById(userId)).thenReturn(true);
        when(taskRep.findByUserId(userId)).thenReturn(List.of(pendingTask, completedTask));

        List<Task> pendingTasks = taskService.getPendingTasksByUserId(userId);

        assertEquals(1, pendingTasks.size());
        assertFalse(pendingTasks.get(0).getIsComplete());
        verify(userRep).existsById(userId);
        verify(taskRep).findByUserId(userId);
    }

    @Test
    void createForUserId_ValidTask_CreatesTaskAndSendsNotification() throws RecordNotFoundException, DoubleRecordException {
        Task newTask = Task.builder()
                .taskText("New task")
                .userId(userId)
                .build();

        when(userRep.existsById(userId)).thenReturn(true);
        when(taskRep.save(any(Task.class))).thenAnswer(invocation -> {
            Task arg = invocation.getArgument(0);
            arg.setTaskId(100L);
            return arg;
        });

        Task createdTask = taskService.createForUserId(userId, newTask);

        assertNotNull(createdTask.getTaskId());
        assertFalse(createdTask.getIsComplete());
        assertFalse(createdTask.getIsDeleted());

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService).addNotification(notificationCaptor.capture());
        Notification sentNotification = notificationCaptor.getValue();

        assertEquals("Задача '" + createdTask.getTaskText() + "' создана.", sentNotification.getText());
        assertEquals(userId, sentNotification.getUserId());

        verify(userRep).existsById(userId);
        verify(taskRep).save(any(Task.class));
    }

    @Test
    void deleteTask_UserAndTaskExist_TaskIsDeleted() throws RecordNotFoundException {
        when(userRep.existsById(userId)).thenReturn(true);
        when(taskRep.findById(taskId)).thenReturn(Optional.of(sampleTask));
        when(taskRep.updateTask(any(Task.class))).thenReturn(sampleTask);

        assertDoesNotThrow(() -> taskService.deleteTask(userId, taskId));

        assertTrue(sampleTask.getIsDeleted());
        verify(userRep, times(2)).existsById(userId);
        verify(taskRep).findById(taskId);
        verify(taskRep).updateTask(sampleTask);
    }

    // Негативные тесты

    @Test
    void findById_UserDoesNotExist_ThrowsRecordNotFoundException() {
        when(userRep.existsById(userId)).thenReturn(false);

        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class,
                () -> taskService.findById(userId, taskId));

        assertTrue(ex.getMessage().contains("User " + userId + " not found"));
        verify(userRep).existsById(userId);
        verify(taskRep, never()).findById(anyLong());
    }

    @Test
    void findById_TaskNotFound_ThrowsRecordNotFoundException() {
        when(userRep.existsById(userId)).thenReturn(true);
        when(taskRep.findById(taskId)).thenReturn(Optional.empty());

        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class,
                () -> taskService.findById(userId, taskId));

        assertTrue(ex.getMessage().contains("Task " + taskId + " not found"));
        verify(userRep).existsById(userId);
        verify(taskRep).findById(taskId);
    }

    @Test
    void createForUserId_UserDoesNotExist_ThrowsRecordNotFoundException() {
        long userId = 1L;
        Task task = new Task();
        task.setTaskText("Test task");

        when(userRep.existsById(userId)).thenReturn(false);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            taskService.createForUserId(userId, task);
        });

        assertEquals("User 1 not found.", exception.getMessage());
        verify(userRep, times(1)).existsById(userId); // вызывался только один раз
        verifyNoInteractions(taskRep);
        verifyNoInteractions(notificationService);
    }


    @Test
    void createForUserId_NullOrBlankTask_ThrowsIllegalArgumentException() throws RecordNotFoundException {
        when(userRep.existsById(userId)).thenReturn(true);

        Task nullTask = null;
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> taskService.createForUserId(userId, nullTask));
        assertEquals("Invalid Task.", ex1.getMessage());

        Task blankTask = Task.builder()
                .taskText("  ")
                .userId(userId)
                .build();
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> taskService.createForUserId(userId, blankTask));
        assertEquals("Invalid Task.", ex2.getMessage());
    }

    @Test
    void deleteTask_UserDoesNotExist_ThrowsRecordNotFoundException() {
        when(userRep.existsById(userId)).thenReturn(false);

        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class,
                () -> taskService.deleteTask(userId, taskId));

        assertTrue(ex.getMessage().contains("User " + userId + " not found"));
        verify(userRep).existsById(userId);
        verify(taskRep, never()).findById(anyLong());
    }

    @Test
    void deleteTask_TaskNotFound_ThrowsRecordNotFoundException() {
        long userId = 1L;
        long taskId = 123L;

        when(userRep.existsById(userId)).thenReturn(true);
        when(taskRep.findById(taskId)).thenReturn(Optional.empty());

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class, () -> {
            taskService.deleteTask(userId, taskId);
        });

        assertEquals("Task 123 not found.", exception.getMessage());
        verify(userRep, times(2)).existsById(userId); // 1 раз в deleteTask, 1 раз во findById
        verify(taskRep).findById(taskId);
        verify(taskRep, never()).updateTask(any());
    }

}
