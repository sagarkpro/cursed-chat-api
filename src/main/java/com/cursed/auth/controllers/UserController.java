package com.cursed.auth.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursed.auth.dtos.LoginDTO;
import com.cursed.auth.dtos.RegisterDTO;
import com.cursed.auth.services.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity getAllUserResponseEntity() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO request) {
        return ResponseEntity.ok(userService.login(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO request) {
        return ResponseEntity.ok(userService.register(request));
    }
}
