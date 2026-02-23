package com.cursed.chat.records;

public record RedisOTPVerification(
                String otp,
                String email,
                String verificationToken) {
}
