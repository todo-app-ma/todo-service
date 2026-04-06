package com.todoappma.todoservice.service;

import com.todoappma.todoservice.dto.request.CreateTodoRequestDto;
import com.todoappma.todoservice.dto.request.UpdateTodoRequestDto;
import com.todoappma.todoservice.dto.response.TodoResponseDto;

import java.util.List;
import java.util.UUID;

public interface TodoService {
    TodoResponseDto createTodo(CreateTodoRequestDto request);
    TodoResponseDto getTodo(UUID todoId, UUID userId);
    List<TodoResponseDto> getAllTodos(UUID userId);
    TodoResponseDto updateTodo(UpdateTodoRequestDto request);
    void deleteTodo(UUID todoId, UUID userId);
    TodoResponseDto markDone(UUID todoId, UUID userId);
}
