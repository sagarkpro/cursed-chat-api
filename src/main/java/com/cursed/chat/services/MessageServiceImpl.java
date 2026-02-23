package com.cursed.chat.services;

import java.time.ZonedDateTime;
import java.util.List;

import com.cursed.chat.dtos.SendMessageDTO;
import com.cursed.chat.dtos.response.BaseResponseDTO;
import com.cursed.chat.dtos.response.ErrorDTO;
import com.cursed.chat.entities.Message;
import com.cursed.chat.repository.MessageRepository;
import com.cursed.chat.utils.JwtUtils;

public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public BaseResponseDTO<List<Message>> getAllMessages(String senderId) {
        var messages = messageRepository.findBySenderId(senderId);
        return BaseResponseDTO.<List<Message>>builder()
                .data(messages)
                .success(true)
                .build();
    }

    @Override
    public BaseResponseDTO<Message> sendMessage(SendMessageDTO request) {
        var currentUser = JwtUtils.getCurrentUser();
        if (currentUser.isPresent()) {
            var user = currentUser.get();
            var message = Message.builder()
                    .content(request.getContent())
                    .createdAt(ZonedDateTime.now().toOffsetDateTime())
                    .receiverId(request.getReceiverId())
                    .receiverType(request.getReceiverType())
                    .senderId(user.getId())
                    .build();

            messageRepository.save(message);
            return BaseResponseDTO.<Message>builder()
                    .data(message)
                    .success(true)
                    .build();
        } else {
            return BaseResponseDTO.<Message>builder()
                    .data(null)
                    .success(false)
                    .error(ErrorDTO.builder()
                            .message("Logged in user not found")
                            .build())
                    .build();
        }
    }

}
