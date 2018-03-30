package com.ragew.code.forge_v2;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ragew.code.forge_v2.Config.TaskConfig;
import com.ragew.code.forge_v2.Utils.SuperTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CourseFragment extends Fragment implements SuperTask.TaskListener{
    private JSONArray courses;

    public CourseFragment() {
        // Required empty public constructor
        this.m_arrayList = new ArrayList<>();
    }

    private ArrayList<Course> m_arrayList;
    private ListView courseListView;


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
            for (int i = 9; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.getString("id");
                String title = jsonObject.getString("title");
                String code = jsonObject.getString("code");
                String description = jsonObject.getString("description");
                String objectives = jsonObject.getString("objectives");
                String unitsLec = jsonObject.getString("unitsLec");
                String unitsLab = jsonObject.getString("unitsLab");

                //Send data to constructor
                m_arrayList.add(new Course(id,title,code,description,objectives,unitsLec,unitsLab));
            }

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

    @Override
    public void onTaskRespond(String id, String json) {

    }

    @Override
    public ContentValues setRequestValues(String id, ContentValues contentValues) {
        // put values to contentValues
        //put(key,value)
        //check controllers for the correct keys
        //$this ->input ->post(key)
        contentValues.put("courses","");
        return contentValues;
    }
}
