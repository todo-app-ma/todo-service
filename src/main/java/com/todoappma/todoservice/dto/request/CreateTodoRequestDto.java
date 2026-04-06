package com.todoappma.todoservice.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateTodoRequestDto {
    private UUID userId;
    private String title;
    private String description;
    private LocalDateTime deadline;
}
