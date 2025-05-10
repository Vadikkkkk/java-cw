package com.example.task_java.model;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @NonNull
    private Long taskId;

    @NonNull
    private Long userId;

    @NonNull
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
