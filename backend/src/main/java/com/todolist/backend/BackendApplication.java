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

            Task newTask = Task
                    .builder()
                    .title("Study Java")
                    .description("Study Java Spring Framework")
                    .user(newUser)
                    .build();
            taskRepository.save(newTask);
        };
    }

}
