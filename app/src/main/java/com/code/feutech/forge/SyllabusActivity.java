package com.code.feutech.forge;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.code.feutech.forge.config.TaskConfig;
import com.code.feutech.forge.interfaces.TabbedActivityListener;
import com.code.feutech.forge.items.CloPoMap;
import com.code.feutech.forge.items.Curriculum;
import com.code.feutech.forge.items.GradingSystem;
import com.code.feutech.forge.items.Syllabus;
import com.code.feutech.forge.interfaces.OnLoadingListener;
import com.code.feutech.forge.items.Tags;
import com.code.feutech.forge.items.WeeklyActivity;
import com.code.feutech.forge.utils.TaskCreator;
import com.github.rjeschke.txtmark.Processor;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Arrays;

public class SyllabusActivity extends AppCompatActivity
        implements TaskCreator.TaskListener, OnLoadingListener, TabbedActivityListener {

    private int syllabusId;
    private Syllabus syllabus;
    private ArrayList<Curriculum.Item> curriculumItemList;
    private ArrayList<CloPoMap.Item> cloItemList;
    private ArrayList<WeeklyActivity> weeklyActivitiesItemList;
    private ArrayList<GradingSystem.Item> gradingSystemItemList;

    private View noDataContainer;
    private TextView noDataText;
    private View loadingContainer;
    private View syllabusLoader;
    private TabLayout tabLayout;
    private ViewPager mViewPager;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.syllabus_toolbar);
        setSupportActionBar(toolbar);

        // set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
          The {@link android.support.v4.view.PagerAdapter} that will provide
          fragments for each of the sections. We use a
          {@link FragmentPagerAdapter} derivative, which will keep every
          loaded fragment in memory. If this becomes too memory intensive, it
          may be best to switch to a
          {@link android.support.v4.app.FragmentStatePagerAdapter}.
         */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /*
          The {@link ViewPager} that will host the section contents.
         */
        mViewPager = (ViewPager) findViewById(R.id.syllabus_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.syllabus_tabs);

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

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (tabLayout != null && tabLayout.getSelectedTabPosition() != 0) {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(0);
                return;
            }
        }
        super.onBackPressed();
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

    // for setData
    private void setDataActivities(View view, int index, int actualPosition, boolean force) throws Exception {
        if (!(force || index == actualPosition)) {
            return;
        }
        
        final HtmlTextView activitiesSubtitle = view.findViewById(R.id.syllabus_activities_subtitle);
        final ListView activitiesListView = view.findViewById(R.id.syllabus_activities_list_view);

        // get total hours
        activitiesSubtitle.setHtml("Total hours: <b>" + syllabus.getFormattedTotalHours() + " hours</b>");

        if (weeklyActivitiesItemList == null) {
            weeklyActivitiesItemList = new ArrayList<>(Arrays.asList(syllabus.getWeeklyActivities()));
        }

        // set adapter
        if (activitiesListView.getAdapter() == null) {
            WeeklyActivity.WeeklyActivitiesArrayAdapter adapter = new WeeklyActivity.WeeklyActivitiesArrayAdapter(this, android.R.layout.simple_list_item_1, weeklyActivitiesItemList);
            activitiesListView.setAdapter(adapter);
        } else {
            ((ArrayAdapter) activitiesListView.getAdapter()).notifyDataSetChanged();
        }

        // set onclick for listView
        activitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // check first if selected is object
                final WeeklyActivity activity = syllabus.getWeeklyActivities()[i];

                if (!activity.isAsObject()) {
                    final String weekText = WeeklyActivity.createWeekText(i, syllabus.getWeeklyActivities());

                    new AlertDialog.Builder(SyllabusActivity.this)
                        .setTitle("Week " + weekText)
                        .setView(WeeklyActivity.createDialogView(getLayoutInflater(), syllabus, i))
                        .setPositiveButton(R.string.dialog_dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                    return;
                }

                // pass syllabus to weekly activities
                Intent intent = new Intent(view.getContext(), WeeklyActivitiesActivity.class);
                intent.putExtra("syllabus", syllabus.getJson());
                // also put index of clicked
                intent.putExtra("index", i);
                startActivity(intent);
            }
        });
    }

    private void setDataGrading(View view, int index, int actualPosition, boolean force) throws Exception {
        if (!(force || index == actualPosition)) {
            return;
        }

        final ListView gradingListView = view.findViewById(R.id.syllabus_grading_list_view);
        final View gradingWarningView = view.findViewById(R.id.syllabus_grading_warning);

        // if not latest, show this warning
        final TextView tvWarningText = gradingWarningView.findViewById(R.id.warning_view_text);
        tvWarningText.setText(R.string.syllabus_grading_warning);
        gradingWarningView.setVisibility(syllabus.getGradingSystem().isLatest() ? View.GONE : View.VISIBLE);

        if (gradingSystemItemList == null) {
            gradingSystemItemList = new ArrayList<>(Arrays.asList(syllabus.getGradingSystem().getItems()));
        }

        // set adapter when list is done
        if (gradingListView.getAdapter() == null) {
            ArrayAdapter<GradingSystem.Item> adapter = new GradingSystem.GradingSystemItemArrayAdapter(this, android.R.layout.simple_list_item_1, gradingSystemItemList);
            gradingListView.setAdapter(adapter);
        } else {
            ((ArrayAdapter) gradingListView.getAdapter()).notifyDataSetChanged();
        }
    }

    private void setDataCurriculum(View view, int index, int actualPosition, boolean force) throws Exception {
        if (!(force || index == actualPosition)) {
            return;
        }

        final TextView curriculumTitle = view.findViewById(R.id.syllabus_curriculum_title);
        final HtmlTextView curriculumSubtitle = view.findViewById(R.id.syllabus_curriculum_subtitle);
        final ListView curriculumListView = view.findViewById(R.id.syllabus_curriculum_list_view);
        final View curriculumWarningView = view.findViewById(R.id.syllabus_curriculum_warning);

        // set values
        curriculumTitle.setText(syllabus.getCurriculum().getLabel());
        curriculumSubtitle.setHtml("Last updated on <b>" + syllabus.getUpdatedAt().convert("MMMM dd, yyyy hh:ss a") + "</b>.");
        // if not latest, show this warning
        final TextView tvWarningText = curriculumWarningView.findViewById(R.id.warning_view_text);
        tvWarningText.setText(R.string.syllabus_curriculum_warning);
        curriculumWarningView.setVisibility(syllabus.getCurriculum().isLatest() ? View.GONE : View.VISIBLE);

        if (curriculumItemList == null) {
            curriculumItemList = new ArrayList<>(Arrays.asList(syllabus.getCurriculum().getItems()));
        }

        // set adapter when list is done
        if (curriculumListView.getAdapter() == null) {
            ArrayAdapter<Curriculum.Item> adapter = new Curriculum.CurriculumItemArrayAdapter(this, android.R.layout.simple_list_item_1, curriculumItemList);
            curriculumListView.setAdapter(adapter);
        } else {
            ((ArrayAdapter) curriculumListView.getAdapter()).notifyDataSetChanged();
        }
    }

    private void setDataClos(View view, int index, int actualPosition, boolean force) throws Exception {
        if (!(force || index == actualPosition)) {
            return;
        }

        final ListView closListView = view.findViewById(R.id.syllabus_clos_list_view);
        final FlexboxLayout legendContainer = view.findViewById(R.id.syllabus_clos_legend_container);

        // set legend
        Tags.setTagsInLayout(closListView, legendContainer, syllabus.getCloPoMap().getLegendString(), true);

        if (cloItemList == null) {
            cloItemList = new ArrayList<>(Arrays.asList(syllabus.getCloPoMap().getItems()));
        }

        // set adapter when list is done
        if (closListView.getAdapter() == null) {
            ArrayAdapter<CloPoMap.Item> adapter = new CloPoMap.CloArrayAdapter(this, android.R.layout.simple_list_item_1, cloItemList);
            closListView.setAdapter(adapter);
        } else {
            ((ArrayAdapter) closListView.getAdapter()).notifyDataSetChanged();
        }
    }

    private void setDataReferences(View view, int index, int actualPosition, boolean force) throws Exception {
        if (!(force || index == actualPosition)) {
            return;
        }

        final HtmlTextView referencesSubtitle = view.findViewById(R.id.syllabus_references_subtitle);
        final ListView referencesListView = view.findViewById(R.id.syllabus_references_list_view);

        // get references
        final String[] items = syllabus.getReferences();

        // set values
        referencesSubtitle.setHtml("Total references: <b>" + items.length + "</b>");

        if (referencesListView.getAdapter() == null) {
            referencesListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = convertView;

                    if (view == null) {
                        view = inflater.inflate(R.layout.item_simple_text_view, null);
                    }

                    final String item = Processor.process(getItem(position));

                    // set views
                    final HtmlTextView tvText = view.findViewById(R.id.item_weekly_activity_simple_text);

                    // set values
                    tvText.setHtml(item);

                    return view;
                }
            });
        } else {
            ((ArrayAdapter) referencesListView.getAdapter()).notifyDataSetChanged();
        }
    }

    // TabbedActivityListener
    @Override
    public void setData(View view, int index, boolean force) throws Exception {
        if (syllabus == null || view == null) {
            return;
        }

        setDataActivities(view, index, 0, force);
        setDataGrading(view, index, 1, force);
        setDataCurriculum(view, index, 2, force);
        setDataClos(view, index, 3, force);
        setDataReferences(view, index, 4, force);
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

        // also check for the latest curriculum
        final JSONObject jsonLatestCurriculum = response.getJSONObject("latestCurriculum");
        final Curriculum latestCurriculum = new Curriculum(jsonLatestCurriculum);

        // get also latest grading system
        final JSONArray jsonLatestGrading = response.getJSONArray("latestGrading");
        final GradingSystem latestGrading = new GradingSystem(jsonLatestGrading);

        // get the syllabus
        JSONObject jsonSyllabus = syllabi.getJSONObject(0);
        Syllabus syllabus = new Syllabus(jsonSyllabus, latestCurriculum, latestGrading);

        // set data here and adapter
        this.syllabus = syllabus;

        // set title
        getSupportActionBar().setTitle(syllabus.getVersion());
        getSupportActionBar().setSubtitle(syllabus.getCourse().getCode());

        // set data
        // handle exception here when views of index 0 and above are not created
        try {
            setData(findViewById(R.id.syllabus_root), 0, true);
        } catch (Exception e) {
            Log.e("tagx", "Error: ", e);
        }

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
        contentValues.put("withLatestCurriculum", true);
        contentValues.put("withLatestGrading", true);
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

        public PlaceholderFragment() {}

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
                R.layout.fragment_syllabus_activities,
                R.layout.fragment_syllabus_grading,
                R.layout.fragment_syllabus_curriculum,
                R.layout.fragment_syllabus_clos,
                R.layout.fragment_syllabus_references
        };

        private TabbedActivityListener listener;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final int index = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = getView();

            if (rootView == null && index < LAYOUT_IDS.length) {
                rootView = inflater.inflate(LAYOUT_IDS[index], container, false);
            }

            try {
                listener.setData(rootView, index, false);
            } catch (Exception e) {
                Log.e("tagx", "Error: ", e);
            }

            return rootView;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof TabbedActivityListener) {
                listener = (TabbedActivityListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement TabbedActivityListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            listener = null;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayMap<Integer, Fragment> items;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            items = new ArrayMap<>();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            // get from map if exists
            Fragment fragment = items.get(position);

            if (fragment == null) {
                fragment = PlaceholderFragment.newInstance(position);
                items.put(position, fragment);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return PlaceholderFragment.LAYOUT_IDS.length;
        }
    }
}
