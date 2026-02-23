package com.cursed.chat.services;

import java.util.List;
import java.util.Optional;

import com.cursed.chat.dtos.RegisterDTO;
import com.cursed.chat.dtos.VerifyOTPDTO;
import com.cursed.chat.dtos.response.BaseResponseDTO;
import com.cursed.chat.dtos.response.LoginResponseDTO;
import com.cursed.chat.dtos.response.RegisterResponseDTO;
import com.cursed.chat.entities.User;

public interface UserService {
    BaseResponseDTO<RegisterResponseDTO> register(RegisterDTO request);

    BaseResponseDTO<LoginResponseDTO> verifyOtp(VerifyOTPDTO request);

    BaseResponseDTO<User> getUserByEmail(String email);

    BaseResponseDTO<LoginResponseDTO> login(String email, String password);

    BaseResponseDTO<List<User>> findAllUsers();

}
