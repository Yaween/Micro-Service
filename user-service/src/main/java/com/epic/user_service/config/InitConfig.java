package com.epic.user_service.config;

public class InitConfig {

    //response codes
    public static final String SUCCESS = "0000";

    public static final String MISSING_FIELDS_REGISTRATION = "1001";
    public static final String USERNAME_TAKEN = "1002";
    public static final String EMAIL_TAKEN = "1003";
    public static final String CONTACT_NO_TAKEN = "1004";
    public static final String PASSWORD_INVALID = "1005";
    public static final String REGISTRATION_FAILED = "1111";

    public static final String MISSING_FIELDS_LOGIN = "2001";
    public static final String PASSWORD_INCORRECT = "2002";
    public static final String INVALID_USERNAME = "2003";
    public static final String LOGIN_FAILED = "2222";

    public static final String MISSING_FIELDS_FORGOT_PW = "3001";
    public static final String NEW_PASSWORD_INVALID = "3002";
    public static final String OLD_PASSWORD_MISMATCH = "3003";
    public static final String USERNAME_INVALID = "3004";
    public static final String CHANGE_PW_FAILED = "3333";

    //titles
    public static final String TITLE_SUCCESS = "SUCCESS";
    public static final String TITLE_FAILED = "FAILED";

}
