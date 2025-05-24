package com.example.task_java.controller_test;

import com.example.task_java.controller.NotificationController;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Notification;
import com.example.task_java.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification sampleNotification;

    @BeforeEach
    void setUp() {
        sampleNotification = Notification.builder()
                .notificationId(1L)
                .taskId(10L)
                .userId(1L)
                .text("Test notification")
                .date(LocalDateTime.now().minusDays(1))
                .isRead(false)
                .build();
    }

    @Test
    void getAllUserNotifications_ReturnsNotifications() throws Exception {
        Mockito.when(notificationService.getAllUsersNotification(1L))
                .thenReturn(List.of(sampleNotification));

        mockMvc.perform(get("/users/1/notifications/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text").value("Test notification"))
                .andExpect(jsonPath("$[0].taskId").value(10))
                .andExpect(jsonPath("$[0].isRead").value(false));
    }

    @Test
    void getAllUserNotifications_UserNotFound_Returns404() throws Exception {
        Mockito.when(notificationService.getAllUsersNotification(99L))
                .thenThrow(new RecordNotFoundException("User not found"));

        mockMvc.perform(get("/users/99/notifications/all"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User not found"));
    }

    @Test
    void getPendingUsersNotifications_ReturnsNotifications() throws Exception {
        Mockito.when(notificationService.getUsersPendingNotifications(1L))
                .thenReturn(List.of(sampleNotification));

        mockMvc.perform(get("/users/1/notifications/pending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text").value("Test notification"))
                .andExpect(jsonPath("$[0].taskId").value(10))
                .andExpect(jsonPath("$[0].isRead").value(false));
    }

    @Test
    void getPendingUsersNotifications_UserNotFound_Returns404() throws Exception {
        Mockito.when(notificationService.getUsersPendingNotifications(999L))
                .thenThrow(new RecordNotFoundException("User not found"));

        mockMvc.perform(get("/users/999/notifications/pending"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User not found"));
    }
}
