package com.code.feutech.forge.config;

public final class TaskConfig {
    public static final String BASE_URL = AppConfig.PRODUCTION
        ? "http://codegenerator.x10host.com/api/"
        : "http://192.168.1.10/xforge/public/api/";
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String COURSES_URL = BASE_URL + "courses";
    public static final String ASSIGNS_MY_URL = BASE_URL + "assigns/my";
    public static final String REVIEWS_URL = BASE_URL + "assigns/my_reviews";
}
