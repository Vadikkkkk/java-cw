package com.example.task_java.controller_test;

import com.example.task_java.controller.TaskConroller;
import com.example.task_java.dto.TaskRequest;
import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Task;
import com.example.task_java.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskConroller.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .taskId(1L)
                .userId(1L)
                .taskText("Test Task")
                .targetDate(LocalDateTime.now().plusDays(5))
                .creationDate(LocalDateTime.now())
                .build();
    }

    //Позитивные тесты

    @Test
    void getAllUserTasks_ReturnsTasks() throws Exception {
        List<Task> tasks = Arrays.asList(sampleTask);
        Mockito.when(taskService.getAllByUserId(1L)).thenReturn(tasks);

        mockMvc.perform(get("/users/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].taskText").value("Test Task"));
    }

    @Test
    void createForUserId_ReturnsCreatedTask() throws Exception {
        TaskRequest request = new TaskRequest("Test Task", LocalDateTime.now().plusDays(5));
        Mockito.when(taskService.createForUserId(eq(1L), any(Task.class))).thenReturn(sampleTask);

        mockMvc.perform(post("/users/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/1/tasks/1"))
                .andExpect(jsonPath("$.taskText").value("Test Task"));
    }

    @Test
    void getPendingUserTasks_ReturnsTasks() throws Exception {
        Mockito.when(taskService.getPendingTasksByUserId(1L)).thenReturn(List.of(sampleTask));

        mockMvc.perform(get("/users/1/tasks/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].taskText").value("Test Task"));
    }

    @Test
    void softDeleteTask_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/users/1/tasks/delete/1"))
                .andExpect(status().isNoContent());
    }

    // Негативные тесты

    @Test
    void getAllUserTasks_UserNotFound_Returns404() throws Exception {
        Mockito.when(taskService.getAllByUserId(99L))
                .thenThrow(new RecordNotFoundException("User not found"));

        mockMvc.perform(get("/users/99/tasks"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User not found"));
    }

    @Test
    void createForUserId_EmptyText_ReturnsBadRequest() throws Exception {
        TaskRequest request = new TaskRequest("   ", LocalDateTime.now());

        mockMvc.perform(post("/users/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Invalid Task!"));
    }

    @Test
    void createForUserId_DuplicateTask_ReturnsConflict() throws Exception {
        TaskRequest request = new TaskRequest("Duplicate Task", LocalDateTime.now());

        Mockito.when(taskService.createForUserId(eq(1L), any(Task.class)))
                .thenThrow(new DoubleRecordException("Task already exists"));

        mockMvc.perform(post("/users/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(status().reason("Task already exists"));
    }

    @Test
    void getPendingUserTasks_UserNotFound_Returns404() throws Exception {
        Mockito.when(taskService.getPendingTasksByUserId(999L))
                .thenThrow(new RecordNotFoundException("User not found"));

        mockMvc.perform(get("/users/999/tasks/pending"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User not found"));
    }

    @Test
    void softDeleteTask_NotFound_Returns404() throws Exception {
        Mockito.doThrow(new RecordNotFoundException("Task not found"))
                .when(taskService).deleteTask(1L, 99L);

        mockMvc.perform(delete("/users/1/tasks/delete/99"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Task not found"));
    }
}
