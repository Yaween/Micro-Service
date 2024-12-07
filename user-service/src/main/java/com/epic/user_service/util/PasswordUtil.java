package com.epic.user_service.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PasswordUtil {
    public static String encodeToBase64(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
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

//        String specialCharacters = ".*[^a-zA-Z0-9].*";
//        String upperCaseLetters = ".*[A-Z].*";
//        String lowerCaseLetters = ".*[a-z].*";
//        String numbers = ".*[0-9].*";
//
//        if(password.matches(specialCharacters) && password.matches(upperCaseLetters)
//                && password.matches(lowerCaseLetters) && password.matches(numbers)){
//            log.info("Password is valid");
//            return "Valid";
//
//        } else if(password.matches(specialCharacters) && password.matches(upperCaseLetters)
//                && password.matches(lowerCaseLetters) && !password.matches(numbers)){
//            log.info("No numeric");
//            return "Numeric";
//
//        }else if (password.matches(specialCharacters) && password.matches(upperCaseLetters)
//                && !password.matches(lowerCaseLetters) && password.matches(numbers)){
//            log.info("No Lowercase");
//            return "Lowercase";
//
//        } else if (password.matches(specialCharacters) && !password.matches(upperCaseLetters)
//                && password.matches(lowerCaseLetters) && password.matches(numbers)) {
//            log.info("No Uppercase");
//            return "Uppercase";
//
//        } else if (!password.matches(specialCharacters) && password.matches(upperCaseLetters)
//                && password.matches(lowerCaseLetters) && password.matches(numbers)) {
//            log.info("No Special");
//            return "Special";
//
//        } else if (!password.matches(specialCharacters) && !password.matches(upperCaseLetters)
//                && password.matches(lowerCaseLetters) && password.matches(numbers)) {
//            log.info("No Special & No Uppercase");
//            return "Special & Uppercase";
//
//        } else if (!password.matches(specialCharacters) && password.matches(upperCaseLetters)
//                && !password.matches(lowerCaseLetters) && password.matches(numbers)) {
//            log.info("No Special & lowercase");
//            return "Special & Lowercase";
//
//        } else if (!password.matches(specialCharacters) && password.matches(upperCaseLetters)
//                && password.matches(lowerCaseLetters) && !password.matches(numbers)) {
//            log.info("No Special & Numeric");
//            return "Special & Numeric";
//
//        } else if (password.matches(specialCharacters) && !password.matches(upperCaseLetters)
//                && !password.matches(lowerCaseLetters) && password.matches(numbers)) {
//            log.info("No Uppercase & Lowercase");
//            return "Uppercase & Lowercase";
//
//        } else if (password.matches(specialCharacters) && !password.matches(upperCaseLetters)
//                && password.matches(lowerCaseLetters) && !password.matches(numbers)) {
//            log.info("No Uppercase & Numeric");
//            return "Uppercase & Numeric";
//
//        }else if (password.matches(specialCharacters) && password.matches(upperCaseLetters)
//                && !password.matches(lowerCaseLetters) && !password.matches(numbers)) {
//            log.info("No Lowercase & Numeric");
//            return "Lowercase & Numeric";
//
//        } else if (password.matches(specialCharacters) && !password.matches(upperCaseLetters)
//                && !password.matches(lowerCaseLetters) && !password.matches(numbers)) {
//            log.info("No uppercase & lowercase & numeric");
//            return "Uppercase & Lowercase & numeric";
//
//        }else if(!password.matches(specialCharacters) && password.matches(upperCaseLetters)
//                && !password.matches(lowerCaseLetters) && !password.matches(numbers)){
//            log.info("No Special & lowercase & numeric");
//            return "Special & Lowercase & Numeric";
//
//        } else if (!password.matches(specialCharacters) && !password.matches(upperCaseLetters)
//                && password.matches(lowerCaseLetters) && !password.matches(numbers)) {
//            log.info("No special & uppercase & numeric");
//            return "Special & Uppercase & Numeric";
//
//        } else if (!password.matches(specialCharacters) && !password.matches(upperCaseLetters)
//                && !password.matches(lowerCaseLetters) && password.matches(numbers)) {
//            log.info("No special & uppercase & lowercase");
//            return "Special & Uppercase & Lowercase";
//
//        }else {
//            log.info("Missing any character");
//            return "Invalid";
//        }
    }
}
