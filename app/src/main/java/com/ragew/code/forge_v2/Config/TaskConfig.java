package com.ragew.code.forge_v2.Config;

/**
 * Created by ragew on 3/30/2018.
 */

public final class TaskConfig {
    //"http://192.168.1.3/xforge/public/ci/"
    //192.168.43.192
    //172.16.46.56
    //192.168.1.18
    //192.168.137.1
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String COURSE_URL = BASE_URL + "courses";
    public static final String ASSIGN_URL = BASE_URL + "assigns/my";
    private static final boolean PRODUCTION = true;
    public static final String BASE_URL = PRODUCTION ? "http://192.168.1.3/xforge/public/api/" : "";
}
