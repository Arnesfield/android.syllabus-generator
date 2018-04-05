package com.ragew.code.forge_v2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.ragew.code.forge_v2.Config.TaskConfig;
import com.ragew.code.forge_v2.Utils.SuperTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class AssignsFragment extends Fragment {

    private ArrayList<Assigns> m_arrayList;
    private ListView assignsListView;

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

    public AssignsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View assignsFragmentView = inflater.inflate(R.layout.fragment_assigns, container, false);

        //Listview
        assignsListView = assignsFragmentView.findViewById(R.id.assignsListView);

        //Set an adapter for the list view
        CustomAdapter customAdapter = new CustomAdapter();
        assignsListView.setAdapter(customAdapter);

        SuperTask.execute(getContext(), "assign", TaskConfig.ASSIGN_URL);

        return assignsFragmentView;
    }

    //Create method for setting each assign values
    public void setAssignedBy(JsonObject jsonObject) {

        for (int i = 0; i < jsonObject.size(); i++) {
//            Set<String> strings = jsonObject.keySet();
//            Iterator<String> iterator = strings.iterator();
//            while (iterator.hasNext()) {
//                String key = iterator.next();
//                Log.d("thekey", key);
//            }

            String fname = jsonObject.get("fname").getAsString();
            String lname = jsonObject.get("lname").getAsString();
            String mname = jsonObject.get("mname").getAsString();
            String username = jsonObject.get("username").getAsString();
            String title = jsonObject.get("title").getAsString();
            String weight = jsonObject.get("weight").getAsString();
            String img_src = jsonObject.get("img_src").getAsString();
        }

        Toast.makeText(getContext(), assignedFname + " " + assignedLname, Toast.LENGTH_LONG).show();

    }

    public void setCreatedBy(JsonObject jsonObject){

        for (int i = 0; i < jsonObject.size(); i++){
            assignedToFname = jsonObject.get("fname").getAsString();
            assignedToLname = jsonObject.get("lname").getAsString();
        }
    }

    public void getCourseDetails(JsonObject jsonObject){

        for (int i = 0; i < jsonObject.size(); i++){
            courseTitleText = jsonObject.get("title").getAsString();
            courseCodeText = jsonObject.get("code").getAsString();
        }
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.assignment_info_listview,null);
            TextView courseCode = convertView.findViewById(R.id.courseCode);
            TextView courseTitle = convertView.findViewById(R.id.courseTitle);
            TextView assignedBy = convertView.findViewById(R.id.assignedByName);
            TextView assignedTo = convertView.findViewById(R.id.assignedToName);
  //          TextView firstLetter = convertView.findViewById(R.id.firstLetter);

//            first = assignedFname.charAt(0);

            courseCode.setText(courseCodeText);
            courseTitle.setText(courseTitleText);
            assignedBy.setText(assignedFname + assignedLname);
            assignedTo.setText(assignedToFname + assignedToLname);
  //          firstLetter.setText(String.valueOf(first));

            return convertView;
        }
    }
}
