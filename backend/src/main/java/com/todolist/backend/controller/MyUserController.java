package com.todolist.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.todolist.backend.model.dto.CreateUserRequest;
import com.todolist.backend.service.MyUserService;

@RestController
public class MyUserController {

    private final MyUserService myUserService;

    @Autowired
    public MyUserController(MyUserService myUserService) {
        this.myUserService = myUserService;
    }

    @PostMapping("/api/users")
    public ResponseEntity<Void> createNewUser(@RequestBody CreateUserRequest request) {
        myUserService.createNewUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
