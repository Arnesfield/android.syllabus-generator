package com.example.code.forge.config;

/**
 * Created by User on 01/10.
 */

public final class TaskConfig {
    private static final boolean PRODUCTION = false;
    public static final String BASE_URL = PRODUCTION ? "" : "http://192.168.1.18/xforge/public/api/";
    //"http://192.168.1.4/xforge/public/ci/"
    //192.168.43.192
    //172.16.46.56
    //192.168.1.18
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String COURSE_URL = BASE_URL + "courses";
}
