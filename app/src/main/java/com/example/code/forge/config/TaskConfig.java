package com.example.code.forge.config;

/**
 * Created by User on 01/10.
 */

public final class TaskConfig {
    private static final boolean PRODUCTION = false;
    public static final String BASE_URL = PRODUCTION ? "" : "http://localhost/proj/thesis/";
    public static final String LOGIN_URL = BASE_URL + "";
}
