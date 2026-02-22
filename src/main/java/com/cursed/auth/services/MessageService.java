package com.cursed.auth.services;

import java.util.List;

import com.cursed.auth.dtos.SendMessageDTO;
import com.cursed.auth.dtos.response.BaseResponseDTO;
import com.cursed.auth.entities.Message;

public interface MessageService {
    BaseResponseDTO<List<Message>> getAllMessages(String senderId);

    BaseResponseDTO<Message> sendMessage(SendMessageDTO request);
}
