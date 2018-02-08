package com.example.code.forge;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SchedulesFragment extends Fragment {


    public SchedulesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View schedulesView =  inflater.inflate(R.layout.fragment_schedules, container, false);

        MaterialCalendarView materialCalendarView = schedulesView.findViewById(R.id.materialCalendarView);


        return schedulesView;
    }

}
