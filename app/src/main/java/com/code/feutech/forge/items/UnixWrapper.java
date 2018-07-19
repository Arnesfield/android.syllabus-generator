package com.code.feutech.forge.items;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UnixWrapper {
    private long time;

    public UnixWrapper(long time) {
        this.time = time;
    }

    public String convert(String format) {
        Date date = new Date(this.time * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
