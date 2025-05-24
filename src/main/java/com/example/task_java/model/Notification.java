package com.example.task_java.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @NonNull
    private Long taskId;

    @NonNull
    private Long userId;

    @NonNull
    @Column(nullable = false)
    private String text;

    @NonNull
    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    @NonNull
    @Builder.Default
    private Boolean isRead = false;
}