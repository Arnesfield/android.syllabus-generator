package com.code.feutech.forge;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.code.feutech.forge.interfaces.TabbedActivityListener;
import com.code.feutech.forge.items.CloPoMap;
import com.code.feutech.forge.items.Syllabus;
import com.code.feutech.forge.items.Tags;
import com.code.feutech.forge.items.WeeklyActivity;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Arrays;

public class WeeklyActivitiesActivity extends AppCompatActivity implements TabbedActivityListener {

    private int index;
    private Syllabus syllabus;
    private ArrayList<CloPoMap.Item> cloItemList;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private TextView toolbarTitle;
    private TextView toolbarSubtitle;
    private LinearLayout toolbarTextContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_activities);

        // get syllabus
        try {
            final String jsonSyllabus = getIntent().getStringExtra("syllabus");
            index = getIntent().getIntExtra("index", -1);
            if (index == -1) {
                throw new Exception("No index set.");
            }
            syllabus = new Syllabus(new JSONObject(jsonSyllabus));
        } catch (Exception e) {
            Log.e("tagx", "Error: ", e);
            finish();
            return;
        }

        toolbar = (Toolbar) findViewById(R.id.weekly_activities_toolbar);
        setSupportActionBar(toolbar);

        // set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // tab
        tabLayout = (TabLayout) findViewById(R.id.weekly_activities_tabs);

        // set toolbar texts
        toolbarTitle = findViewById(R.id.weekly_activities_toolbar_title);
        toolbarSubtitle = findViewById(R.id.weekly_activities_toolbar_subtitle);
        toolbarTextContainer = findViewById(R.id.weekly_activities_toolbar_text_container);

        // set click
        toolbarTextContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final WeeklyActivitiesActivity context = WeeklyActivitiesActivity.this;
                final String[] strWeeks = WeeklyActivity.createWeekNoArray(syllabus.getWeeklyActivities(), "Week ", true);

                // create dialog here
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder
                    .setTitle(R.string.weekly_activities_dialog_title)
                    .setSingleChoiceItems(strWeeks, context.index, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // change the index and setData again

                            // if selected is not on object, show dialog instead
                            final WeeklyActivity activity = context.syllabus.getWeeklyActivities()[i];
                            if (!activity.isAsObject()) {
                                final String weekText = WeeklyActivity.createWeekText(i, syllabus.getWeeklyActivities());

                                new AlertDialog.Builder(context)
                                    .setTitle("Week " + weekText)
                                    .setView(WeeklyActivity.createDialogView(getLayoutInflater(), context.syllabus, i))
                                    .setPositiveButton(R.string.dialog_dismiss, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .show();
                                return;
                            }

                            context.index = i;
                            setPagerAndTabs();
                            try {
                                context.setData(context.findViewById(R.id.weekly_activities_root), context.tabLayout.getSelectedTabPosition(), true);
                            } catch (Exception e) {
                                Log.e("tagx", "Error: ", e);
                            }
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.dialog_dismiss, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
            }
        });

        setPagerAndTabs();
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
        /*
          The {@link ViewPager} that will host the section contents.
         */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.weekly_activities_view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weekly_activities, menu);
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

    // tabbedActivityListener
    @Override
    public void setData(View view, int index, boolean force) throws Exception {
        // from here, use weeklyActivities of this.index
        final WeeklyActivity activity = syllabus.getWeeklyActivities()[this.index];

        final String weekText = WeeklyActivity.createWeekText(this.index, syllabus.getWeeklyActivities());

        // set view data
        toolbarTitle.setText("Week " + weekText);
        toolbarSubtitle.setText(syllabus.getCourse().getCode());

        final TextView tvTitle = view.findViewById(R.id.weekly_activities_title);
        final HtmlTextView tvSubtitle = view.findViewById(R.id.weekly_activities_subtitle);
        final ListView listView = view.findViewById(R.id.weekly_activities_list_view);

        // depending on index, get strings
        String[] items;

        if (index == 0) {
            items = activity.getTopics();
            tvTitle.setText(R.string.weekly_activities_topics_title);
            tvSubtitle.setHtml("Total topics: <b>" + items.length + "</b>");
        } else if (index == 1) {
            items = activity.getIlos();
            tvTitle.setText(R.string.weekly_activities_ilos_title);
            tvSubtitle.setHtml("Total ILOs: <b>" + items.length + "</b>");
        } else if (index == 2) {
            items = activity.getAssessmentTasks();
            tvTitle.setText(R.string.weekly_activities_tasks_title);
            tvSubtitle.setHtml("Total tasks: <b>" + items.length + "</b>");
        } else if (index == 3) {
            items = activity.getTlaFaculty();
            tvTitle.setText(R.string.weekly_activities_tla_faculty_title);
            tvSubtitle.setHtml("Total activities: <b>" + items.length + "</b>");
        } else if (index == 4) {
            items = activity.getTlaStudent();
            tvTitle.setText(R.string.weekly_activities_tla_student_title);
            tvSubtitle.setHtml("Total activities: <b>" + items.length + "</b>");
        } else {
            items = new String[]{};
        }

        // set adapter
        if (index < 5) {
            if (listView.getAdapter() == null) {
                listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = convertView;

                        if (view == null) {
                            view = inflater.inflate(R.layout.item_weekly_activity_simple_view, null);
                        }

                        final String item = getItem(position);

                        // set views
                        final HtmlTextView tvText = view.findViewById(R.id.item_weekly_activity_simple_text);

                        // set values
                        tvText.setHtml(item);

                        return view;
                    }
                });
            } else {
                ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        } else {
            // if clo, use syllabus clo view
            final ListView closListView = view.findViewById(R.id.syllabus_clos_list_view);
            final FlexboxLayout legendContainer = view.findViewById(R.id.syllabus_clos_legend_container);

            // also compare to activity's clos
            final int[] clos = activity.getCloMap();

            // set legend
            Tags.setTagsInLayout(closListView, legendContainer, syllabus.getCloPoMap().getLegendString(clos), true);

            cloItemList = new ArrayList<>(Arrays.asList(syllabus.getCloPoMap().getItems(clos)));

            // set adapter when list is done
            if (closListView.getAdapter() == null) {
                ArrayAdapter<CloPoMap.Item> adapter = new CloPoMap.CloArrayAdapter(this, android.R.layout.simple_list_item_1, cloItemList);
                closListView.setAdapter(adapter);
            } else {
                ((ArrayAdapter) closListView.getAdapter()).notifyDataSetChanged();
            }
        }
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
            View rootView = getView();

            if (rootView == null) {
                int layoutId = index < 5 ? R.layout.fragment_weekly_activities : R.layout.fragment_syllabus_clos;
                rootView = inflater.inflate(layoutId, container, false);
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
            return 6;
        }
    }
}
