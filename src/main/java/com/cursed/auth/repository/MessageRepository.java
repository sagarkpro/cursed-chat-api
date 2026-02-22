package com.cursed.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cursed.auth.entities.Message;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findBySenderId(String senderId);
}
