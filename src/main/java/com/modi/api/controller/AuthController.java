package com.modi.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.modi.api.entity.User;
import com.modi.api.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody User user) {

        try {

            String msg =
                    authService.register(user);

            return ResponseEntity.ok(
                    Map.of("message", msg)
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User user) {

    try {

        User dbUser = authService.login(user);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login Successful",
                        "name", dbUser.getName(),
                        "email", dbUser.getEmail()
                )
        );

    } catch (Exception e) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "message",
                        e.getMessage()
                ));
    }
}
}