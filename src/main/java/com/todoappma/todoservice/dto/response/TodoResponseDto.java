package com.todoappma.todoservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TodoResponseDto {
    private UUID todoId;
    private UUID userId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private boolean done;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
