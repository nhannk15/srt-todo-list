package com.todolist.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todolist.backend.model.dto.UserInfoResponse;
import com.todolist.backend.service.MyUserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class AuthenticationController {

    private final MyUserService myUserService;

    @Autowired
    public AuthenticationController(MyUserService myUserService) {
        this.myUserService = myUserService;
    }

    @GetMapping("/api/me")
    public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal String email) {
        return ResponseEntity.status(HttpStatus.OK).body(myUserService.getMyInfo(email));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. Xóa session (nếu có)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 2. Xóa SecurityContext
        SecurityContextHolder.clearContext();

        // 3. Xóa Access Token cookie
        Cookie accessTokenCookie = new Cookie("access_token", null);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // Nếu dùng HTTPS
        accessTokenCookie.setMaxAge(0); // Xóa ngay
        response.addCookie(accessTokenCookie);

        // 4. Xóa Refresh Token cookie
        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);

        // 5. Xóa JSESSIONID cookie (nếu có)
        Cookie sessionCookie = new Cookie("JSESSIONID", null);
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);

        return ResponseEntity.ok()
                .body(Map.of(
                        "message", "Logged out successfully",
                        "status", "success"));
    }

}
