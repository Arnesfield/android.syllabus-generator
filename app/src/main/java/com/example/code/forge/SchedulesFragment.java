package com.example.code.forge;


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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SchedulesFragment extends Fragment implements DialogCreator.DialogActionListener {


    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());
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
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        //set event Friday, February 9, 2018 1:15:37 PM GMT +08:00

        final Event testEvent = new Event(Color.RED,1518153337000L, "This is a test event");
        compactCalendarView.addEvent(testEvent);
        final Event testEvent2 = new Event(Color.BLUE, 1518153337000L, "Test event 2");
        compactCalendarView.addEvent(testEvent2);

        final Calendar calendar = Calendar.getInstance();

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                for (Event x:events){
                    // x.getData().toString();
                    Toast.makeText(getActivity(),"Day was clicked: " + dateClicked + " with events " + events,Toast.LENGTH_LONG).show();
                    //get data wow magic
                    /*calendar.setTimeInMillis(x.getTimeInMillis());
                    int mYear = calendar.get(Calendar.YEAR);
                    int mMonth = calendar.get(Calendar.MONTH);
                    int mDay = calendar.get(Calendar.DAY_OF_MONTH);*/
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
        /*ListView listView = view.findViewById(R.id.eventView);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, eventList);
        listView.setAdapter(arrayAdapter);*/
    }

}
