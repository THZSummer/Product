package com.openapp.application.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.user.name:admin}")
    private String adminUsername;

    @Value("${spring.security.user.password:changeme}")
    private String adminPassword;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        if (!adminUsername.equals(request.getUsername()) || 
            !adminPassword.equals(request.getPassword())) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", 401);
            error.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(error);
        }
        
        String userId = "user-" + System.currentTimeMillis();
        String token = jwtTokenProvider.generateToken(userId, request.getUsername(), "ADMIN");
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "登录成功");
        response.put("data", new LoginResponse(token, userId, request.getUsername(), "ADMIN"));
        
        log.info("Login successful for user: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private String userId;
        private String username;
        private String role;
    }
}
