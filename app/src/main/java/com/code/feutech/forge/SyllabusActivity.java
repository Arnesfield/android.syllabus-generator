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
import com.code.feutech.forge.items.Course;
import com.code.feutech.forge.items.Curriculum;
import com.code.feutech.forge.items.GradingSystem;
import com.code.feutech.forge.items.Syllabus;
import com.code.feutech.forge.interfaces.OnLoadingListener;
import com.code.feutech.forge.items.Tags;
import com.code.feutech.forge.items.User;
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
    private boolean starDialog;
    private boolean isOffline;
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
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);

        // get data from extras
        final Bundle extras = getIntent().getExtras();
        try {
            syllabusId = extras.getInt("syllabusId", -1);
            if (syllabusId == -1) throw new Exception();
        } catch (Exception ignored) {
            finish();
            return;
        }

        // set star
        starDialog = extras.getBoolean("starDialog", false);

        // handle offline too
        final String syllabusStr = extras.getString("syllabus", null);
        this.isOffline = syllabusStr != null;

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

        // handle offline after stuff has been set above
        if (this.isOffline) {
            try {
                this.syllabus = new Syllabus(new JSONObject(syllabusStr));

                // set title
                getSupportActionBar().setTitle(syllabus.getVersion());
                getSupportActionBar().setSubtitle(syllabus.getCourse().getCode());

                onHasData();
            } catch (Exception e) {
                Log.e("tagx", "Error: ", e);
            }
        }

        // do loading if no syllabus
        fetch(noDataBtnRefresh);
    }

    private void fetch(View view) {
        // execute here
        if (isOffline) {
            onHasData();
        } else {
            onLoading();
        }
        TaskCreator.execute(view.getContext(), this, "syllabus", TaskConfig.SYLLABI_URL);
    }

    private void setMenu(boolean withMessage) {
        if (menu == null || syllabus == null) {
            return;
        }
        // check prefs syllabi if this syllabus is bookmarked
        boolean isOffline = Syllabus.isSyllabusOffline(this, syllabus);

        final MenuItem item = menu.getItem(0);
        item.setIcon(isOffline ? R.drawable.ic_star : R.drawable.ic_star_border);

        if (withMessage) {
            final int strId = isOffline ? R.string.msg_saved_offline : R.string.msg_unsaved_offline;
            Snackbar.make(findViewById(R.id.syllabus_root), strId, Snackbar.LENGTH_LONG).show();
        }
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
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_syllabus, menu);
        setMenu(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_star && syllabus != null) {
            // set this syllabus hehe
            if (starDialog && Syllabus.isSyllabusOffline(this, syllabus)) {
                // show dialog if syllabus is offline and starDialog is true
                new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_title_unsave)
                    .setMessage(R.string.msg_dialog_unsave)
                    .setPositiveButton(R.string.dialog_unsave, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Syllabus.setSyllabusToggle(SyllabusActivity.this, syllabus);
                            dialogInterface.dismiss();
                            setMenu(true);
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
            } else {
                Syllabus.setSyllabusToggle(this, syllabus);
                setMenu(true);
            }
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
                intent.putExtra("syllabus", syllabus.getJSON());
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

    private void setDataCourse(View view, int index, int actualPosition, boolean force) throws Exception {
        if (!(force || index == actualPosition)) {
            return;
        }

        final Course course = syllabus.getCourse();

        // set view components
        final View mainView = view.findViewById(R.id.course_info_info_main);
        final View codeView = view.findViewById(R.id.course_info_info_code);
        final View unitsView = view.findViewById(R.id.course_info_info_units);
        final View descriptionView = view.findViewById(R.id.course_info_info_description);
        final View prerequisitesView = view.findViewById(R.id.course_info_info_prerequisites);
        final View corequisitesView = view.findViewById(R.id.course_info_info_corequisites);
        final View tagsView = view.findViewById(R.id.course_info_info_tags);
        // dividers
        final View divider0 = view.findViewById(R.id.course_info_info_divider_0);
        final View divider1 = view.findViewById(R.id.course_info_info_divider_1);
        final View divider2 = view.findViewById(R.id.course_info_info_divider_2);
        final View divider3 = view.findViewById(R.id.course_info_info_divider_3);
        final View divider4 = view.findViewById(R.id.course_info_info_divider_4);
        final View divider5 = view.findViewById(R.id.course_info_info_divider_5);

        // reveal codeView and divider0
        codeView.setVisibility(View.VISIBLE);
        divider0.setVisibility(View.VISIBLE);

        // set here
        CourseInfoActivity.setCourseInfoData(mainView, divider1, R.string.course_info_info_main, course.getTitle());
        CourseInfoActivity.setCourseInfoData(codeView, divider0, R.string.course_info_info_code, course.getCode());
        CourseInfoActivity.setCourseInfoData(unitsView, divider2, R.string.course_info_info_units, course.getUnitsText());
        CourseInfoActivity.setCourseInfoData(descriptionView, divider3, R.string.course_info_info_description, course.getDescription());
        CourseInfoActivity.setCourseInfoData(
                prerequisitesView,
                divider4,
                R.string.course_info_info_prerequisites,
                null, course.getRelatedCoursesNames(course.getPrerequisites()),
                true);
        CourseInfoActivity.setCourseInfoData(
                corequisitesView,
                divider5,
                R.string.course_info_info_corequisites,
                null, course.getRelatedCoursesNames(course.getCorequisites()),
                true);
        CourseInfoActivity.setCourseInfoData(tagsView, divider5, R.string.course_info_info_tags, null, course.getTags(), false);
    }

    private void setDataStatements(View view, int index, int actualPosition, boolean force) throws Exception {
        if (!(force || index == actualPosition)) {
            return;
        }

        final String institutionVision = syllabus.getInstitutionVision();
        final String institutionMission = syllabus.getInstitutionMission();
        final String departmentVision = syllabus.getDepartmentVision();
        final String departmentMission = syllabus.getDepartmentMission();

        // set views
        final View iv = view.findViewById(R.id.syllabus_statements_institution_vision);
        final View im = view.findViewById(R.id.syllabus_statements_institution_mission);
        final View dv = view.findViewById(R.id.syllabus_statements_department_vision);
        final View dm = view.findViewById(R.id.syllabus_statements_department_mission);

        final View div1 = view.findViewById(R.id.syllabus_statements_divider_1);
        final View div2 = view.findViewById(R.id.syllabus_statements_divider_2);
        final View div3 = view.findViewById(R.id.syllabus_statements_divider_3);

        CourseInfoActivity.setCourseInfoData(iv, div1, R.string.syllabus_statements_institution_vision, institutionVision);
        CourseInfoActivity.setCourseInfoData(im, div2, R.string.syllabus_statements_institution_mission, institutionMission);
        CourseInfoActivity.setCourseInfoData(dv, div3, R.string.syllabus_statements_department_vision, departmentVision);
        CourseInfoActivity.setCourseInfoData(dm, div3, R.string.syllabus_statements_department_mission, departmentMission);
    }

    private void setDataSyllabus(View view, int index, int actualPosition, boolean force) throws Exception {
        if (!(force || index == actualPosition)) {
            return;
        }

        // get arrays lol
        final User[] facultyInCharge = { syllabus.getFacultyInCharge() };
        final User[] evaluatedBy = syllabus.getEvaluatedBy();
        final User[] approvedBy = syllabus.getApprovedBy();

        final View facultyInChargeView = view.findViewById(R.id.syllabus_syllabus_faculty_in_charge);
        final View evaluatedByView = view.findViewById(R.id.syllabus_syllabus_evaluated_by);
        final View approvedByView = view.findViewById(R.id.syllabus_syllabus_approved_by);

        // also get texts
        final HtmlTextView tvVersion = view.findViewById(R.id.syllabus_syllabus_version_text);
        final HtmlTextView tvDate = view.findViewById(R.id.syllabus_syllabus_date_text);

        // set values
        tvVersion.setHtml("Version: <b>" + syllabus.getRawVersion() + "</b>");
        tvDate.setHtml("Date modified: <b>" + syllabus.getUpdatedAt().convert("MMMM dd, yyyy hh:ss a") + "</b>");

        // set list view here
        this.setDataSyllabusUser(facultyInChargeView, facultyInCharge, R.string.syllabus_syllabus_faculty_in_charge);
        this.setDataSyllabusUser(evaluatedByView, evaluatedBy, R.string.syllabus_syllabus_evaluated_by);
        this.setDataSyllabusUser(approvedByView, approvedBy, R.string.syllabus_syllabus_approved_by);
    }

    private void setDataSyllabusUser(View main, User[] users, int title) {
        final TextView tvTitle = main.findViewById(R.id.component_list_view_with_title_text);
        final ListView listView = main.findViewById(R.id.component_list_view_with_title_list_view);

        tvTitle.setText(title);

        final ArrayList<User> list = new ArrayList<>(Arrays.asList(users));
        // set adapter when list is done
        if (listView.getAdapter() == null) {
            ArrayAdapter<User> adapter = new Syllabus.SyllabusUserArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);
        } else {
            ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

    // TabbedActivityListener
    @Override
    public void setData(View view, int index, boolean force) throws Exception {
        if (syllabus == null || view == null) {
            return;
        }

        setMenu(false);

        setDataActivities(view, index, 0, force);
        setDataGrading(view, index, 1, force);
        setDataCurriculum(view, index, 2, force);
        setDataClos(view, index, 3, force);
        setDataReferences(view, index, 4, force);
        setDataCourse(view, index, 5, force);
        setDataStatements(view, index, 6, force);
        setDataSyllabus(view, index, 7, force);
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
        // do not do this if offline
        if (!isOffline) {
            Snackbar.make(noDataText, R.string.error, Snackbar.LENGTH_LONG).show();
            onNoData(R.string.error_frown);
        }
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
                R.layout.fragment_syllabus_references,
                R.layout.fragment_course_info_info,
                R.layout.fragment_syllabus_statements,
                R.layout.fragment_syllabus_syllabus
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
