package com.cursed.chat.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursed.chat.dtos.LoginDTO;
import com.cursed.chat.dtos.RegisterDTO;
import com.cursed.chat.dtos.VerifyOTPDTO;
import com.cursed.chat.dtos.response.BaseResponseDTO;
import com.cursed.chat.dtos.response.LoginResponseDTO;
import com.cursed.chat.dtos.response.RegisterResponseDTO;
import com.cursed.chat.entities.User;
import com.cursed.chat.services.UserService;
import com.cursed.chat.utils.CommonUtils;

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
    public ResponseEntity<BaseResponseDTO<List<User>>> getAllUserResponseEntity() {
        return CommonUtils.handleResponse(userService.findAllUsers());
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponseDTO<LoginResponseDTO>> login(@RequestBody LoginDTO request) {
        return CommonUtils.handleResponse(userService.login(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponseDTO<RegisterResponseDTO>> register(@RequestBody @Valid RegisterDTO request) {
        return CommonUtils.handleResponse(userService.register(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<BaseResponseDTO<LoginResponseDTO>> verifyOtp(@RequestBody @Valid VerifyOTPDTO request) {
        return CommonUtils.handleResponse(userService.verifyOtp(request));
    }
}
