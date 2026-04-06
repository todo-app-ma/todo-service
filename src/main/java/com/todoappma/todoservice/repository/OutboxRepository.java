package com.todoappma.todoservice.repository;

import com.todoappma.todoservice.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
}
