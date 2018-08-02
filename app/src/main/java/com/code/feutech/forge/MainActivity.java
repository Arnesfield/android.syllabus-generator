package com.code.feutech.forge;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
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
import com.code.feutech.forge.fragments.DashboardFragment;
import com.code.feutech.forge.interfaces.AppTitleActivityListener;
import com.code.feutech.forge.items.Syllabus;
import com.code.feutech.forge.items.User;
import com.code.feutech.forge.interfaces.OnLoadingListener;
import com.code.feutech.forge.utils.TaskCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        AssignmentsFragment.OnFragmentInteractionListener,
        CoursesFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener,
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

    private Menu menu;
    private NavigationView navigationView;

    private String courseSearch;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            setFragment(new DashboardFragment());
        }

        checkForLogInMsg();
    }

    private void checkForLogInMsg() {
        // if did login, then show snackbar
        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesList.PREF_LOGIN, MODE_PRIVATE);
        boolean didLogin = sharedPreferences.getBoolean(PreferencesList.PREF_DID_LOG_IN, false);
        if (didLogin) {
            Snackbar.make(findViewById(R.id.drawer_layout), R.string.msg_did_log_in, Snackbar.LENGTH_LONG).show();
        }
        // then remove that prop
        sharedPreferences.edit().remove(PreferencesList.PREF_DID_LOG_IN).apply();
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
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchMenuItem = menu.findItem(R.id.action_search);

        if (FRAGMENT != null && FRAGMENT instanceof CoursesFragment) {
            searchMenuItem.setVisible(true);
            final SearchView searchView = (SearchView) searchMenuItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // execute and search
                    MainActivity.this.courseSearch = query;
                    TaskCreator.execute(MainActivity.this, MainActivity.this, "courses", TaskConfig.COURSES_URL);

                    if(!searchView.isIconified()) {
                        searchView.setIconified(true);
                    }
                    searchMenuItem.collapseActionView();

                    if (FRAGMENT instanceof CoursesFragment) {
                        ((CoursesFragment) FRAGMENT).didSearch(query);
                    }
                    return true;

                }
                @Override
                public boolean onQueryTextChange(String s) {
                    if (s != null && !s.trim().isEmpty()) {
                        MainActivity.this.courseSearch = s;
                        TaskCreator.execute(MainActivity.this, MainActivity.this, "courses", TaskConfig.COURSES_URL);
                        if (FRAGMENT instanceof CoursesFragment) {
                            ((CoursesFragment) FRAGMENT).didSearch(s);
                        }
                    }
                    return true;
                }
            });
        } else {
            searchMenuItem.setVisible(false);
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // reset search here
        courseSearch = null;

        Fragment newFragment = null;
        int id = item.getItemId();
        if (id == R.id.nav_dashboard) {
            newFragment = new DashboardFragment();
        } else if (id == R.id.nav_courses) {
            newFragment = new CoursesFragment();
        } else if (id == R.id.nav_assignments) {
            newFragment = new AssignmentsFragment(TaskConfig.ASSIGNS_MY_URL, "assignments");
        } else if (id == R.id.nav_reviews) {
            newFragment = new AssignmentsFragment(TaskConfig.REVIEWS_URL, "reviews");
        } else if (id == R.id.nav_logout) {
            doLogout();
            return true;
        }

        // set FRAGMENT here
        setFragment(newFragment);

        // reset menu
        invalidateOptionsMenu();

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
        if (
            fragment instanceof DashboardFragment ||
            fragment instanceof AssignmentsFragment ||
            fragment instanceof CoursesFragment
        ) {
            return ((AppTitleActivityListener) fragment).getAppTitle(getResources());
        }
        return getResources().getString(R.string.app_name);
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
        editor.remove(PreferencesList.PREF_DID_LOG_IN);
        editor.putBoolean(PreferencesList.PREF_DID_LOG_OUT, true);
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // course fragment listener
    @Override
    public void clearCoursesSearch() {
        courseSearch = null;
        TaskCreator.execute(MainActivity.this, MainActivity.this, "courses", TaskConfig.COURSES_URL);
        if (FRAGMENT instanceof CoursesFragment) {
            ((CoursesFragment) FRAGMENT).didSearch(courseSearch);
        }
        if (searchMenuItem != null) {
            searchMenuItem.collapseActionView();
        }
    }

    // dashboard fragment listener
    @Override
    public Syllabus[] getSyllabi() throws JSONException {
        return Syllabus.getOfflineSyllabi(this);
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
            if (courseSearch != null) {
                contentValues.put("search", courseSearch.trim());
            }
        }

        return contentValues;
    }
}
