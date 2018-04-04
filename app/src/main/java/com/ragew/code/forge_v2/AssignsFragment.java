package com.ragew.code.forge_v2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.ragew.code.forge_v2.Config.TaskConfig;
import com.ragew.code.forge_v2.Utils.SuperTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class AssignsFragment extends Fragment {

    private ListView assignsListView;

    private String fname;
    private String lname;

    public AssignsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View assignsFragmentView = inflater.inflate(R.layout.fragment_assigns, container, false);

        assignsListView = assignsFragmentView.findViewById(R.id.assignsListView);
        SuperTask.execute(getContext(), "assign", TaskConfig.ASSIGN_URL);


        return assignsFragmentView;
    }

    //Create method for setting each assign values
    public void setAssignment(JsonObject jsonObject) {

        for (int i = 0; i < jsonObject.size(); i++) {
            fname = jsonObject.get("fname").getAsString();
            lname = jsonObject.get("lname").getAsString();
        }
        Toast.makeText(getContext(), fname + " " + lname, Toast.LENGTH_LONG).show();

    }
}
