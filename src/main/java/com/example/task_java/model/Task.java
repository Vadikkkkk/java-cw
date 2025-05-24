package com.example.task_java.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @NonNull
    private Long userId;

    @NonNull
    @Column(nullable = false)
    private String taskText;

    private LocalDateTime targetDate;

    @NonNull
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @NonNull
    @Builder.Default
    private Boolean isComplete = false;

    @NonNull
    @Builder.Default
    private Boolean isDeleted = false;
}
