package com.example.code.forge;


import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.code.forge.utils.DialogCreator;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SchedulesFragment extends Fragment implements DialogCreator.DialogActionListener {


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    CompactCalendarView compactCalendarView;
    TextView monthDisplay;

    public SchedulesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View schedulesView =  inflater.inflate(R.layout.fragment_schedules, container, false);

        monthDisplay = schedulesView.findViewById(R.id.month_display);

        //Do shit here
        compactCalendarView = schedulesView.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        //set event
        Event testEvent = new Event(Color.RED,1518148800L, "This is a test event");
        compactCalendarView.addEvent(testEvent);
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                if (dateClicked.toString().compareTo("Fri Feb 09 04:00:00 AST 2018") == 0){
                    Toast.makeText(getActivity(), "This is a test event", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(),"No events", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthDisplay.setText(simpleDateFormat.format(firstDayOfNewMonth));
            }
        });

        return schedulesView;
    }

    // implement methods for DialogCreator
    @Override
    public void onClickPositiveButton(String actionId) {
        switch (actionId) {
            case "someDialog":
                Log.d("test", "someDialog positive");
                break;
        }
    }

    @Override
    public void onClickNegativeButton(String actionId) {
        switch (actionId) {
            case "someDialog":
                Log.d("test", "someDialog negative");
                break;
        }
    }

    @Override
    public void onClickNeutralButton(String actionId) {
        switch (actionId) {
            case "someDialog":
                Log.d("test", "someDialog neutral");
                break;
        }
    }

    @Override
    public void onClickMultiChoiceItem(String actionId, int which, boolean isChecked) {

    }

    @Override
    public void onCreateDialogView(String actionId, View view) {

    }

}
