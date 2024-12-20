package com.epic.user_service.util;

import java.util.UUID;

public class UniqueIdGenerator {

    public static String generateUniqueId() {
        String shortUuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String timeBase36 = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
        return shortUuid + timeBase36.substring(0, 4);
    }
}
