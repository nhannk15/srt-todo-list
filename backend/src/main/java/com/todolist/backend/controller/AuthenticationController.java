package com.todolist.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todolist.backend.model.dto.UserInfoResponse;
import com.todolist.backend.service.MyUserService;

@RestController
public class AuthenticationController {

    private final MyUserService myUserService;

    @Autowired
    public AuthenticationController(MyUserService myUserService) {
        this.myUserService = myUserService;
    }

    @GetMapping
    public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal String email) {
        return ResponseEntity.status(HttpStatus.OK).body(myUserService.getMyInfo(email));
    }

}
