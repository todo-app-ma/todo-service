package com.todoappma.todoservice.grpc;

import com.todoappma.todoservice.dto.request.CreateTodoRequestDto;
import com.todoappma.todoservice.dto.request.UpdateTodoRequestDto;
import com.todoappma.todoservice.dto.response.TodoResponseDto;
import com.todoappma.todoservice.exception.TodoException;
import com.todoappma.todoservice.service.TodoService;
import com.todoappma.proto.todo.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class TodoServiceGrpcHandler extends TodoServiceGrpc.TodoServiceImplBase {

    private final TodoService todoService;

    @Override
    public void createTodo(CreateTodoRequestGrpc request, StreamObserver<TodoResponseGrpc> responseObserver) {
        try {
            var result = todoService.createTodo(CreateTodoRequestDto.builder()
                    .userId(UUID.fromString(request.getUserId()))
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .deadline(LocalDateTime.parse(request.getDeadline()))
                    .build());
            responseObserver.onNext(toGrpc(result));
            responseObserver.onCompleted();
        } catch (TodoException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getErrorCode() + ":" + e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getTodo(GetTodoRequestGrpc request, StreamObserver<TodoResponseGrpc> responseObserver) {
        try {
            var result = todoService.getTodo(UUID.fromString(request.getTodoId()), UUID.fromString(request.getUserId()));
            responseObserver.onNext(toGrpc(result));
            responseObserver.onCompleted();
        } catch (TodoException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getErrorCode() + ":" + e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAllTodos(GetAllTodosRequestGrpc request, StreamObserver<GetAllTodosResponseGrpc> responseObserver) {
        var results = todoService.getAllTodos(UUID.fromString(request.getUserId()));
        var response = GetAllTodosResponseGrpc.newBuilder()
                .addAllTodos(results.stream().map(this::toGrpc).toList())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateTodo(UpdateTodoRequestGrpc request, StreamObserver<TodoResponseGrpc> responseObserver) {
        try {
            var result = todoService.updateTodo(UpdateTodoRequestDto.builder()
                    .todoId(UUID.fromString(request.getTodoId()))
                    .userId(UUID.fromString(request.getUserId()))
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .deadline(LocalDateTime.parse(request.getDeadline()))
                    .build());
            responseObserver.onNext(toGrpc(result));
            responseObserver.onCompleted();
        } catch (TodoException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getErrorCode() + ":" + e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteTodo(DeleteTodoRequestGrpc request, StreamObserver<DeleteTodoResponseGrpc> responseObserver) {
        try {
            todoService.deleteTodo(UUID.fromString(request.getTodoId()), UUID.fromString(request.getUserId()));
            responseObserver.onNext(DeleteTodoResponseGrpc.newBuilder().setSuccess(true).build());
            responseObserver.onCompleted();
        } catch (TodoException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getErrorCode() + ":" + e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void markTodoDone(MarkTodoDoneRequestGrpc request, StreamObserver<TodoResponseGrpc> responseObserver) {
        try {
            var result = todoService.markDone(UUID.fromString(request.getTodoId()), UUID.fromString(request.getUserId()));
            responseObserver.onNext(toGrpc(result));
            responseObserver.onCompleted();
        } catch (TodoException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getErrorCode() + ":" + e.getMessage()).asRuntimeException());
        }
    }

    private TodoResponseGrpc toGrpc(TodoResponseDto dto) {
        return TodoResponseGrpc.newBuilder()
                .setTodoId(dto.getTodoId().toString())
                .setUserId(dto.getUserId().toString())
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription() != null ? dto.getDescription() : "")
                .setDeadline(dto.getDeadline().toString())
                .setDone(dto.isDone())
                .setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().toString() : "")
                .setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt().toString() : "")
                .build();
    }
}
