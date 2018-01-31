package com.example.code.forge;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeachingPlanFragment extends Fragment {


    public TeachingPlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View teachingPlanView = inflater.inflate(R.layout.fragment_teaching_plan, container, false);
        return teachingPlanView;
    }

}
