package com.cursed.chat.services;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cursed.chat.clients.BrevoEmailClient;
import com.cursed.chat.constants.RedisKeys;
import com.cursed.chat.dtos.RegisterDTO;
import com.cursed.chat.dtos.VerifyOTPDTO;
import com.cursed.chat.dtos.response.BaseResponseDTO;
import com.cursed.chat.dtos.response.ErrorDTO;
import com.cursed.chat.dtos.response.LoginResponseDTO;
import com.cursed.chat.dtos.response.RegisterResponseDTO;
import com.cursed.chat.entities.User;
import com.cursed.chat.logging.CursedLogger;
import com.cursed.chat.records.RedisOTPVerification;
import com.cursed.chat.records.SendOTPVerificationMail;
import com.cursed.chat.repository.UserRepository;
import com.cursed.chat.utils.JwtUtils;

import tools.jackson.databind.ObjectMapper;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final BrevoEmailClient brevoEmailClient;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtils jwtUtils,
            RedisService redisService, ObjectMapper objectMapper, BrevoEmailClient brevoEmailClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.redisService = redisService;
        this.objectMapper = objectMapper;
        this.brevoEmailClient = brevoEmailClient;
    }

    public BaseResponseDTO<RegisterResponseDTO> register(RegisterDTO request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return BaseResponseDTO.<RegisterResponseDTO>builder()
                        .success(false)
                        .error(ErrorDTO.builder()
                                .message("User already exists")
                                .build())
                        .build();
            }
            if (userRepository.existsByUsername(request.getUsername())) {
                return BaseResponseDTO.<RegisterResponseDTO>builder()
                        .success(false)
                        .error(ErrorDTO.builder()
                                .message("This username is taken by another user")
                                .build())
                        .build();
            }
            User user = User.builder()
                    .createdAt(Instant.now())
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .middleName(request.getMiddleName())
                    .lastName(request.getLastName())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .build();

            User res = userRepository.save(user);

            String otp = RandomStringUtils.secure().nextAlphanumeric(6).toUpperCase();
            CursedLogger.info("OTP for user " + user.getEmail() + " is: " + otp);
            redisService.save(user.getEmail() + RedisKeys.OTP_VERIFICATION, objectMapper.valueToTree(
                    new RedisOTPVerification(otp, user.getEmail())));

            sendEmailVerificationOtp(user, otp);
            return BaseResponseDTO.<RegisterResponseDTO>builder()
                    .success(true)
                    .data(RegisterResponseDTO.builder()
                            .email(res.getEmail())
                            .id(res.getId())
                            .build())
                    .build();
        } catch (Exception e) {
            CursedLogger.error(e.getMessage(), e.getCause());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public BaseResponseDTO<User> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return BaseResponseDTO.<User>builder()
                .data(user)
                .success(true)
                .build();
    }

    @Override
    public BaseResponseDTO<LoginResponseDTO> login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return BaseResponseDTO.<LoginResponseDTO>builder()
                    .success(false)
                    .error(ErrorDTO
                            .builder()
                            .message("Invalid email/password")
                            .build())
                    .build();
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            if (!user.isVerified()) {
                return BaseResponseDTO.<LoginResponseDTO>builder()
                        .success(false)
                        .error(ErrorDTO
                                .builder()
                                .message("Email not verified")
                                .build())
                        .build();
            }
            if (!user.isActive()) {
                return BaseResponseDTO.<LoginResponseDTO>builder()
                        .success(false)
                        .error(ErrorDTO
                                .builder()
                                .message("User is disabled")
                                .build())
                        .build();
            }
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
    public BaseResponseDTO<List<User>> findAllUsers() {
        return BaseResponseDTO.<List<User>>builder()
                .data(userRepository.findAll())
                .success(true)
                .build();
    }

    @Override
    public BaseResponseDTO<LoginResponseDTO> verifyOtp(VerifyOTPDTO request) {
        var user = userRepository.findByEmail(request.getEmail());
        if (user != null && !user.isVerified()) {
            var otpVerification = redisService.getJson(user.getEmail() + RedisKeys.OTP_VERIFICATION);
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
            } else {
                return BaseResponseDTO.<LoginResponseDTO>builder()
                        .error(ErrorDTO.builder()
                                .message("Invalid OTP").build())
                        .build();
            }
        }
        return BaseResponseDTO.<LoginResponseDTO>builder()
                .error(ErrorDTO.builder()
                        .message("Failed to verify user").build())
                .build();
    }

    private boolean sendEmailVerificationOtp(User user, String otp) {
        var request = new SendOTPVerificationMail(user.getEmail(), user.getDisplayName(), otp);
        var response = brevoEmailClient.sendEmailVerificationOTP(request);
        if (response != null && !response.get("messageId").isEmpty()) {
            return true;
        }
        return false;
    }
}
