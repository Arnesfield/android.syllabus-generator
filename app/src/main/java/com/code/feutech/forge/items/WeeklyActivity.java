package com.code.feutech.forge.items;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeeklyActivity {
    private boolean asObject;
    private String[] assessmentTasks;
    private String[] ilos;
    private String[] instructionalMaterials;
    private String[] tlaFaculty;
    private String[] tlaStudent;
    private String[] topics;
    private int[] cloMap;
    private Double noOfHours;
    private int noOfWeeks;
    private String text;

    public WeeklyActivity(JSONObject json) throws JSONException {
        this.asObject = json.getBoolean("asObject");
        this.assessmentTasks = setStrArray(json, "assessmentTasks");
        this.ilos = setStrArray(json, "ilo");
        this.instructionalMaterials = setStrArray(json, "instructionalMaterials");
        this.tlaFaculty = setStrArray(json, "tlaFaculty");
        this.tlaStudent = setStrArray(json, "tlaStudent");
        this.topics = setStrArray(json, "topics");
        this.noOfHours = json.getDouble("noOfHours");
        this.noOfWeeks = json.getInt("noOfWeeks");
        this.text = !asObject ? json.getString("text") : null;

        // cloMap
        final JSONArray jsonCloMap = json.getJSONArray("cloMap");
        this.cloMap = new int[jsonCloMap.length()];

        for (int i = 0; i < jsonCloMap.length(); i++) {
            this.cloMap[i] = jsonCloMap.getInt(i);
        }
    }

    private String[] setStrArray(JSONObject json, String str) throws JSONException {
        final JSONArray jsonArray = json.getJSONArray(str);
        final String[] res = new String[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            res[i] = jsonArray.getString(i);
        }
        return res;
    }

    public boolean isAsObject() {
        return asObject;
    }

    public String[] getAssessmentTasks() {
        return assessmentTasks;
    }

    public String[] getIlos() {
        return ilos;
    }

    public String[] getInstructionalMaterials() {
        return instructionalMaterials;
    }

    public String[] getTlaFaculty() {
        return tlaFaculty;
    }

    public String[] getTlaStudent() {
        return tlaStudent;
    }

    public String[] getTopics() {
        return topics;
    }

    public Double getNoOfHours() {
        return noOfHours;
    }

    public int getNoOfWeeks() {
        return noOfWeeks;
    }

    public String getText() {
        return text;
    }

    public int[] getCloMap() {
        return cloMap;
    }
}
