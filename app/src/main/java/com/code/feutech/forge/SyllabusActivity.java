package com.code.feutech.forge;

import android.content.ContentValues;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.code.feutech.forge.config.TaskConfig;
import com.code.feutech.forge.items.Course;
import com.code.feutech.forge.items.Syllabus;
import com.code.feutech.forge.utils.OnLoadingListener;
import com.code.feutech.forge.utils.TaskCreator;

import org.json.JSONArray;
import org.json.JSONObject;

public class SyllabusActivity extends AppCompatActivity implements TaskCreator.TaskListener, OnLoadingListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private int syllabusId;

    private View noDataContainer;
    private TextView noDataText;
    private View loadingContainer;
    private View syllabusLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);

        // get data from extras
        try {
            syllabusId = getIntent().getExtras().getInt("syllabusId", -1);
            if (syllabusId == -1) throw new Exception();
        } catch (Exception ignored) {
            finish();
            return;
        }

        // set components
        noDataContainer = findViewById(R.id.no_data_container);
        noDataText = findViewById(R.id.no_data_text);
        loadingContainer = findViewById(R.id.loading_container);
        syllabusLoader = findViewById(R.id.syllabus_loader);
        Button noDataBtnRefresh = findViewById(R.id.no_data_btn_refresh);

        // set listeners
        noDataBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetch(view);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fetch(noDataBtnRefresh);
    }

    private void fetch(View view) {
        // execute here
        onLoading();
        TaskCreator.execute(view.getContext(), this, "syllabus", TaskConfig.SYLLABI_URL);
    }

    private void setData(Syllabus syllabus) {
        getSupportActionBar().setTitle(syllabus.getVersion());
        getSupportActionBar().setSubtitle(syllabus.getCourse().getCode());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_syllabus, menu);
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

    // task listener methods
    @Override
    public void onTaskRespond(String id, String json) throws Exception {
        JSONObject response = new JSONObject(json);
        boolean success = TaskCreator.isSuccessful(response);

        if (!success) {
            throw new Exception("Request failure.");
        }

        final JSONArray syllabi = response.getJSONArray("syllabi");
        if (syllabi.length() != 1) {
            onNoData(R.string.no_data_syllabus_text);
            return;
        }

        // get the course
        JSONObject jsonSyllabus = syllabi.getJSONObject(0);
        Syllabus syllabus = new Syllabus(jsonSyllabus);

        // set data here and adapter
        setData(syllabus);
        onHasData();
    }

    @Override
    public void onTaskError(String id, Exception e) {
        Snackbar.make(noDataText, R.string.error, Snackbar.LENGTH_LONG).show();
        onNoData(R.string.error_frown);
    }

    @Override
    public ContentValues setRequestValues(String id, ContentValues contentValues) {
        contentValues.put("id", syllabusId);
        return contentValues;
    }

    // loading listener
    @Override
    public void onHasData() {
        syllabusLoader.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.GONE);
        noDataContainer.setVisibility(View.GONE);
    }

    @Override
    public void onNoData() {
        syllabusLoader.setVisibility(View.VISIBLE);
        loadingContainer.setVisibility(View.GONE);
        noDataContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoData(String msg) {
        noDataText.setText(msg);
        onNoData();
    }

    @Override
    public void onNoData(int resid) {
        noDataText.setText(resid);
        onNoData();
    }

    @Override
    public void onLoading() {
        syllabusLoader.setVisibility(View.VISIBLE);
        loadingContainer.setVisibility(View.VISIBLE);
        noDataContainer.setVisibility(View.GONE);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        // in order
        // curriculum
        // clo
        // activities
        // grading system
        // books / references
        // course
        // syllabus
        private static int[] LAYOUT_IDS = {
                R.layout.fragment_syllabus_curriculum
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final int index = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;

            if (index <= LAYOUT_IDS.length - 1) {
                rootView = inflater.inflate(LAYOUT_IDS[index], container, false);
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 7;
        }
    }
}
