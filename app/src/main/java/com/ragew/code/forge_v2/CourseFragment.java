package com.ragew.code.forge_v2;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class CourseFragment extends Fragment{
    private ArrayList<Course> m_arrayList;
    private ListView courseListView;
    private String id;
    private String title;
    private String code;
    private String description;
    private String objectives;
    private String unitsLec;
    private String unitsLab;
    private SwipeRefreshLayout m_swipeRefreshLayout;

    public CourseFragment() {
        // Required empty public constructor
        this.m_arrayList = new ArrayList<>();
    }

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

        //Refresh
        m_swipeRefreshLayout = courseView.findViewById(R.id.swiperefresh);

        m_swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Toast.makeText(getContext(),"Refresh success",Toast.LENGTH_LONG).show();
                m_swipeRefreshLayout.setRefreshing(true);

                //Start the refresh background task
                //This method calls setRefreshing(false) when it's finished.
                doRefreshList();

                //Call finished
                m_swipeRefreshLayout.setRefreshing(false);
            }
        });
        //Return the view
        return courseView;

    }

    //Create a method for setting each course
    public void setCourses (JSONArray jsonArray){
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
