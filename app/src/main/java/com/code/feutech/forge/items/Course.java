package com.code.feutech.forge.items;

import org.json.JSONException;
import org.json.JSONObject;

public class Course {
    private int id;
    private int status;
    private UnixWrapper createdAt;
    private UnixWrapper updatedAt;
    private int unitsLec;
    private int unitsLab;
    private String code;
    private String title;
    private String description;
    private String objectives;

    public Course(JSONObject json) throws JSONException {
        this(json, false, false);
    }

    public Course(JSONObject json, boolean parseRelated) throws JSONException {
        this(json, parseRelated, false);
    }

    public Course(JSONObject json, boolean parseRelated, boolean deepParse) throws JSONException {
        this.id = json.getInt("id");
        this.status = json.getInt("status");
        this.createdAt = new UnixWrapper(json.getLong("created_at"));
        this.updatedAt = new UnixWrapper(json.getLong("updated_at"));
        this.unitsLec = json.getInt("unitsLec");
        this.unitsLab = json.getInt("unitsLab");
        this.code = json.getString("code");
        this.title = json.getString("title");
        this.description = json.getString("description");
        this.objectives = json.getString("objectives");

        // remember to parse content
        if (parseRelated) {

        }
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public UnixWrapper getCreatedAt() {
        return createdAt;
    }

    public UnixWrapper getUpdatedAt() {
        return updatedAt;
    }

    public int getUnitsLec() {
        return unitsLec;
    }

    public int getUnitsLab() {
        return unitsLab;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getObjectives() {
        return objectives;
    }
}
