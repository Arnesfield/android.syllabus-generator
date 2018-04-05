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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ragew.code.forge_v2.Utils.SuperTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SuperTask.TaskListener {

    //Declare variables here

    //Shared preferences variables
    private static final String NAME_PREF = "name_pref";
    private static final String NAME_ID = "name_id";
    private String firstname;
    private String lastname;
    private String fullname;
    private TextView usernameTV;
    private TextView titleTV ;
    private int uid;

    //Fragments
    private CourseFragment courseFragment;
    private AssignsFragment assignsFragment;

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_assigns) {
            assignsFragment = new AssignsFragment();
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.baseLayout, assignsFragment).commit();
            getFragmentManager().popBackStackImmediate();
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
        String retrievedData = json;
        switch (id){
            case "courses":{
                try{
                    JSONObject jsonObject = new JSONObject(retrievedData);
                    JSONArray courses = jsonObject.getJSONArray("courses");
                    courseFragment.setCourses(courses);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            }
            case "assign":{
                String jsonObjectString;
                try {
                    JSONObject jsonObject = new JSONObject(retrievedData);
                    //LEVEL 0 JSON OBJECTS
                    //Get the level 0 values
                    //Log.d("Return",jsonObject.toString());

                    //Get the true false value
                    Boolean isTrueOrFalse = jsonObject.getBoolean("success");

                    //Assign value of json object to string variable
                    jsonObjectString = jsonObject.toString();

                    //Parse json object into json array
                    JsonObject assignsJsonObject = new JsonParser().parse(jsonObjectString).getAsJsonObject();

                    //Assign a jsonArray for the assigns array
                    JsonArray assignsJsonArray = assignsJsonObject.get("assigns").getAsJsonArray();


/*
                    //Get the value of content from json array and make it object
                    JsonObject contentJsonObject = assignsJsonArray.getAsJsonObject();

                    //Assign a jsonArray for the assigns array
                    JsonArray contentJsonArray = contentJsonObject.get("content").getAsJsonArray();
*/

                    //Get each json element from the assignsJsonArray JSONArray
                    for (JsonElement jsonElement : assignsJsonArray) {

                        //Get each json element and make it a json object
                        JsonObject assignsArrayJsonObject = jsonElement.getAsJsonObject();

                        //LEVEL 1 JSON OBJECTS
                        //Get the level 1 values of assigns array
                        /*String assignsID = assignsArrayJsonObject.get("id").getAsString();
                        String assignsCreatedAt = assignsArrayJsonObject.get("created_at").getAsString();
                        String assignsUpdatedAt = assignsArrayJsonObject.get("updated_at").getAsString();
                        String assignsStatus = assignsArrayJsonObject.get("status").getAsString();*/

                        //LEVEL 2 JSON OBJECTS
                        //Get the content json object from json assignsarrayjsonobject
                        JsonObject contentsJsonObject = assignsArrayJsonObject.get("content").getAsJsonObject();

                        //Get the created_by json object from json assignsarrayjsonobject
                        JsonObject created_byJsonObject = assignsArrayJsonObject.get("created_by").getAsJsonObject();
                        assignsFragment.setCreatedBy(created_byJsonObject);

                        //LEVEL 3 JSON OBJECTS
                        //get the assigned json object from the contentsjsonobject
                        JsonObject assignedJsonObject = contentsJsonObject.get("assigned").getAsJsonObject();

                        //LEVEL 4 JSON OBJECT

                        //get the user json object from the assigned json object
                        JsonObject userJsonObject = assignedJsonObject.get("user").getAsJsonObject();
                        assignsFragment.setAssignedBy(userJsonObject);

                        //get the course json object from the contentsjsonobject
                        JsonObject courseJsonObject = contentsJsonObject.get("course").getAsJsonObject();
                        assignsFragment.getCourseDetails(courseJsonObject);

                        //get the levels json array from the contentsjsonobject
                        JsonArray levelsJsonArray = contentsJsonObject.get("levels").getAsJsonArray();

                        //Get the contents of levelsJsonObject
                        for (JsonElement jsonElement1 : levelsJsonArray){
                            JsonArray levelsJsonArray2 = jsonElement1.getAsJsonArray();

                            for (JsonElement jsonElement2 : levelsJsonArray2) {
                                JsonObject levelsJsonArray3 = jsonElement2.getAsJsonObject();

                                JsonObject levelsUserJsonObject = levelsJsonArray3.get("user").getAsJsonObject();

                                //Get a specific value
//                                String testString = levelsUserJsonObject.get("fname").getAsString();

//                                Toast.makeText(LoginActivity.this, String.valueOf(testString) , Toast.LENGTH_LONG).show();
                            }

                        }

                        //Get the value from key fname
                        //String testName = userJsonObject.get("fname").getAsString();

//                        Toast.makeText(LoginActivity.this, String.valueOf(levelsJsonArray) , Toast.LENGTH_LONG).show();

                    }

//                    Toast.makeText(LoginActivity.this, String.valueOf(isTrueOrFalse),Toast.LENGTH_LONG).show();

                } catch (JSONException e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public ContentValues setRequestValues(String id, ContentValues contentValues) {
        switch (id){
            case "courses":{
                contentValues.put("search","");
                break;
            }
            case "assign":{
                contentValues.put("id",uid);
                break;
            }
        }
        return contentValues;
    }
}
