package com.todolist.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.todolist.backend.model.dto.CreateTaskRequest;
import com.todolist.backend.model.dto.TaskResponse;
import com.todolist.backend.model.dto.UpdateTaskRequest;
import com.todolist.backend.service.TaskService;

@RestController
public class TaskController {
    
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/api/tasks")
    public ResponseEntity<Void> createNewTask(@RequestBody CreateTaskRequest request) {
        taskService.createNewTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<List<TaskResponse>> getUndoneAllTasks(@RequestBody String email) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getAllUserUndoneTasks(email));
    }

    public record MarkAsDoneRequest(Long taskId) {
    }
    @PostMapping("/api/mark-as-done")
    public ResponseEntity<TaskResponse> markAsDone(@RequestBody MarkAsDoneRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.markAsDone(request.taskId));
    }

    @PutMapping("/api/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId, @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.updateTask(taskId, request));
    }

    @DeleteMapping("/api/tasks/{taskId}")
    public ResponseEntity<TaskResponse> deleteTask(@PathVariable Long taskId) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.deleteTask(taskId));
    }
    

}
