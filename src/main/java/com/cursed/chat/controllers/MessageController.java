package com.cursed.chat.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    @GetMapping("/all")
    public ResponseEntity getAllMessages() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send")
    public ResponseEntity sendMessage() {
        return ResponseEntity.ok().build();
    }
}
