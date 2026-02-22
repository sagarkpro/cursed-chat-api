package com.cursed.auth.dtos;

import com.cursed.auth.entities.enums.ReceiverType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMessageDTO {
    @NotBlank
    String content;
    @NotBlank
    String receiverId;
    @NotNull
    ReceiverType receiverType;
}
