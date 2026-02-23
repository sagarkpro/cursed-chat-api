package com.cursed.chat.services;

import java.util.List;

import com.cursed.chat.dtos.SendMessageDTO;
import com.cursed.chat.dtos.response.BaseResponseDTO;
import com.cursed.chat.entities.Message;

public interface MessageService {
    BaseResponseDTO<List<Message>> getAllMessages(String senderId);

    BaseResponseDTO<Message> sendMessage(SendMessageDTO request);
}
