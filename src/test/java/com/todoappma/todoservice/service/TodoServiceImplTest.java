package com.todoappma.todoservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoappma.todoservice.dto.request.CreateTodoRequestDto;
import com.todoappma.todoservice.entity.Todo;
import com.todoappma.todoservice.exception.TodoException;
import com.todoappma.todoservice.repository.OutboxRepository;
import com.todoappma.todoservice.repository.TodoRepository;
import com.todoappma.todoservice.service.impl.TodoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock private TodoRepository todoRepository;
    @Mock private OutboxRepository outboxRepository;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    @Test
    void createTodo_shouldSaveTodoAndWriteOutboxEvent() throws Exception {
        var userId = UUID.randomUUID();
        var request = CreateTodoRequestDto.builder()
                .userId(userId)
                .title("Buy groceries")
                .description("Milk, eggs, bread")
                .deadline(LocalDateTime.now().plusDays(1))
                .build();

        var savedTodo = Todo.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(todoRepository.save(any())).thenReturn(savedTodo);

        var response = todoService.createTodo(request);

        assertThat(response.getTitle()).isEqualTo("Buy groceries");
        verify(outboxRepository).save(any());
    }

    @Test
    void getTodo_shouldThrow_whenNotFound() {
        var todoId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        when(todoRepository.findByIdAndUserId(todoId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.getTodo(todoId, userId))
                .isInstanceOf(TodoException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getAllTodos_shouldReturnUserTodos() {
        var userId = UUID.randomUUID();
        var todo = Todo.builder().userId(userId).title("Test").deadline(LocalDateTime.now().plusDays(1)).build();
        when(todoRepository.findByUserId(userId)).thenReturn(List.of(todo));

        var result = todoService.getAllTodos(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test");
    }

    @Test
    void markDone_shouldSetDoneAndWriteOutboxEvent() throws Exception {
        var todoId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var todo = Todo.builder().userId(userId).title("Test").deadline(LocalDateTime.now().plusDays(1)).done(false).build();

        when(todoRepository.findByIdAndUserId(todoId, userId)).thenReturn(Optional.of(todo));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(todoRepository.save(any())).thenReturn(todo);

        var result = todoService.markDone(todoId, userId);

        assertThat(todo.isDone()).isTrue();
        verify(outboxRepository).save(any());
    }
}
