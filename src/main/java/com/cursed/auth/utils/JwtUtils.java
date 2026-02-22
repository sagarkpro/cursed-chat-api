package com.cursed.auth.utils;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.cursed.auth.entities.User;
import com.cursed.auth.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    private final SecretKey secretKey;
    private final long expirationMillis;
    private final UserRepository userRepository;

    public JwtUtils(@Value("${spring.JWT_SECRET}") String base64Secret,
            @Value("${spring.JWT_EXPIRY}") long expirationMillis, UserRepository userRepository) {
        this.secretKey = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(base64Secret));
        this.expirationMillis = expirationMillis;
        this.userRepository = userRepository;
    }

    public String generateToken(User user) {
        return generateTimedToken(user, expirationMillis);
    }

    public String generateShortLivedToken(User user) {
        long tenMins = 10L * 60 * 1000;
        return generateTimedToken(user, tenMins);
    }

    public boolean verifySignature(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException _) {
            return false;
        }
    }

    public User getUserFromToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("Missing JWT token");
        }

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String userEmail = claims.getSubject();
        var user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("Invalid email or password");
        }
        return user;
    }

    public static Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var user = auth != null ? (User) auth.getPrincipal() : null;
        return Optional.of(user);
    }

    private String generateTimedToken(User user, long life) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(life)))
                .claims(Map.of(
                        "role", user.getRole().name(),
                        "userId", user.getId()))
                .signWith(secretKey)
                .compact();
    }
}
