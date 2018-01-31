package com.example.code.forge;

/**
 * Created by ragew on 1/31/2018.
 */

public class Course{
    private String id;
    private String title;
    private String code;
    private String description;
    private String objectives;
    private String unitsLec;
    private String unitsLab;

    public Course(String id, String title, String code, String description, String objectives, String unitsLec, String unitsLab){
        this.id = id;
        this.title = title;
        this.code = code;
        this.description = description;
        this.objectives = objectives;
        this.unitsLec = unitsLec;
        this.unitsLab = unitsLab;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getUnitsLec() {
        return unitsLec;
    }

    public void setUnitsLec(String unitsLec) {
        this.unitsLec = unitsLec;
    }

    public String getUnitsLab() {
        return unitsLab;
    }

    public void setUnitsLab(String unitsLab) {
        this.unitsLab = unitsLab;
    }


}
