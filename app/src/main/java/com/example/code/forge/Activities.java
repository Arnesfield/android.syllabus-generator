package com.example.code.forge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Activities extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        Bundle preValues = getIntent().getExtras();
        String testString = preValues.getString("id");
        Integer m_weekNumber = Integer.parseInt(testString.toString());

        ListView activities_ListView = findViewById(R.id.activitiesListView);
        ArrayAdapter<String> arrayAdapter;

        //Use this shit
        String displayString = String.valueOf(m_weekNumber + 1);

        List<String> week1_Activities = new ArrayList<>();
        List<String> week2_Activities = new ArrayList<>();
        List<String> week3_Activities = new ArrayList<>();
        List<String> week4_Activities = new ArrayList<>();
        List<String> week5_Activities = new ArrayList<>();
        List<String> week6_Activities = new ArrayList<>();
        List<String> week7_Activities = new ArrayList<>();
        List<String> week8_Activities = new ArrayList<>();

        week1_Activities.add("Week 1 activity 1");
        week1_Activities.add("Week 1 activity 2");
        week1_Activities.add("Week 1 activity 3");
        week1_Activities.add("Week 1 activity 4");
        week1_Activities.add("Week 1 activity 5");

        week2_Activities.add("Week 2 activity 1");
        week2_Activities.add("Week 2 activity 2");
        week2_Activities.add("Week 2 activity 3");
        week2_Activities.add("Week 2 activity 4");
        week2_Activities.add("Week 2 activity 5");

        week3_Activities.add("Week 3 activity 1");
        week3_Activities.add("Week 3 activity 2");
        week3_Activities.add("Week 3 activity 3");
        week3_Activities.add("Week 3 activity 4");
        week3_Activities.add("Week 3 activity 5");

        week4_Activities.add("Week 4 activity 1");
        week4_Activities.add("Week 4 activity 2");
        week4_Activities.add("Week 4 activity 3");
        week4_Activities.add("Week 4 activity 4");
        week4_Activities.add("Week 4 activity 5");

        week5_Activities.add("Week 5 activity 1");
        week5_Activities.add("Week 5 activity 2");
        week5_Activities.add("Week 5 activity 3");
        week5_Activities.add("Week 5 activity 4");
        week5_Activities.add("Week 5 activity 5");

        week6_Activities.add("Week 6 activity 1");
        week6_Activities.add("Week 6 activity 2");
        week6_Activities.add("Week 6 activity 3");
        week6_Activities.add("Week 6 activity 4");
        week6_Activities.add("Week 6 activity 5");

        week7_Activities.add("Week 7 activity 1");
        week7_Activities.add("Week 7 activity 2");
        week7_Activities.add("Week 7 activity 3");
        week7_Activities.add("Week 7 activity 4");
        week7_Activities.add("Week 7 activity 5");

        week8_Activities.add("Week 8 activity 1");
        week8_Activities.add("Week 8 activity 2");
        week8_Activities.add("Week 8 activity 3");
        week8_Activities.add("Week 8 activity 4");
        week8_Activities.add("Week 8 activity 5");

        switch (displayString){
            case "1":{
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, week1_Activities);
                activities_ListView.setAdapter(arrayAdapter);
                break;
            }case "2":{
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, week2_Activities);
                activities_ListView.setAdapter(arrayAdapter);
                break;
            }case "3":{
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, week3_Activities);
                activities_ListView.setAdapter(arrayAdapter);
                break;
            }case "4":{
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, week4_Activities);
                activities_ListView.setAdapter(arrayAdapter);
                break;
            }case "5":{
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, week5_Activities);
                activities_ListView.setAdapter(arrayAdapter);
                break;
            }case "6":{
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, week6_Activities);
                activities_ListView.setAdapter(arrayAdapter);
                break;
            }case "7":{
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, week7_Activities);
                activities_ListView.setAdapter(arrayAdapter);
                break;
            }case "8":{
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, week8_Activities);
                activities_ListView.setAdapter(arrayAdapter);
                break;
            }
        }

    }
}
