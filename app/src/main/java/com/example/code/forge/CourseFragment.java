package com.example.code.forge;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.code.forge.config.TaskConfig;
import com.example.code.forge.utils.SuperTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseFragment extends Fragment {
    private JSONArray courses;

    public CourseFragment() { //Constructor
        // Lahat ng declaration nandito
        // Required empty public constructor
        this.m_arrayList = new ArrayList();
        ListView courseLV;
    }

    private ArrayList<Course> m_arrayList;
    private ListView courseListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View courseView = inflater.inflate(R.layout.fragment_course, container, false);
        courseListView = courseView.findViewById(R.id.courseListView);

        courseListView.setAdapter(new Course.CourseAdapter(getActivity(),android.R.layout.simple_list_item_1, m_arrayList));
        SuperTask.execute(getContext(),"courses", TaskConfig.COURSE_URL);
        return courseView;
    }

    public void setCourses (JSONArray jsonArray){
        this.courses = jsonArray;
        Toast.makeText(getContext(),"SUCCESS",Toast.LENGTH_LONG).show();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String title = jsonObject.getString("title");
                String code = jsonObject.getString("code");
                String description = jsonObject.getString("description");
                String objectives = jsonObject.getString("objectives");
                String unitsLec = jsonObject.getString("unitsLec");
                String unitsLab = jsonObject.getString("unitsLab");

                // send data to constructor
                m_arrayList.add(new Course(id, title, code, description, objectives, unitsLec, unitsLab));
            }
            for (Course course: m_arrayList) {
                // Toast.makeText(getContext(), course.getCode().toString(), Toast.LENGTH_LONG).show();
            }
            doRefreshList();
        }catch (Exception e){
            Log.d("Error", String.valueOf(e));
        }
    }

    public void doRefreshList() {
        ((Course.CourseAdapter)this.courseListView.getAdapter()).notifyDataSetChanged();
    }

}
