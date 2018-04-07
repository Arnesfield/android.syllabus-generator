package com.code.feutech.forge.items;

import org.json.JSONException;
import org.json.JSONObject;

public class Level {
    private int id;
    private int status;
    private User user;

    public Level(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.status = json.getInt("status");
        JSONObject jsonUser = json.getJSONObject("user");
        this.user = new User(jsonUser);
    }
}
