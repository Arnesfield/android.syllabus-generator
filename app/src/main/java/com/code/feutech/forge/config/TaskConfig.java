package com.code.feutech.forge.config;

public final class TaskConfig {
    private static final boolean PRODUCTION = true;
    public static final String BASE_URL = PRODUCTION
        ? "http://192.168.1.13/xforge/public/api/"
        : "http://codegenerator.x10host.com/api/";
    //"http://192.168.1.3/xforge/public/ci/"
    //192.168.43.192
    //172.16.46.56
    //192.168.1.18
    //192.168.137.1
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String COURSES_URL = BASE_URL + "courses";
    public static final String ASSIGNS_MY_URL = BASE_URL + "assigns/my";
}
