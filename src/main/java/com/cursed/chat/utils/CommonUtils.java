package com.cursed.chat.utils;

import org.springframework.http.ResponseEntity;

import com.cursed.chat.dtos.response.BaseResponseDTO;

public class CommonUtils {
    private CommonUtils() {
        /* This utility class should not be instantiated */
    }

    public static <T> ResponseEntity<BaseResponseDTO<T>> handleResponse(BaseResponseDTO<T> response) {
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        if (response.getError() != null && response.getError().getStatus() != null) {
            return ResponseEntity.status(response.getError().getStatus()).body(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}
