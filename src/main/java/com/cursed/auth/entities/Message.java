package com.cursed.auth.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import com.cursed.auth.entities.enums.ReceiverType;
import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Document(collection = "messages")
public class Message extends BaseEntity {
    @NonNull
    String content;
    @NonNull
    String senderId;
    @NonNull
    String receiverId;
    @NonNull
    ReceiverType receiverType;
}
