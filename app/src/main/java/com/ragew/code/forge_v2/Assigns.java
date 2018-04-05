package com.ragew.code.forge_v2;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ragew on 4/5/2018.
 */

public class Assigns {
    public static class AssignsAdapter extends ArrayAdapter<Assigns> {
        public AssignsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Assigns> objects){
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View view = convertView;
            if (view == null){
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //inflate view here
                view = inflater.inflate(R.layout.assignment_info_listview,null);
            }

            final Assigns assigns = getItem(position);

            final TextView courseCode = view.findViewById(R.id.courseCode);
            final TextView courseTitle = view.findViewById(R.id.courseTitle);
            final TextView assignedBy = view.findViewById(R.id.assignedByName);
            final TextView assignedTo = view.findViewById(R.id.assignedToName);
            final TextView firstLetter = view.findViewById(R.id.firstLetter);

            courseCode.setText(assigns.getCourseCodeText());
            courseTitle.setText(assigns.getCourseTitleText());
            assignedBy.setText(assigns.getAssignedFname() + " " + assigns.getAssignedLname());
            assignedTo.setText(assigns.getAssignedToFname() + " " + assigns.getAssignedToLname());
            firstLetter.setText(String.valueOf(assigns.getFirst()));

            return view;
        }
    }

    //Assigned by
    private String assignedFname;
    private String assignedLname;

    //Created by
    private String assignedToFname;
    private String assignedToLname;

    //Course details
    private String courseTitleText;
    private String courseCodeText;

    private char first;

    public Assigns(){

    }

    public Assigns(String assignedFname, String assignedLname){
        this.assignedFname = assignedFname;
        this.assignedLname = assignedLname;
    }

    public Assigns(String assignedToFname, String assignedToLname, String dummy){
        this.assignedToFname = assignedToFname;
        this.assignedToLname = assignedToLname;
    }

    public Assigns(String courseTitleText, String courseCodeText, int x){
        this.courseTitleText = courseTitleText;
        this.courseCodeText = courseCodeText;
    }

    public Assigns(String assignedFname, String assignedLname, String assignedToFname, String assignedToLname, String courseTitleText, String courseCodeText, char first){
        this.assignedFname = assignedFname;
        this.assignedLname = assignedLname;
        this.assignedToFname = assignedToFname;
        this.assignedToLname = assignedToLname;
        this.courseTitleText = courseTitleText;
        this.courseCodeText = courseCodeText;
        this.first = first;
    }

    public String getAssignedFname() {
        return assignedFname;
    }

    public void setAssignedFname(String assignedFname) {
        this.assignedFname = assignedFname;
    }

    public String getAssignedLname() {
        return assignedLname;
    }

    public void setAssignedLname(String assignedLname) {
        this.assignedLname = assignedLname;
    }

    public String getAssignedToFname() {
        return assignedToFname;
    }

    public void setAssignedToFname(String assignedToFname) {
        this.assignedToFname = assignedToFname;
    }

    public String getAssignedToLname() {
        return assignedToLname;
    }

    public void setAssignedToLname(String assignedToLname) {
        this.assignedToLname = assignedToLname;
    }

    public String getCourseTitleText() {
        return courseTitleText;
    }

    public void setCourseTitleText(String courseTitleText) {
        this.courseTitleText = courseTitleText;
    }

    public String getCourseCodeText() {
        return courseCodeText;
    }

    public void setCourseCodeText(String courseCodeText) {
        this.courseCodeText = courseCodeText;
    }

    public char getFirst() {
        return first;
    }

    public void setFirst(char first) {
        this.first = first;
    }
}
