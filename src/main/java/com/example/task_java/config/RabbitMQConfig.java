package com.example.task_java.config;


import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TASK_EXCHANGE = "task.exchange";
    public static final String TASK_QUEUE = "task.queue";
    public static final String TASK_ROUTING_KEY = "task.created";

    public static final String TASK_OVERDUE_QUEUE = "task.overdue.queue";
    public static final String TASK_OVERDUE_ROUTING_KEY = "task.overdue";

    public static final String TASK_COMPLETED_QUEUE = "task.completed.queue";
    public static final String TASK_COMPLETED_ROUTING_KEY = "task.completed";


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(TASK_EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return new Queue(TASK_QUEUE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(TASK_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue overdueQueue() {
        return new Queue(TASK_OVERDUE_QUEUE);
    }

    @Bean
    public Binding overdueBinding() {
        return BindingBuilder
                .bind(overdueQueue())
                .to(exchange())
                .with(TASK_OVERDUE_ROUTING_KEY);
    }

    @Bean
    public Queue completedQueue() {
        return new Queue(TASK_COMPLETED_QUEUE);
    }

    @Bean
    public Binding completedBinding() {
        return BindingBuilder
                .bind(completedQueue())
                .to(exchange())
                .with(TASK_COMPLETED_ROUTING_KEY);
    }
}