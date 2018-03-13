package com.example.user.attendr.constants;

/**
 * Created by Eamon on 11/02/2018.
 *
 * Api URLs for the server
 */

public class ApiUrls {

    private static final String BASE_URL = "http://46.101.13.145:8000/api/";
    public static final String LOGIN = BASE_URL + "login/";
    public static final String REGISTER = BASE_URL + "register/";
    public static final String EVENTS = BASE_URL + "events/";
    public static final String EVENTS_FOR_USER = BASE_URL + "{username}/events/";
    public static final String VERIFY_GROUP = BASE_URL + "verify_group/";
    public static final String MANUAL_SIGN_IN = BASE_URL + "manual_sign_in/";
    public static final String REMOVE_USER_FROM_ATTENDING = BASE_URL + "remove_user_from_attending/";
    public static final String OBTAIN_TOKEN = BASE_URL + "api-token-auth/";


}
