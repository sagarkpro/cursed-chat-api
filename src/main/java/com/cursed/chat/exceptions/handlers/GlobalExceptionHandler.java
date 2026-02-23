package com.cursed.chat.exceptions.handlers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cursed.chat.dtos.response.BaseResponseDTO;
import com.cursed.chat.dtos.response.ErrorDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<BaseResponseDTO> handleAllExceptions(Exception ex) {

                log.error("Unhandled exception occurred", ex);

                ErrorDTO error = ErrorDTO.builder()
                                .code("INTERNAL_SERVER_ERROR")
                                .message(ex.getMessage())
                                .details(ex.getClass().getSimpleName())
                                .timestamp(LocalDateTime.now())
                                .build();

                BaseResponseDTO response = BaseResponseDTO.builder()
                                .success(false)
                                .error(error)
                                .build();

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }
}
