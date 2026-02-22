package com.cursed.auth.services;

import java.util.List;
import java.util.Optional;

import com.cursed.auth.dtos.RegisterDTO;
import com.cursed.auth.dtos.VerifyOTPDTO;
import com.cursed.auth.dtos.response.BaseResponseDTO;
import com.cursed.auth.dtos.response.LoginResponseDTO;
import com.cursed.auth.dtos.response.RegisterResponseDTO;
import com.cursed.auth.entities.User;

public interface UserService {
    BaseResponseDTO<RegisterResponseDTO> register(RegisterDTO request);

    BaseResponseDTO<LoginResponseDTO> verifyOtp(VerifyOTPDTO request);

    Optional<User> getUserByEmail(String email);

    BaseResponseDTO<LoginResponseDTO> login(String email, String password);

    List<User> findAllUsers();

}
