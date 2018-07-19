package com.code.feutech.forge.items;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int id;
    private String fname;
    private String mname;
    private String lname;
    private String username;
    private String title;
    private String weight;
    private String imgSrc;

    public User(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.fname = json.getString("fname");
        this.mname = json.getString("mname");
        this.lname = json.getString("lname");
        this.username = json.getString("username");
        this.title = json.getString("title");
        this.weight = json.getString("weight");
        this.imgSrc = json.getString("img_src");
    }

    public int getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getMname() {
        return mname;
    }

    public String getLname() {
        return lname;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getWeight() {
        return weight;
    }

    public String getImgSrc() {
        return imgSrc;
    }
}
