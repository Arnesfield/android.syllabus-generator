package com.ragew.code.forge_v2;

import android.content.ContentValues;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ragew.code.forge_v2.Utils.SuperTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SuperTask.TaskListener {

    //Declare variables here

    private String firstname;
    private String lastname;
    private String fullname;

    private TextView usernameTV;
    private TextView titleTV ;

    private int uid;

    //Shared preferences variables
    private static final String NAME_PREF = "name_pref";
    private static final String NAME_ID = "name_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //After the loading is finished,
        //the preloader will be dismissed
        new SpotsDialog(HomePage.this, R.style.Loader).dismiss();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //View of the navigation bar
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Get the header view of the navigation view
        View headerView = navigationView.getHeaderView(0);

        //Get the intent values
        Bundle userDetails = getIntent().getExtras();

        //Get extras
        uid = getIntent().getIntExtra("uid", -1);

        //If there is no uid
        if (uid == -1){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //Get current name from shared pref
        SharedPreferences sharedPreferences = getSharedPreferences(NAME_PREF, MODE_PRIVATE);
        fullname = sharedPreferences.getString(NAME_ID,fullname);

        //Check if there are values
        if (userDetails != null) {
            firstname = userDetails.getString("fname");
            lastname = userDetails.getString("lname");
        }

        //Assign the values to the header view
        usernameTV = headerView.findViewById(R.id.usernameTV);
        titleTV = headerView.findViewById(R.id.titleTV);

        //Assign the name to the header
        fullname = firstname + " " +lastname;
        usernameTV.setText(fullname.toString());

        //Save names to sharedpref
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME_ID, fullname);
        editor.apply();
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
        getMenuInflater().inflate(R.menu.home_page, menu);
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

    private CourseFragment courseFragment;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_courses) {
            courseFragment = new CourseFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.baseLayout,courseFragment).commit();
            getFragmentManager().popBackStackImmediate();
        } else if (id == R.id.nav_logout) {
            onLoggedOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //logoutListener
    public void onLoggedOut(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("logout",1);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTaskRespond(String id, String json) {
        //Retrieve the values of the courses
        String string = json;
        switch (id){
            case "courses":{
                try{
                    JSONObject jsonObject = new JSONObject(string);
                    JSONArray courses = jsonObject.getJSONArray("courses");
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
