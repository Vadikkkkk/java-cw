package com.example.task_java.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DoubleRecordException extends RuntimeException{
    public DoubleRecordException(String message) {
        super(message);
    }
}
