package com.example.code.forge;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ragew on 2/25/2018.
 */

public class Syllabi {

    public static class SyllabiAdapter extends ArrayAdapter<Syllabi>{
        public SyllabiAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Syllabi> objects) {
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

            final Syllabi syllabi = getItem(position);

            /*final TextView tv_syllabiName = view.findViewById(R.id.syllabus_Name);
            final TextView tv_syllabiDescription = view.findViewById(R.id.syllabus_Description);*/

            /*tv_syllabiName.setText(syllabi.getName());
            tv_syllabiDescription.setText(syllabi.getDescription());
            */// return super.getView(position, convertView, parent);
            return view;
        }
    }

    private String name;
    private String description;

    public Syllabi(String name, String description){
        this.name = name;
        this.description = description;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
