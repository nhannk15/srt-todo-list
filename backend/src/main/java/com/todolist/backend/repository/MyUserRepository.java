package com.todolist.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todolist.backend.model.entity.MyUser;

public interface MyUserRepository extends JpaRepository<MyUser, Long> {
    
    Optional<MyUser> findByEmail(String email);

}
