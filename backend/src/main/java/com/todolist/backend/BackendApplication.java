package com.todolist.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.todolist.backend.model.entity.MyUser;
import com.todolist.backend.model.entity.Task;
import com.todolist.backend.repository.MyUserRepository;
import com.todolist.backend.repository.TaskRepository;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(MyUserRepository myUserRepository, TaskRepository taskRepository) {
        return args -> {
            MyUser newUser = MyUser
                    .builder()
                    .fullname("Nguyen Khac Le Nhan")
                    .email("nhannk15@gmail.com")
                    .googleId("googleId")
                    .build();
            myUserRepository.save(newUser);

            Task task1 = Task
                    .builder()
                    .title("Study Java")
                    .description("Study Java Spring Framework")
                    .user(newUser)
                    .priority(1L)
                    .build();
            taskRepository.save(task1);

            Task task2 = Task
                    .builder()
                    .title("Study React")
                    .description("Learn React hooks and state management")
                    .priority(1L)
                    .user(newUser)
                    .isDone(true)
                    .build();
            taskRepository.save(task2);

            Task task3 = Task
                    .builder()
                    .title("Complete Project")
                    .description("Finish the todolist project")
                    .priority(1L)
                    .user(newUser)
                    .build();
            taskRepository.save(task3);

            Task task4 = Task
                    .builder()
                    .title("Write Documentation")
                    .description("Write API documentation for the project")
                    .priority(1L)
                    .user(newUser)
                    .build();
            taskRepository.save(task4);

            Task task5 = Task
                    .builder()
                    .title("Deploy Application")
                    .description("Deploy to production server")
                    .priority(1L)
                    .user(newUser)
                    .isDone(true)
                    .build();
            taskRepository.save(task5);
        };
    }

}
