package com.cursed.chat.records;

public record SendOTPVerificationMail(
        String receiverEmail,
        String receiverName,
        String OTP) {
}
