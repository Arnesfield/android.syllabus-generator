package com.code.feutech.forge.config;

public final class TaskConfig {
    public static final String BASE_URL = AppConfig.PRODUCTION
        ? "http://codegenerator.x10host.com/"
        : "http://192.168.1.10/xforge/public/";
    public static final String API_URL = BASE_URL + "api/";
    public static final String LOGIN_URL = API_URL + "login";
    public static final String USERS_URL = API_URL + "users";
    public static final String COURSES_URL = API_URL + "courses";
    public static final String ASSIGNS_MY_URL = API_URL + "assigns/my";
    public static final String REVIEWS_URL = API_URL + "assigns/my_reviews";
    public static final String UPLOADED_IMAGES_URL = BASE_URL + "uploads/images/";
}
