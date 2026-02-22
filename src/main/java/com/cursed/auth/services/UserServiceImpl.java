package com.cursed.auth.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cursed.auth.dtos.RegisterDTO;
import com.cursed.auth.dtos.VerifyOTPDTO;
import com.cursed.auth.dtos.response.BaseResponseDTO;
import com.cursed.auth.dtos.response.ErrorDTO;
import com.cursed.auth.dtos.response.LoginResponseDTO;
import com.cursed.auth.dtos.response.RegisterResponseDTO;
import com.cursed.auth.entities.User;
import com.cursed.auth.records.RedisOTPVerification;
import com.cursed.auth.repository.UserRepository;
import com.cursed.auth.utils.JwtUtils;

import tools.jackson.databind.ObjectMapper;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtils jwtUtils,
            RedisService redisService, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
    }

    public BaseResponseDTO<RegisterResponseDTO> register(RegisterDTO request) {
        User user = User.builder()
                .createdAt(ZonedDateTime.now().toOffsetDateTime())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User res = userRepository.save(user);

        String verificationToken = jwtUtils.generateShortLivedToken(user);
        redisService.save(user.getEmail() + "-otpVerification", objectMapper.valueToTree(
                new RedisOTPVerification(RandomStringUtils.secure().nextAlphanumeric(6).toUpperCase(),
                        user.getEmail(),
                        verificationToken)));

        return BaseResponseDTO.<RegisterResponseDTO>builder()
                .success(true)
                .data(RegisterResponseDTO.builder()
                        .email(res.getEmail())
                        .id(res.getId())
                        .build())
                .build();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email is required");
        }
        User user = userRepository.findByEmail(email);
        return Optional.of(user);
    }

    @Override
    public BaseResponseDTO<LoginResponseDTO> login(String email, String password) {
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email is required");
        }
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("password is required");
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NoSuchElementException("Invalid email/password");
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtils.generateToken(user);
            return BaseResponseDTO.<LoginResponseDTO>builder()
                    .success(true)
                    .data(LoginResponseDTO.builder()
                            .accessToken(token)
                            .build())
                    .build();
        }
        return BaseResponseDTO.<LoginResponseDTO>builder()
                .success(false)
                .error(ErrorDTO
                        .builder()
                        .message("Invalid email/password")
                        .build())
                .build();
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public BaseResponseDTO<LoginResponseDTO> verifyOtp(VerifyOTPDTO request) {
        if (jwtUtils.verifySignature(request.getVerificationToken())) {
            var user = jwtUtils.getUserFromToken(request.getVerificationToken());
            if (user.getEmail().equals(request.getEmail())) {
                var otpVerification = redisService.getJson(user.getEmail() + "-otpVerification");
                var otpVerificationRecord = objectMapper.treeToValue(otpVerification, RedisOTPVerification.class);
                if (otpVerificationRecord.otp().equals(request.getOtp())) {
                    user.setVerified(true);
                    userRepository.save(user);
                    String accessToken = jwtUtils.generateToken(user);
                    return BaseResponseDTO.<LoginResponseDTO>builder()
                            .data(LoginResponseDTO.builder()
                                    .accessToken(accessToken)
                                    .build())
                            .build();
                }
            }
        }
        return BaseResponseDTO.<LoginResponseDTO>builder()
                .error(ErrorDTO.builder()
                        .message("Failed to verify user").build())
                .build();
    }
}
