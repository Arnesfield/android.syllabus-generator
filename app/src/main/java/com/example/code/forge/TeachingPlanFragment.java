package com.example.code.forge;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeachingPlanFragment extends Fragment {

    private ListView teachingPlanListView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> teachingPlanList;
    private String[] semesters = {"Week 1", "Week 2", "Week 3", "Week 4", "Week 5", "Week 6", "Week 7", "Week 8"};
    Intent intent;

    public TeachingPlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View teachingPlanView = inflater.inflate(R.layout.fragment_teaching_plan, container, false);
        teachingPlanListView = teachingPlanView.findViewById(R.id.teachingPlanListView);
        teachingPlanList = new ArrayList<>();
        for (int i = 0; i < semesters.length; i++){
            teachingPlanList.add(semesters[i]);
        }
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, teachingPlanList);
        teachingPlanListView.setAdapter(arrayAdapter);

        teachingPlanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity().getApplicationContext(), String.valueOf(position), Toast.LENGTH_LONG).show();
                intent = new Intent(getActivity().getApplication(),Activities.class);
                intent.putExtra("id",String.valueOf(position));
                startActivity(intent);
            }
        });

        return teachingPlanView;
    }

}
