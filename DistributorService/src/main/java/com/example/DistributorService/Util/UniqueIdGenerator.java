package com.example.DistributorService.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class UniqueIdGenerator {

    public static String generateUniqueId() {
        // Generate a random UUID, remove dashes, and take the first 12 characters
        String shortUuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        // Get the current time in milliseconds and encode it in Base36
        String timeBase36 = Long.toString(System.currentTimeMillis(), 36).toUpperCase();

        // Combine the short UUID and Base36 timestamp to create a 16-character ID
        return shortUuid + timeBase36.substring(0, 4); // 12 (UUID) + 4 (Base36 time)
    }

    public static String generateDistributorId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String shortTimePart = timestamp.substring(timestamp.length() - 4);
        int randomDigit = new Random().nextInt(10);

        return "D" + shortTimePart + randomDigit;
    }
}


