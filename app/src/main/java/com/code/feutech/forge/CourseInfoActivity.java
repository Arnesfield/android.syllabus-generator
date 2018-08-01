package com.code.feutech.forge;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.code.feutech.forge.items.Course;
import com.code.feutech.forge.items.Syllabus;
import com.code.feutech.forge.items.Tags;
import com.code.feutech.forge.interfaces.OnLoadingListener;
import com.code.feutech.forge.utils.TaskCreator;
import com.github.rjeschke.txtmark.Processor;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Arrays;

public class CourseInfoActivity extends AppCompatActivity
        implements TaskCreator.TaskListener, OnLoadingListener, TabbedActivityListener {

    private Course course;
    private int courseId;
    private ArrayList<Syllabus> syllabiList;

    private View noDataContainer;
    private TextView noDataText;
    private View loadingContainer;
    private View courseInfoLoader;
    private CollapsingToolbarLayout toolbarLayout;
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        // get data from extras
        try {
            courseId = getIntent().getExtras().getInt("courseId", -1);
            if (courseId == -1) throw new Exception();
        } catch (Exception ignored) {
            finish();
            return;
        }

        // set components
        noDataContainer = findViewById(R.id.no_data_container);
        noDataText = findViewById(R.id.no_data_text);
        loadingContainer = findViewById(R.id.loading_container);
        courseInfoLoader = findViewById(R.id.course_info_loader);
        toolbarLayout = findViewById(R.id.course_info_toolbar_layout);
        appBarLayout = findViewById(R.id.course_info_appbar);
        Button noDataBtnRefresh = findViewById(R.id.no_data_btn_refresh);

        // set listeners
        noDataBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetch(view);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.course_info_toolbar);
        setSupportActionBar(toolbar);

        // set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*
          The {@link ViewPager} that will host the section contents.
         */
        mViewPager = (ViewPager) findViewById(R.id.course_info_view_pager);

        // tab
        tabLayout = (TabLayout) findViewById(R.id.course_info_tabs);

        fetch(noDataBtnRefresh);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPagerAndTabs();
    }

    private void fetch(View view) {
        // execute here
        onLoading();
        TaskCreator.execute(view.getContext(), this, "course", TaskConfig.COURSES_URL);
    }

    private void setPagerAndTabs() {
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
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
    }

    public static void setCourseInfoData(View view, View divider, int title, String text) {
        setCourseInfoData(view, divider, title, text, null, false);
    }

    public static void setCourseInfoData(View view, View divider, int title, String text, String[] tags, boolean rect) {
        String strTitle = view.getContext().getResources().getString(title);
        setCourseInfoData(view, divider, strTitle, text, tags, rect);
    }

    public static void setCourseInfoData(View view, View divider, String title, String text, String[] tags, boolean rect) {
        // first, if text is null or empty, then don't mind putting this view hehe
        if (tags != null && tags.length > 0) {
            // empty if to not proceed to else if below if there is a tags arg
        } else if (text == null || text.trim().isEmpty()) {
            view.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            return;
        } else {
            view.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
        }

        final TextView tvTitle = view.findViewById(R.id.component_course_info_title);
        final HtmlTextView tvText = view.findViewById(R.id.component_course_info_text);
        final FlexboxLayout layoutTags = view.findViewById(R.id.component_course_info_tags_container);

        if (!(text == null || text.trim().isEmpty())) {
            // text is in markdown, so convert it to html!
            tvText.setHtml(Processor.process(text));
            tvText.setVisibility(View.VISIBLE);
        } else {
            tvText.setVisibility(View.GONE);
        }

        // set values
        tvTitle.setText(title);

        // set tags
        Tags.setTagsInLayout(view, layoutTags, tags, rect);
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
        getMenuInflater().inflate(R.menu.menu_course_info, menu);
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


    // TabbedActivityListener
    @Override
    public void setData(View view, int index, boolean force) {
        if (force || index == 0) {
            // set view components
            final View mainView = view.findViewById(R.id.course_info_info_main);
            final View unitsView = view.findViewById(R.id.course_info_info_units);
            final View descriptionView = view.findViewById(R.id.course_info_info_description);
            final View prerequisitesView = view.findViewById(R.id.course_info_info_prerequisites);
            final View corequisitesView = view.findViewById(R.id.course_info_info_corequisites);
            final View tagsView = view.findViewById(R.id.course_info_info_tags);
            // dividers
            final View divider1 = view.findViewById(R.id.course_info_info_divider_1);
            final View divider2 = view.findViewById(R.id.course_info_info_divider_2);
            final View divider3 = view.findViewById(R.id.course_info_info_divider_3);
            final View divider4 = view.findViewById(R.id.course_info_info_divider_4);
            final View divider5 = view.findViewById(R.id.course_info_info_divider_5);

            // set here
            setCourseInfoData(mainView, divider1, R.string.course_info_info_main, course.getTitle());
            setCourseInfoData(unitsView, divider2, R.string.course_info_info_units, course.getUnitsText());
            setCourseInfoData(descriptionView, divider3, R.string.course_info_info_description, course.getDescription());
            setCourseInfoData(
                    prerequisitesView,
                    divider4,
                    R.string.course_info_info_prerequisites,
                    null, course.getRelatedCoursesNames(course.getPrerequisites()),
                    true);
            setCourseInfoData(
                    corequisitesView,
                    divider5,
                    R.string.course_info_info_corequisites,
                    null, course.getRelatedCoursesNames(course.getCorequisites()),
                    true);
            setCourseInfoData(tagsView, divider5, R.string.course_info_info_tags, null, course.getTags(), false);
        }
        // however, that's just the info lol
        // now set the syllabi
        if (force || index == 1) {
            if (syllabiList == null) {
                syllabiList = new ArrayList<>(Arrays.asList(course.getSyllabi()));
            }

            // set adapter when list is done
            ListView listView = view.findViewById(R.id.course_info_syllabi_list_view);
            if (listView.getAdapter() == null) {
                ArrayAdapter<Syllabus> adapter = new Syllabus.SyllabusArrayAdapter(this, android.R.layout.simple_list_item_1, syllabiList);
                listView.setAdapter(adapter);
            } else {
                ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
            }

            // also set listener for listView
            if (listView.getOnItemClickListener() == null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final Syllabus syllabus = CourseInfoActivity.this.syllabiList.get(i);

                        final Intent intent = new Intent(CourseInfoActivity.this, SyllabusActivity.class);
                        intent.putExtra("syllabusId", syllabus.getId());
                        startActivity(intent);
                    }
                });
            }

            // though, if there is no syllabi, then reveal the frown
            if (course.getSyllabi().length == 0) {
                TextView tvSyllabiEmpty = view.findViewById(R.id.course_info_syllabi_no_data_text);
                tvSyllabiEmpty.setVisibility(View.VISIBLE);
            }
        }
    }

    // task listener methods
    @Override
    public void onTaskRespond(String id, String json) throws Exception {
        JSONObject response = new JSONObject(json);
        boolean success = TaskCreator.isSuccessful(response);

        if (!success) {
            throw new Exception("Request failure.");
        }

        final JSONArray courses = response.getJSONArray("courses");
        if (courses.length() != 1) {
            onNoData(R.string.no_data_course_info_text);
            return;
        }

        // get the course
        JSONObject jsonCourse = courses.getJSONObject(0);
        Course course = new Course(jsonCourse, true, true);

        // set data here and adapter
        this.course = course;
        // set title
        toolbarLayout.setTitle(course.getCode());

        setData(findViewById(R.id.course_info_root), 0, true);
        onHasData();
    }

    @Override
    public void onTaskError(String id, Exception e) {
        Snackbar.make(noDataText, R.string.error, Snackbar.LENGTH_LONG).show();
        onNoData(R.string.error_frown);
    }

    @Override
    public ContentValues setRequestValues(String id, ContentValues contentValues) {
        contentValues.put("cid", courseId);
        contentValues.put("withRelated", true);
        contentValues.put("withSyllabi", true);
        return contentValues;
    }

    // appbar ux
    private void doLayoutState(final boolean state) {
        appBarLayout.setExpanded(state);
        appBarLayout.setNestedScrollingEnabled(state);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        if (params.getBehavior() == null) {
            params.setBehavior(new AppBarLayout.Behavior());
        }
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return state;
            }
        });
        toolbarLayout.setTitleEnabled(state);
    }

    // loading listener
    @Override
    public void onHasData() {
        doLayoutState(true);
        courseInfoLoader.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.GONE);
        noDataContainer.setVisibility(View.GONE);
    }

    @Override
    public void onNoData() {
        doLayoutState(false);
        courseInfoLoader.setVisibility(View.VISIBLE);
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
        doLayoutState(false);
        courseInfoLoader.setVisibility(View.VISIBLE);
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

        private TabbedActivityListener listener;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final int index = getArguments().getInt(ARG_SECTION_NUMBER);

            // if index is 0, then this is info
            // else, syllabi

            int layoutId = index == 0 ? R.layout.fragment_course_info_info : R.layout.fragment_course_info_syllabi;
            View rootView = inflater.inflate(layoutId, container, false);

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
            // Show 2 total pages.
            return 2;
        }
    }
}
