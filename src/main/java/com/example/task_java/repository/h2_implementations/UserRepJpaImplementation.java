package com.example.task_java.repository.h2_implementations;

import com.example.task_java.model.User;
import com.example.task_java.repository.UserRep;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("h2")
public interface UserRepJpaImplementation extends UserRep, JpaRepository<User, Long>{
    boolean existsByEmailIgnoreCase(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}
