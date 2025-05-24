package com.example.task_java.repository.h2_implementations;

import com.example.task_java.model.Task;
import com.example.task_java.repository.TaskRep;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("h2")
public interface TaskRepJpaImplementation extends TaskRep, JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
}
