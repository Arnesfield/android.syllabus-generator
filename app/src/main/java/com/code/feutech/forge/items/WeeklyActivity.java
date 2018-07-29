package com.code.feutech.forge.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.code.feutech.forge.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WeeklyActivity {
    private boolean asObject;
    private String[] assessmentTasks;
    private String[] ilos;
    private String[] instructionalMaterials;
    private String[] tlaFaculty;
    private String[] tlaStudent;
    private String[] topics;
    private int[] cloMap;
    private double noOfHours;
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

    public double getNoOfHours() {
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

    public static int getTotalWeeksBefore(int index, WeeklyActivitiesArrayAdapter adapter) {
        return getTotalWeeksBefore(index, getActivitiesFromAdapter(adapter));
    }

    public static int getTotalWeeksBefore(int index, WeeklyActivity[] activities) {
        // starting from 1, add all weeks
        int weeks = 0;
        for (int i = 0; i < index; i++) {
            weeks += activities[i].getNoOfWeeks();
        }
        return weeks;
    }

    public static WeeklyActivity[] getActivitiesFromAdapter(WeeklyActivitiesArrayAdapter adapter) {
        final WeeklyActivity[] activities = new WeeklyActivity[adapter.getCount()];
        for (int i = 0; i < adapter.getCount(); i++) {
            activities[i] = adapter.getItem(i);
        }
        return activities;
    }

    public static String createWeekNo(int totalWeeksBefore, int weeks) {
        int nFirst = totalWeeksBefore + 1;
        int nLast = totalWeeksBefore + weeks;

        // if equal, then only return first
        return nFirst == nLast ? String.valueOf(nFirst) : nFirst + "-" + nLast;
    }

    public static String[] createWeekNoArray(WeeklyActivity[] activities, String prepend) {
        final String[] res = new String[activities.length];
        for (int i = 0; i < activities.length; i++) {
            final WeeklyActivity activity = activities[i];
            final int totalWeeksBefore = WeeklyActivity.getTotalWeeksBefore(i, activities);
            final String weekText = WeeklyActivity.createWeekNo(totalWeeksBefore, activity.getNoOfWeeks());
            res[i] = prepend + weekText;
        }
        return res;
    }

    // adapter
    public static class WeeklyActivitiesArrayAdapter extends ArrayAdapter<WeeklyActivity> {
        public WeeklyActivitiesArrayAdapter(@NonNull Context context, int resource, @NonNull List<WeeklyActivity> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = convertView;

            if (view == null) {
                view = inflater.inflate(R.layout.item_weekly_activity_view, null);
            }

            final WeeklyActivity activity = getItem(position);

            // set components
            final TextView tvTitle = view.findViewById(R.id.item_weekly_activity_title);
            final TextView tvSubtitle = view.findViewById(R.id.item_weekly_activity_subtitle);

            final int totalWeeksBefore = WeeklyActivity.getTotalWeeksBefore(position, this);
            final String weekText = WeeklyActivity.createWeekNo(totalWeeksBefore, activity.getNoOfWeeks());

            // set values
            tvTitle.setText("Week " + weekText);
            tvSubtitle.setText(Syllabus.getFormattedDouble(activity.getNoOfHours()) + " hours");

            return view;
        }
    }
}
