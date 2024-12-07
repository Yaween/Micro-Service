package com.epic.user_service.util;

import java.security.SecureRandom;

public class OTPGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public static String generateOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10)); // Generate digits 0-9
        }
        return otp.toString();
    }
}
