package com.cursed.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cursed.chat.entities.Message;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findBySenderId(String senderId);
}
