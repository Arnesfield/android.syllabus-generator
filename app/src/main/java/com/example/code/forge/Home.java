package com.example.code.forge;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.code.forge.utils.DialogCreator;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DialogCreator.DialogActionListener {

    //Create drawer essentials
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar m_toolbar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Declare variables
        String fname = "",mname = "",lname = "",m_id = "",m_username = "",m_password = "",m_status = "",m_type = "";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sample dialog creator
                DialogCreator.create(Home.this, "someDialog")
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

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        //Button b_logout = findViewById(R.id.logout); //Button
        TextView userValues = findViewById(R.id.user_details);
        TextView m_fullName = headerView.findViewById(R.id.full_name);

        if (preValues != null){
            fname = preValues.getString("f_name");
            mname = preValues.getString("m_name");
            lname = preValues.getString("l_name");
            m_id = preValues.getString("m_id");
            m_username = preValues.getString("m_username");
            m_password = preValues.getString("m_password");
            m_status = preValues.getString("m_status");
            m_type = preValues.getString("m_type");
        }

        userValues.setText("First Name: " + fname + "\nMiddle Name: " + mname
                + "\nLast Name: " + lname + "\nUser ID: " + m_id + "\nUsername: " + m_username
                + "\nPassword: " + m_password + "\nUser status:" + m_status + "\nUser type: " + m_type);

        m_fullName.setText(fname.substring(0,1).toUpperCase() + fname.substring(1) + " " + mname.substring(0,1).toUpperCase()+". " + lname.substring(0,1).toUpperCase() + lname.substring(1));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent syllabusIntent;
        Intent teachingPlanIntent;
        Intent scheduleIntent;
        Intent insMaterialsIntent;
        Intent homeIntent;
        Intent coursesIntent;
        if (id == R.id.nav_syllabusList) {
            syllabusIntent = new Intent(this, Syllabus.class);
            startActivity(syllabusIntent);
            finish();
        } else if (id == R.id.nav_teachingPlan) {
            teachingPlanIntent = new Intent(this,TeachingPlan.class);
            startActivity(teachingPlanIntent);
            finish();
        } else if (id == R.id.nav_schedules) {
            scheduleIntent = new Intent(this,Schedules.class);
            startActivity(scheduleIntent);
            finish();
        } else if (id == R.id.nav_insMaterials) {
            insMaterialsIntent = new Intent(this,InstructionalMaterials.class);
            startActivity(insMaterialsIntent);
            finish();
        } else if (id == R.id.nav_courses) {
            coursesIntent = new Intent(this,Courses.class);
            startActivity(coursesIntent);
            finish();
        } else if (id == R.id.nav_home){
            homeIntent = new Intent(this, Home.class);
            startActivity(homeIntent);
            finish();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(Home.this,"Logout successful",Toast.LENGTH_LONG).show();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
}
