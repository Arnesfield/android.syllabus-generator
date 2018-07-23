package com.code.feutech.forge.items;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tags {
    private String[] tags;

    public Tags(JSONObject json, String field) {
        // check first if field is json array or string
        try {
            JSONArray jsonArray = json.getJSONArray(field);
            // using the array, loop and set the strings
            this.tags = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                // note: consider non strings lol
                this.tags[i] = jsonArray.getString(i);
            }
        } catch (JSONException e) {
            // if no array, then tags should be empty
            this.tags = new String[]{};
        }
    }

    public String[] getTags() {
        return tags;
    }

    public boolean hasTag(String tag) {
        for (final String s : this.tags) {
            if (s.toLowerCase().equals(tag.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
