package com.todolist.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.todolist.backend.model.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    

    @Query("""
            SELECT task
            FROM Task task
            JOIN task.user user
            WHERE user.id = :userId
            AND isDone = FALSE
            AND isActive = TRUE
            ORDER BY task.id DESC
            """)
    List<Task> findUserUndoneTasks(@Param("userId") Long userId);

    @Query("""
            SELECT task
            FROM Task task
            JOIN task.user user
            WHERE user.id = :userId
            AND isDone = TRUE
            AND isActive = TRUE
            ORDER BY task.id DESC
            """)
    List<Task> findUserDoneTasks(@Param("userId") Long userId);

    @Query("""
            SELECT task
            FROM Task task
            JOIN task.user user
            WHERE user.id = :userId
            AND isActive = TRUE
            ORDER BY task.id DESC
            """)
    List<Task> findAllTasks(@Param("userId") Long userId);
}
