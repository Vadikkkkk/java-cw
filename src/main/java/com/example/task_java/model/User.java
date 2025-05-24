package com.example.task_java.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;

    @NonNull
    @Column(nullable = false, unique = true)
    private String email;

    @NonNull
    @Builder.Default
    private LocalDateTime registrationDate = LocalDateTime.now();

}
