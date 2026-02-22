package com.cursed.auth.dtos;

import org.hibernate.validator.constraints.Length;

import com.cursed.auth.entities.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyOTPDTO {
    @NotBlank
    @Email
    String email;

    @NotBlank
    @Size(min = 6, max = 6)
    String otp;

    @NotBlank
    String verificationToken;
}
