package com.code.feutech.forge;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.code.feutech.forge.config.PreferencesList;
import com.code.feutech.forge.config.TaskConfig;
import com.code.feutech.forge.fragments.AssignmentsFragment;
import com.code.feutech.forge.utils.OnLoadingListener;
import com.code.feutech.forge.utils.TaskCreator;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AssignmentsFragment.OnFragmentInteractionListener, TaskCreator.TaskListener {

    private static Fragment FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // if fragment not set
        if (FRAGMENT != null) {
            setFragment(FRAGMENT);
        } else {
            // create default fragment and set
        }
    }

    @Override
    protected void onDestroy() {
        FRAGMENT = null;
        super.onDestroy();
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
        getMenuInflater().inflate(R.menu.main, menu);
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
        Fragment newFragment = null;
        int id = item.getItemId();
        String title = "Forge";
        if (id == R.id.nav_dashboard) {
            // Handle the camera action
        } else if (id == R.id.nav_assignments) {
            newFragment = new AssignmentsFragment(TaskConfig.ASSIGNS_MY_URL, "assignments");
            title = "Assignments";
        } else if (id == R.id.nav_reviews) {
            newFragment = new AssignmentsFragment(TaskConfig.REVIEWS_URL, "reviews");
            title = "Reviews";
        } else if (id == R.id.nav_logout) {
            doLogout();
            return true;
        }

        // set FRAGMENT here
        setFragment(newFragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getMyTitle(Fragment fragment) {
        if (fragment instanceof AssignmentsFragment) {
            String requestId = ((AssignmentsFragment)fragment).getRequestId();
            return requestId == "assignments" ? "Assignments" : "Reviews";
        }
        return "Forge";
    }

    private void setFragment(Fragment newFragment) {
        if (newFragment != null) {
            final FragmentManager manager = getSupportFragmentManager();
            final FragmentTransaction transaction = manager.beginTransaction();

            if (FRAGMENT == null) {
                transaction.add(R.id.main_base_layout, newFragment);
            } else {
                transaction.replace(R.id.main_base_layout, newFragment);
            }

            // set as current fragment
            FRAGMENT = newFragment;

            transaction.commit();

            // then set title
            setTitle(getMyTitle(FRAGMENT));
        }
    }

    private void doLogout() {
        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesList.PREF_LOGIN, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PreferencesList.PREF_USER_ID);
        editor.remove(PreferencesList.PREF_USER_JSON);
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // task listener
    @Override
    public void onTaskRespond(String id, String json) throws Exception {
        JSONObject response = new JSONObject(json);
        boolean success = TaskCreator.isSuccessful(response);

        if (!success) {
            throw new Exception("Request failure.");
        }

        if (id == "assignments" || id == "reviews") {
            final JSONArray assigns = response.getJSONArray("assigns");
            if (assigns.length() == 0) {
                // message
                int msg = id == "assignments" ? R.string.no_data_assignments_text : R.string.no_data_reviews_text;
                ((OnLoadingListener) FRAGMENT).onNoData(msg);
                return;
            }
            // set data in assignments
            ((AssignmentsFragment) FRAGMENT).setData(assigns);
        }
    }

    @Override
    public void onTaskError(String id, Exception e) {
        if (FRAGMENT != null) {
            ((OnLoadingListener) FRAGMENT).onNoData(R.string.error_frown);
        }
    }

    @Override
    public ContentValues setRequestValues(String id, ContentValues contentValues) {
        if (id == "assignments" || id == "reviews") {
            // get id from sharedpref
            SharedPreferences sharedPreferences = getSharedPreferences(PreferencesList.PREF_LOGIN, MODE_PRIVATE);
            int uid = sharedPreferences.getInt(PreferencesList.PREF_USER_ID, -1);

            contentValues.put("id", uid);
        }

        return contentValues;
    }
}
