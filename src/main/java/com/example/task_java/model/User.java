package com.example.task_java.model;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long userId;

    private String userName;

    @NonNull
    private String email;

    @NonNull
    @Builder.Default
    private LocalDateTime registrationDate = LocalDateTime.now();

}
