package com.example.code.forge;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.code.forge.utils.DialogCreator;
import com.example.code.forge.utils.SuperTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DialogCreator.DialogActionListener, SuperTask.TaskListener {

    //Create drawer essentials
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private String fname = "",mname = "",lname = "";
    /*
        private void setupName(){
            SharedPreferences preferences = getSharedPreferences("UsernameAndPassword", MODE_PRIVATE);
            fname = preferences.getString("firstName",null);
            mname = preferences.getString("middleName",null);
            lname = preferences.getString("lastName",null);
        }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //Declare variable
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sample dialog creator
                DialogCreator.create(BaseActivity.this, "someDialog")
                        .setTitle("Title here")
                        .setMessage(R.string.test_string_only)
                        .setPositiveButton(R.string.btn_test_positive)
                        .setNegativeButton("Cancel")
                        .setNeutralButton("No thanks")
                        .show();

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Snackbar.make(fab, "Login Successful!", Snackbar.LENGTH_LONG).show();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Navigation View access list
        View headerView = navigationView.getHeaderView(0);

        //Retrieve values from past activity
        Bundle preValues = getIntent().getExtras();

        //Basic user actions
        TextView m_fullName = headerView.findViewById(R.id.full_name);

        if (preValues != null){
            fname = preValues.getString("f_name");
            mname = preValues.getString("m_name");
            lname = preValues.getString("l_name");
        }

        //Header text view
        m_fullName.setText(fname.substring(0,1).toUpperCase() + fname.substring(1) + " " + mname.substring(0,1).toUpperCase()+". " + lname.substring(0,1).toUpperCase() + lname.substring(1));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the BaseActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_search: {
                //Temporary design
                DialogCreator.create(BaseActivity.this, "someDialog")
                        .setTitle("This is the temporary search bar")
                        .setMessage(R.string.test_string_only)
                        .setPositiveButton(R.string.btn_test_positive)
                        .setNegativeButton("Cancel")
                        .setNeutralButton("No thanks")
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private CourseFragment courseFragment;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_syllabusList) {
            SyllabiFragment syllabiFragment = new SyllabiFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().add(R.id.baseLayout,syllabiFragment).commit();

        } else if (id == R.id.nav_teachingPlan) {
            TeachingPlanFragment teachingPlanFragment = new TeachingPlanFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().add(R.id.baseLayout,teachingPlanFragment).commit();

        } else if (id == R.id.nav_schedules) {
            SchedulesFragment schedulesFragment = new SchedulesFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().add(R.id.baseLayout,schedulesFragment).commit();

        } else if (id == R.id.nav_courses) {
            courseFragment = new CourseFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().add(R.id.baseLayout,courseFragment).commit();

        } else if (id == R.id.nav_logout) {
            this.finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onTaskRespond(String id, String json) {
        String testString = json;
        Log.d("courses", json);
        switch (id) {
            case "courses": {
                try {
                    JSONObject m_courseObject = new JSONObject(testString);
                    Log.d("Course content",m_courseObject.toString());
                    m_courseObject.getBoolean("courses");
                    // if parsed:
                    // wrong
                    Log.d("courses", "It s false");
                    return;
                }catch (JSONException e) {
                    e.printStackTrace();
                }

                // correct
                try {
                    JSONObject m_courseObject = new JSONObject(testString);
                    JSONArray courses = m_courseObject.getJSONArray("courses");
                    Log.d("courses", "It s array");
                    //Retrieve data
                    courseFragment.setCourses(courses);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public ContentValues setRequestValues(String id, ContentValues contentValues) {
        switch (id){
            case "courses":{
                contentValues.put("search","");
            }
        }
        return contentValues;
    }
}
