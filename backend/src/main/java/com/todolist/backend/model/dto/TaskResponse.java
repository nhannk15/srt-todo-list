package com.todolist.backend.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Long priority;
    private boolean isDone;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime doneAt;

}
