package com.code.feutech.forge.items;

import org.json.JSONException;
import org.json.JSONObject;

public class Assigned {
    private int id;
    private int status;
    private User user;

    public Assigned(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.status = json.getInt("status");
        JSONObject assignedUser = json.getJSONObject("user");
        this.user = new User(assignedUser);
    }
}
