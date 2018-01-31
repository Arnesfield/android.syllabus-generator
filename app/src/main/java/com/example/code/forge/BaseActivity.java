package com.example.code.forge;

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

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DialogCreator.DialogActionListener {

    //Create drawer essentials
    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //Declare variables
        String fname = "",mname = "",lname = "";

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
        if (id == R.id.nav_syllabusList) {
            SyllabiFragment syllabiFragment = new SyllabiFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.baseLayout,syllabiFragment).commit();

        } else if (id == R.id.nav_teachingPlan) {
            TeachingPlanFragment teachingPlanFragment = new TeachingPlanFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.baseLayout,teachingPlanFragment).commit();

        } else if (id == R.id.nav_schedules) {
            SchedulesFragment schedulesFragment = new SchedulesFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.baseLayout,schedulesFragment).commit();

        } else if (id == R.id.nav_courses) {
            CourseFragment courseFragment = new CourseFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.baseLayout,courseFragment).commit();
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
}
