package com.example.task_java.messaging;

import com.example.task_java.model.Notification;
import com.example.task_java.repository.NotificationRep;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationListener {

        private final NotificationRep notificationRep;

        @RabbitListener(queues = "task.queue")
        public void handleTaskCreated(Map<String, Object> message) {
            try {
                Long userId = ((Number) message.get("userId")).longValue();
                Long taskId = ((Number) message.get("taskId")).longValue();
                String taskText = (String) message.get("taskText");

                Notification notification = new Notification(
                        null,
                        taskId,
                        userId,
                        "Задача '" + taskText + "' создана.",
                        LocalDateTime.now(),
                        false
                );

                notificationRep.save(notification);
                System.out.println("Notification saved for task: " + taskId);
            } catch (Exception e) {
                System.err.println("Ошибка при обработке сообщения: " + e.getMessage());
                e.printStackTrace(); // Чтобы увидеть stacktrace
            }
        }
}
