package com.todolist.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todolist.backend.model.dto.CreateTaskRequest;
import com.todolist.backend.model.dto.TaskResponse;
import com.todolist.backend.model.dto.UpdateTaskRequest;
import com.todolist.backend.model.entity.MyUser;
import com.todolist.backend.model.entity.Task;
import com.todolist.backend.repository.MyUserRepository;
import com.todolist.backend.repository.TaskRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final MyUserRepository myUserRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, MyUserRepository myUserRepository) {
        this.taskRepository = taskRepository;
        this.myUserRepository = myUserRepository;
    }

    public void createNewTask(String email, CreateTaskRequest request) {
        log.info("createNewTask() - user email: {}", email);
        String title = request.getTitle();
        String description = request.getDescription();
        Long priority = request.getPriority();
        log.info("createNewTask() - title: {}", title);
        log.info("createNewTask() - desciprion: {}", description);
        log.info("createNewTask() - priority: {}", priority);


        MyUser user = myUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Task newTask = Task
                .builder()
                .title(title)
                .description(description)
                .priority(priority)
                .user(user)
                .build();
        taskRepository.save(newTask);
    }

    public List<TaskResponse> getAllUserUndoneTasks(String email) {
        log.info("getAllCustomerUndoneTasks() - email: {}", email);
        MyUser user = myUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Task> undoneTasks = taskRepository.findUserUndoneTasks(user.getId());
        List<TaskResponse> response = undoneTasks
                .stream()
                .map((task) -> {
                    TaskResponse mappedTask = TaskResponse
                            .builder()
                            .id(task.getId())
                            .title(task.getTitle())
                            .description(task.getDescription())
                            .priority(task.getPriority())
                            .isDone(task.isDone())
                            .isActive(task.isActive())
                            .createdAt(task.getCreatedAt())
                            .doneAt(task.getDoneAt())
                            .build();
                    return mappedTask;
                })
                .toList();
        return response;
    }

    public List<TaskResponse> getAllUserDoneTasks(String email) {
        log.info("getAllCustomerUndoneTasks() - email: {}", email);
        MyUser user = myUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Task> undoneTasks = taskRepository.findUserDoneTasks(user.getId());
        List<TaskResponse> response = undoneTasks
                .stream()
                .map((task) -> {
                    TaskResponse mappedTask = TaskResponse
                            .builder()
                            .id(task.getId())
                            .title(task.getTitle())
                            .description(task.getDescription())
                            .priority(task.getPriority())
                            .isDone(task.isDone())
                            .isActive(task.isActive())
                            .createdAt(task.getCreatedAt())
                            .doneAt(task.getDoneAt())
                            .build();
                    return mappedTask;
                })
                .toList();
        return response;
    }

    public List<TaskResponse> getAllTasks(String email) {
        log.info("getAllTasks() - email: {}", email);
        MyUser user = myUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Task> undoneTasks = taskRepository.findAllTasks(user.getId());
        List<TaskResponse> response = undoneTasks
                .stream()
                .map((task) -> {
                    TaskResponse mappedTask = TaskResponse
                            .builder()
                            .id(task.getId())
                            .title(task.getTitle())
                            .description(task.getDescription())
                            .priority(task.getPriority())
                            .isDone(task.isDone())
                            .isActive(task.isActive())
                            .createdAt(task.getCreatedAt())
                            .doneAt(task.getDoneAt())
                            .build();
                    return mappedTask;
                })
                .toList();
        return response;
    }

    public TaskResponse markAsDone(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (task.isDone() == true) {
            throw new RuntimeException("Can't mark done task done");
        }

        task.setDone(true);
        task.setDoneAt(LocalDateTime.now());
        taskRepository.save(task);

        TaskResponse response = TaskResponse
                .builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .isDone(task.isDone())
                .isActive(task.isActive())
                .createdAt(task.getCreatedAt())
                .doneAt(task.getDoneAt())
                .build();
        return response;
    }

    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        String title = request.getTitle();
        String description = request.getDescription();
        Long priority = request.getPriority();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        taskRepository.save(task);

        TaskResponse response = TaskResponse
                .builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .isDone(task.isDone())
                .isActive(task.isActive())
                .createdAt(task.getCreatedAt())
                .doneAt(task.getDoneAt())
                .build();
        return response;
    }

    public TaskResponse deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (task.isActive() == false) {
            throw new RuntimeException("Can't delete deleted task");
        }

        task.setActive(false);
        taskRepository.save(task);

        TaskResponse response = TaskResponse
                .builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .isDone(task.isDone())
                .isActive(task.isActive())
                .createdAt(task.getCreatedAt())
                .doneAt(task.getDoneAt())
                .build();
        return response;
    }

}
