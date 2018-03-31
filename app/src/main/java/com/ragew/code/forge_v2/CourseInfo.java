package com.ragew.code.forge_v2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CourseInfo extends AppCompatActivity {

    String description;
    String objectives;
    String unitsLec;
    String unitsLab;
    String descriptions[];
    String contents[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get intent values
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            description = bundle.getString("description");
            objectives = bundle.getString("objectives");
            unitsLec = bundle.getString("unitsLec");
            unitsLab = bundle.getString("unitsLab");
        } else {
            Toast.makeText(this,"No value",Toast.LENGTH_LONG).show();
        }

        //Arrays of String
        descriptions = new String[]{"Course Description", "Course Objectives", "unitsLec", "unitsLab"};
        contents = new String[]{description, objectives, unitsLec, unitsLab};

        //ListView
        ListView listView = findViewById(R.id.listView);

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

    }

    //Create custom adapter
    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return descriptions.length;
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
            convertView = getLayoutInflater().inflate(R.layout.course_info_listview,null);
            TextView contentView = convertView.findViewById(R.id.content);
            TextView descriptionView = convertView.findViewById(R.id.description);

            contentView.setText(contents[position]);
            descriptionView.setText(descriptions[position]);
            return convertView;
        }
    }//

}
