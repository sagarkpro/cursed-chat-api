package com.cursed.chat.clients;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.cursed.chat.logging.CursedLogger;
import com.cursed.chat.records.SendOTPVerificationMail;

import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailClient {

    private final String BREVO_URL;
    private final String API_KEY;
    private final RestClient restClient;

    public BrevoEmailClient(@Value("${spring.BREVO_URL}") String brevoUrl,
            @Value("${spring.BREVO_API_KEY}") String apiKey) {
        this.BREVO_URL = brevoUrl;
        this.API_KEY = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(BREVO_URL)
                .defaultHeader("accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("content-type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("api-key", API_KEY)
                .build();
    }

    public JsonNode sendEmailVerificationOTP(SendOTPVerificationMail request) {

        Map<String, Object> body = Map.of(
                "sender", Map.of(
                        "name", "Cursed Chat",
                        "email", "chat@cursedshrine.co.in"),
                "to", List.of(
                        Map.of(
                                "email", request.receiverEmail(),
                                "name", request.receiverName())),
                "subject", "Cursed Chat - Email Verification OTP",
                "htmlContent",
                String.format("<html><head></head><body><p>Hello, %s</p><h1>YOUR OTP IS %s</h1></body></html>",
                        request.receiverName(), request.OTP()));

        JsonNode response = restClient.post()
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        CursedLogger.info("SendVerificationOTPEmail API result:", response);
        return response;
    }
}
