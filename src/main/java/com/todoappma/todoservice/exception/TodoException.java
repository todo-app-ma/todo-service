package com.todoappma.todoservice.exception;

import lombok.Getter;

@Getter
public class TodoException extends RuntimeException {

    private final String errorCode;

    public TodoException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public static TodoException notFound() {
        return new TodoException("TODO_001", "Todo not found");
    }

    public static TodoException unauthorized() {
        return new TodoException("TODO_002", "You are not authorized to access this todo");
    }
}
