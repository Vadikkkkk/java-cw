package com.example.task_java.service.implementations;

import com.example.task_java.model.Task;
import com.example.task_java.service.OverdueNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OverdueNotificationServiceImplementation implements OverdueNotificationService{
    private final RabbitTemplate rabbitTemplate;

    @Async("overdueTaskExecutor")
    @Override
    public void sendOverdueNotification(Task task) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", task.getUserId());
        message.put("taskId", task.getTaskId());
        message.put("taskText", task.getTaskText());
        message.put("dueDate", task.getTargetDate());

        rabbitTemplate.convertAndSend("task.exchange", "task.overdue", message);
    }
}
