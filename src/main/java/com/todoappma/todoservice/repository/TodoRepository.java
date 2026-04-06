package com.todoappma.todoservice.repository;

import com.todoappma.todoservice.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoRepository extends JpaRepository<Todo, UUID> {
    List<Todo> findByUserId(UUID userId);
    List<Todo> findByUserIdAndDone(UUID userId, boolean done);
    Optional<Todo> findByIdAndUserId(UUID id, UUID userId);
}
