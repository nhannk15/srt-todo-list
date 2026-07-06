package com.todolist.backend.service;

import com.todolist.backend.model.dto.CreateTaskRequest;
import com.todolist.backend.model.dto.TaskResponse;
import com.todolist.backend.model.dto.UpdateTaskRequest;
import com.todolist.backend.model.entity.MyUser;
import com.todolist.backend.model.entity.Task;
import com.todolist.backend.repository.MyUserRepository;
import com.todolist.backend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MyUserRepository myUserRepository;

    @InjectMocks
    private TaskService taskService;

    private MyUser testUser;
    private Task testTask;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = MyUser.builder()
                .id(1L)
                .email(testEmail)
                .fullname("Test User")
                .googleId("google123")
                .refreshToken("refresh123")
                .build();

        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .priority(1L)
                .isDone(false)
                .isActive(true)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .doneAt(null)
                .build();
    }

    @Test
    void createNewTask_ShouldSaveTask_WhenValidRequest() {
        // Given
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("New Task")
                .description("New Description")
                .priority(2L)
                .build();

        when(myUserRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        taskService.createNewTask(testEmail, request);

        // Then
        verify(myUserRepository, times(1)).findByEmail(testEmail);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createNewTask_ShouldThrowException_WhenUserNotFound() {
        // Given
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("New Task")
                .description("New Description")
                .priority(1L)
                .build();

        when(myUserRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.createNewTask(testEmail, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getAllUserUndoneTasks_ShouldReturnListOfUndoneTasks() {
        // Given
        when(myUserRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.findUserUndoneTasks(testUser.getId())).thenReturn(List.of(testTask));

        // When
        List<TaskResponse> result = taskService.getAllUserUndoneTasks(testEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Task");
        assertThat(result.get(0).isDone()).isFalse();
        assertThat(result.get(0).isActive()).isTrue();

        verify(myUserRepository, times(1)).findByEmail(testEmail);
        verify(taskRepository, times(1)).findUserUndoneTasks(testUser.getId());
    }

    @Test
    void getAllUserDoneTasks_ShouldReturnListOfDoneTasks() {
        // Given
        Task doneTask = Task.builder()
                .id(2L)
                .title("Done Task")
                .description("Done Description")
                .priority(2L)
                .isDone(true)
                .isActive(true)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .doneAt(LocalDateTime.now())
                .build();

        when(myUserRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.findUserDoneTasks(testUser.getId())).thenReturn(List.of(doneTask));

        // When
        List<TaskResponse> result = taskService.getAllUserDoneTasks(testEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isDone()).isTrue();
        assertThat(result.get(0).getDoneAt()).isNotNull();

        verify(taskRepository, times(1)).findUserDoneTasks(testUser.getId());
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        // Given
        when(myUserRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.findAllTasks(testUser.getId())).thenReturn(List.of(testTask));

        // When
        List<TaskResponse> result = taskService.getAllTasks(testEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(taskRepository, times(1)).findAllTasks(testUser.getId());
    }

    @Test
    void markAsDone_ShouldMarkTaskAsDone_WhenTaskExists() {
        // Given
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse result = taskService.markAsDone(taskId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isDone()).isTrue();
        assertThat(result.getDoneAt()).isNotNull();

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void markAsDone_ShouldThrowException_WhenTaskAlreadyDone() {
        // Given
        Long taskId = 1L;
        testTask.setDone(true);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        // When & Then
        assertThatThrownBy(() -> taskService.markAsDone(taskId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Can't mark done task done");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void markAsDone_ShouldThrowException_WhenTaskNotFound() {
        // Given
        Long taskId = 999L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.markAsDone(taskId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Task not found");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldUpdateTask_WhenValidRequest() {
        // Given
        Long taskId = 1L;
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .priority(3L)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse result = taskService.updateTask(taskId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getPriority()).isEqualTo(3L);

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void updateTask_ShouldThrowException_WhenTaskNotFound() {
        // Given
        Long taskId = 999L;
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .priority(2L)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.updateTask(taskId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Task not found");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldSoftDeleteTask_WhenTaskExists() {
        // Given
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse result = taskService.deleteTask(taskId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isActive()).isFalse();

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskAlreadyDeleted() {
        // Given
        Long taskId = 1L;
        testTask.setActive(false);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Can't delete deleted task");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskNotFound() {
        // Given
        Long taskId = 999L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(taskId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Task not found");

        verify(taskRepository, never()).save(any(Task.class));
    }
}