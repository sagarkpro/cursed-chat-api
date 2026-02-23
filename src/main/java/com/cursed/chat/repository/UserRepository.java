package com.cursed.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cursed.chat.entities.User;

public interface UserRepository extends MongoRepository<User, String> {

    // custom query methods (optional)
    User findByEmail(String email);
}
