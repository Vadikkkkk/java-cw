package com.example.task_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TaskJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskJavaApplication.class, args);
	}

}
