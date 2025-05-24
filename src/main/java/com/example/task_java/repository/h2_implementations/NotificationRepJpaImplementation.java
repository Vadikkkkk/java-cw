package com.example.task_java.repository.h2_implementations;

import com.example.task_java.model.Notification;
import com.example.task_java.repository.NotificationRep;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Profile("h2")
public interface NotificationRepJpaImplementation extends NotificationRep, JpaRepository<Notification, Long>{
    List<Notification> findByUserId(long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId")
    void markAllAsRead(@Param("userId") long userId);

}
