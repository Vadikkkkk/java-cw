package com.example.task_java.controller;

import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.Notification;
import com.example.task_java.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/notifications")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllUserNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getAllUsersNotification(userId);
            return ResponseEntity.ok(notifications);
        } catch (RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Notification>> getPendingUsersNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getUsersPendingNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
