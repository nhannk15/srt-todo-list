package com.todolist.backend.service;

import org.springframework.stereotype.Service;

import com.todolist.backend.model.dto.CreateUserRequest;
import com.todolist.backend.model.dto.UserInfoResponse;
import com.todolist.backend.model.entity.MyUser;
import com.todolist.backend.repository.MyUserRepository;

@Service
public class MyUserService {

    private final MyUserRepository myUserRepository;

    public MyUserService(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    public void createNewUser(CreateUserRequest request) {

        String fullname = request.getFullname();
        String googleId = request.getGoogleId();
        String email = request.getEmail();

        MyUser persitedUser = myUserRepository.findByEmail(email)
                .orElse(null);
        if (persitedUser == null) {
            MyUser newUser = MyUser
                    .builder()
                    .fullname(fullname)
                    .email(email)
                    .googleId(googleId)
                    .build();

            myUserRepository.save(newUser);
        } else {
            throw new RuntimeException("Email duplicated");
        }

    }

    public UserInfoResponse getMyInfo(String email) {
        MyUser user = myUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserInfoResponse
                .builder()
                .fullname(user.getFullname())
                .email(user.getFullname())
                .id(user.getId())
                .build();
    }

}
