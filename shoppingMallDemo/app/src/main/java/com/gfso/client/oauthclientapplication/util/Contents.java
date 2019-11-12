package com.gfso.client.oauthclientapplication.util;

public class Contents {
    public static int LOGIN_REQUEST=1;
    public static int CHOOSE_CITY_REQUEST=2;
    public static final String CHOOSE_CITY_VALUE="CHOOSE_CITY_VALUE";

    public static int EDIT_ADDRESS_REQUEST=2;
    public static final String ADDRESS_ID="ADDRESS_ID";
    public static final String ADDRESS_NAME="ADDRESS_NAME";
    public static final String ADDRESS_PHONE="ADDRESS_PHONE";
    public static final String ADDRESS_ZIPCODE="ADDRESS_ZIPCODE";
    public static final String ADDRESS_ADDRESS="ADDRESS_ADDRESS";
    public static final String ADDRESS_DEFAULT="ADDRESS_DEFAULT";
    public static final String ADDRESS_OPERATION="ADDRESS_OPERATION";
    public static final String ADDRESS_OPERATION_ADD="ADDRESS_OPERATION_ADD";
    public static final String ADDRESS_OPERATION_EDIT="ADDRESS_OPERATION_EDIT";

    public static String LOGIN_USERID="loginUserId";
    public static final String USER_JSON = "user_json" ;
    public static final String TOKEN = "token" ;
    public static final String LOGIN_URL = "http://10.59.167.130:8081/mobile/login";
    public static final String SMS_LOGIN_URL = "http://10.59.167.130:8081/mobile/smslogin";

    public static final int PAY_SUCCESS=1;
    public static final int PAY_FAIL=2;
    public static final int PAY_PENDING=3;
    public static final int PAY_ALL=4;
}
