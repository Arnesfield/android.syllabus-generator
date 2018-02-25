package com.example.code.forge;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ragew on 1/31/2018.
 */

public class Course{


    public static class CourseAdapter extends ArrayAdapter<Course> {
        public CourseAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Course> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.course_list_item, null);
            }

            final Course course = getItem(position);

            final TextView tv_courseTitle = view.findViewById(R.id.courseCode);
            final TextView tv_courseCode = view.findViewById(R.id.courseTitle);

            tv_courseTitle.setText(course.getTitle());
            tv_courseCode.setText(course.getCode());
            // return super.getView(position, convertView, parent);
            return view;
        }
    }


    private String id;
    private String title;
    private String code;
    private String description;
    private String objectives;
    private String unitsLec;
    private String unitsLab;

    public Course(){

    }
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
