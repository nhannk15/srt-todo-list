package com.todolist.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateTaskRequest {
    
    private String title;
    private String description;
    private Long priority;
    private boolean isDone;

}
