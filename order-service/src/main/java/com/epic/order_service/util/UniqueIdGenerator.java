package com.epic.order_service.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class UniqueIdGenerator {
    public static String generateUniqueId() {
        String shortUuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String timeBase36 = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
        return shortUuid + timeBase36.substring(0, 4);
    }

    public static String generateRetailerId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String shortTimePart = timestamp.substring(timestamp.length() - 4);
        int randomDigit = new Random().nextInt(10);

        return "R" + shortTimePart + randomDigit;
    }
}
