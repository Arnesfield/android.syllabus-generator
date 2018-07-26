package com.code.feutech.forge;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.feutech.forge.config.PreferencesList;
import com.code.feutech.forge.config.TaskConfig;
import com.code.feutech.forge.fragments.AssignmentsFragment;
import com.code.feutech.forge.fragments.CoursesFragment;
import com.code.feutech.forge.items.User;
import com.code.feutech.forge.utils.OnLoadingListener;
import com.code.feutech.forge.utils.TaskCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        AssignmentsFragment.OnFragmentInteractionListener,
        CoursesFragment.OnFragmentInteractionListener,
        TaskCreator.TaskListener {

    private static Fragment FRAGMENT;

    private static final int[] MENU_ITEM_IDS = new int[]{
            R.id.nav_assignments,
            R.id.nav_reviews
    };

    private static final int[][] MENU_ITEM_AUTH = new int[][]{
            {3},
            {5}
    };

    private NavigationView navigationView;

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        // set nav items
        this.setNavItems();
        // update user info too then set nav items again lol
        TaskCreator.execute(this, this, "user_data", TaskConfig.USERS_URL);

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
        } else if (id == R.id.nav_courses) {
            newFragment = new CoursesFragment();
            title = "Courses";
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

    private void setNavItems() {
        Menu menu = navigationView.getMenu();
        try {
            // get user auth
            final User user = User.getUserFromSharedPref(this);

            for (int i = 0; i < MENU_ITEM_IDS.length; i++) {
                MenuItem item = menu.findItem(MENU_ITEM_IDS[i]);
                // depending on user auth, reveal the menu item
                item.setVisible(user.hasAuth(MENU_ITEM_AUTH[i]));
            }

            // from here, set also the name of user
            final View headerView = navigationView.getHeaderView(0);
            final TextView tvTitle = headerView.findViewById(R.id.nav_title);
            final TextView tvSubtitle = headerView.findViewById(R.id.nav_subtitle);
            final ImageView imageView = headerView.findViewById(R.id.nav_image_view);
            final TextView textView = headerView.findViewById(R.id.nav_no_image_text);

            tvTitle.setText(user.getName());
            tvSubtitle.setText(user.getUsername());
            user.loadImage(this, imageView, textView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getMyTitle(Fragment fragment) {
        if (fragment instanceof AssignmentsFragment) {
            return ((AssignmentsFragment) fragment).getAppTitle();
        } else if (fragment instanceof CoursesFragment) {
            return ((CoursesFragment) fragment).getAppTitle();
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

        if (id == "user_data") {
            final JSONArray users = response.getJSONArray("users");
            if (users.length() == 1) {
                JSONObject jsonUser = users.getJSONObject(0);
                // save this user to shared pref
                SharedPreferences sharedPreferences = getSharedPreferences(PreferencesList.PREF_LOGIN, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PreferencesList.PREF_USER_JSON, jsonUser.toString());
                editor.apply();
                // then set nav items again
                this.setNavItems();
            }
            // do nothing
        } else if (id == "assignments" || id == "reviews") {
            final JSONArray assigns = response.getJSONArray("assigns");
            if (assigns.length() == 0) {
                // message
                int msg = id == "assignments" ? R.string.no_data_assignments_text : R.string.no_data_reviews_text;
                ((OnLoadingListener) FRAGMENT).onNoData(msg);
                return;
            }
            // set data in assignments
            ((AssignmentsFragment) FRAGMENT).setData(assigns);
        } else if (id == "courses") {
            final JSONArray courses = response.getJSONArray("courses");
            if (courses.length() == 0) {
                // message
                ((OnLoadingListener) FRAGMENT).onNoData(R.string.no_data_courses_text);
                return;
            }
            // set data in assignments
            ((CoursesFragment) FRAGMENT).setData(courses);
        }
    }

    @Override
    public void onTaskError(String id, Exception e) {
        Log.d("tagx", e.toString());
        if (id != "user_data" && FRAGMENT != null) {
            ((OnLoadingListener) FRAGMENT).onNoData(R.string.error_frown);
        }
    }

    @Override
    public ContentValues setRequestValues(String id, ContentValues contentValues) {
        final SharedPreferences sharedPreferences = getSharedPreferences(PreferencesList.PREF_LOGIN, MODE_PRIVATE);

        if (id == "user_data") {
            int uid = sharedPreferences.getInt(PreferencesList.PREF_USER_ID, -1);
            contentValues.put("id", uid);
        } else if (id == "assignments" || id == "reviews") {
            // get id from sharedpref
            int uid = sharedPreferences.getInt(PreferencesList.PREF_USER_ID, -1);
            contentValues.put("id", uid);
        } else if (id == "courses") {

        }

        return contentValues;
    }
}
