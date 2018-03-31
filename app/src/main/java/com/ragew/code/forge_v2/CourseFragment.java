package com.ragew.code.forge_v2;


import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ragew.code.forge_v2.Config.TaskConfig;
import com.ragew.code.forge_v2.Utils.SuperTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseFragment extends Fragment{
    private JSONArray courses;

    public CourseFragment() {
        // Required empty public constructor
        this.m_arrayList = new ArrayList<>();
    }

    private ArrayList<Course> m_arrayList;
    private ListView courseListView;

    private String id;
    private String title;
    private String code;


    private String description;
    private String objectives;
    private String unitsLec;
    private String unitsLab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View courseView = inflater.inflate(R.layout.fragment_course, container, false);
        //Get the view of the list view
        courseListView = courseView.findViewById(R.id.courseListView);

        //Set an adapter for the list view using the arraylist (for population)
        courseListView.setAdapter(new Course.CourseAdapter(getActivity(), android.R.layout.simple_list_item_1, m_arrayList));
        SuperTask.execute(getContext(),"courses", TaskConfig.COURSE_URL);
        //Return the view
        return courseView;

    }

    //Create a method for setting each course
    public void setCourses (JSONArray jsonArray){
        this.courses = jsonArray;
        try {
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                id = jsonObject.getString("id");
                title = jsonObject.getString("title");
                code = jsonObject.getString("code");

                description = jsonObject.getString("description");
                objectives = jsonObject.getString("objectives");
                unitsLec = jsonObject.getString("unitsLec");
                unitsLab = jsonObject.getString("unitsLab");

                //Send data to constructor
                m_arrayList.add(new Course(id,title,code,description,objectives,unitsLec,unitsLab));
            }

            courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Get the data of the currently selected list item
                    Course course = m_arrayList.get(position);
                    Intent intent = new Intent(getContext(), CourseInfo.class);
                    intent.putExtra("description",course.getDescription());
                    intent.putExtra("objectives",course.getObjectives());
                    intent.putExtra("unitsLec",course.getUnitsLec());
                    intent.putExtra("unitsLab",course.getUnitsLab());
                    startActivity(intent);

                }
            });

            //Refresh the list view
            doRefreshList();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Create a method to refresh the list
    public void doRefreshList(){
        ((Course.CourseAdapter)this.courseListView.getAdapter()).notifyDataSetChanged();
    }
}
