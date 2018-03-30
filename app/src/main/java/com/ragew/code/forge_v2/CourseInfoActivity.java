package com.ragew.code.forge_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class CourseInfoActivity extends AppCompatActivity {

    private String description;
    private String objectives;
    private String unitsLec;
    private String unitsLab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        //Get values from intent
        Bundle courseInfo = getIntent().getExtras();
        description = courseInfo.getString("description");
        objectives = courseInfo.getString("objectives");
        unitsLec = courseInfo.getString("unitsLec");
        unitsLab = courseInfo.getString("unitsLab");

        Toast.makeText(this,description
                + "\n" + objectives
                + "\n" + unitsLec
                + "\n" + unitsLab
                ,Toast.LENGTH_LONG).show();
    }


}
