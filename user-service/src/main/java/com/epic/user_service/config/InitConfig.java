package com.epic.user_service.config;

public class InitConfig {

    //response codes
    public static final String SUCCESS = "0000";
    public static final String MISSING_FIELDS = "1001";
    public static final String USERNAME_TAKEN = "1002";
    public static final String EMAIL_TAKEN = "1003";
    public static final String CONTACT_NO_TAKEN = "1004";
    public static final String PASSWORD_INVALID = "1005";
    public static final String REGISTRATION_FAILED = "1006";
    public static final String USERNAME_NOT_FOUND = "1007";
    public static final String PASSWORD_INCORRECT = "1008";
    public static final String ROLE_INVALID = "1009";
    public static final String LOGIN_FAILED = "1010";
    public static final String MISSING_FIELDS_FORGOT_PW = "1011";
    public static final String NEW_PASSWORD_INVALID = "1012";
    public static final String OLD_PASSWORD_MISMATCH = "1013";
    public static final String USERNAME_INVALID = "1014";
    public static final String CHANGE_PW_FAILED = "1015";
    public static final String TOKEN_MISSING = "1016";
    public static final String TOKEN_INVALID_EXPIRED = "1017";
    public static final String REQUEST_FAILED = "1018";
    public static final String UNSUCCESSFUL_RESPONSE = "1019";
    public static final String RETAILER_NOT_FOUND = "1020";
    public static final String UNIDENTIFIED_OPTION = "1021";
    public static final String LIST_EMPTY = "1022";
    public static final String DISTRIBUTOR_REQUEST_NOT_FOUND = "1023";
    public static final String ORDER_REQUEST_NOT_FOUND = "1024";
    public static final String PRODUCT_ID_NOT_FOUND = "1025";
    public static final String REGISTRATION_REQ_PENDING = "change";
    public static final String REGISTRATION_REQ_FAILED = "change";

    //titles
    public static final String TITLE_SUCCESS = "SUCCESS";
    public static final String TITLE_FAILED = "FAILED";

    //user types
    public static final String ADMIN = "ADMIN";
    public static final String DISTRIBUTOR = "DISTRIBUTOR";
    public static final String RETAILER = "RETAILER";

    //status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";

}
