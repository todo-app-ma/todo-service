package com.todoappma.todoservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoappma.todoservice.dto.request.CreateTodoRequestDto;
import com.todoappma.todoservice.dto.request.UpdateTodoRequestDto;
import com.todoappma.todoservice.dto.response.TodoResponseDto;
import com.todoappma.todoservice.entity.Outbox;
import com.todoappma.todoservice.entity.Todo;
import com.todoappma.todoservice.exception.TodoException;
import com.todoappma.todoservice.repository.OutboxRepository;
import com.todoappma.todoservice.repository.TodoRepository;
import com.todoappma.todoservice.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public TodoResponseDto createTodo(CreateTodoRequestDto request) {
        Todo todo = Todo.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .done(false)
                .build();

        todo = todoRepository.save(todo);

        writeOutboxEvent(todo.getId().toString(), "Todo", "TodoCreated", Map.of(
                "todoId", todo.getId().toString(),
                "userId", todo.getUserId().toString(),
                "title", todo.getTitle(),
                "deadline", todo.getDeadline().toString()
        ));

        return toDto(todo);
    }

    @Override
    public TodoResponseDto getTodo(UUID todoId, UUID userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(TodoException::notFound);
        return toDto(todo);
    }

    @Override
    public List<TodoResponseDto> getAllTodos(UUID userId) {
        return todoRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public TodoResponseDto updateTodo(UpdateTodoRequestDto request) {
        Todo todo = todoRepository.findByIdAndUserId(request.getTodoId(), request.getUserId())
                .orElseThrow(TodoException::notFound);

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDeadline(request.getDeadline());

        return toDto(todoRepository.save(todo));
    }

    @Override
    @Transactional
    public void deleteTodo(UUID todoId, UUID userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(TodoException::notFound);
        todoRepository.delete(todo);
    }

    @Override
    @Transactional
    public TodoResponseDto markDone(UUID todoId, UUID userId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(TodoException::notFound);

        todo.setDone(true);
        todo = todoRepository.save(todo);

        writeOutboxEvent(todo.getId().toString(), "Todo", "TodoCompleted", Map.of(
                "todoId", todo.getId().toString(),
                "userId", todo.getUserId().toString()
        ));

        return toDto(todo);
    }

    @SneakyThrows
    private void writeOutboxEvent(String aggregateId, String aggregateType, String eventType, Map<String, Object> payload) {
        outboxRepository.save(Outbox.builder()
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .eventType(eventType)
                .payload(objectMapper.writeValueAsString(payload))
                .createdAt(LocalDateTime.now())
                .build());
    }

    private TodoResponseDto toDto(Todo todo) {
        return TodoResponseDto.builder()
                .todoId(todo.getId())
                .userId(todo.getUserId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .deadline(todo.getDeadline())
                .done(todo.isDone())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }
}
