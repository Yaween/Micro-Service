package com.epic.user_service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PasswordUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encodePassword(String password) {
        return encoder.encode(password);
    }

    public static boolean isPasswordValid(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    public static String passwordPolicyChecker(String password) {
        Map<String, String> patterns = new LinkedHashMap<>();
        patterns.put("Special", ".*[^a-zA-Z0-9].*");
        patterns.put("Uppercase", ".*[A-Z].*");
        patterns.put("Lowercase", ".*[a-z].*");
        patterns.put("Numeric", ".*[0-9].*");

        // Find missing categories
        String missing = patterns.entrySet().stream()
                .filter(entry -> !password.matches(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(" & "));

        // Build the response
        if (missing.isEmpty()) {
            log.info("Password is valid");
            return "Valid";
        } else {
            log.info("Missing categories: " + missing);
            return missing;
        }

    }
}
