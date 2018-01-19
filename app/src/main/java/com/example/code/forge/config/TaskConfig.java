package com.example.code.forge.config;

/**
 * Created by User on 01/10.
 */

public final class TaskConfig {
    private static final boolean PRODUCTION = false;
    public static final String BASE_URL = PRODUCTION ? "" : "http://172.16.46.56/WebProject/public/ci/";
    public static final String LOGIN_URL = BASE_URL + "login";
}
