package com.epic.user_service.util;

public class RequestNullChecker {

    //check the provided fields are empty
    public boolean isNullOrEmpty(String... fields) {
        for (String field : fields) {
            if (field == null || field.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
