package com.code.feutech.forge.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.feutech.forge.R;
import com.code.feutech.forge.config.PreferencesList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Syllabus {
    private int id;
    private String json;
    private String version;
    private UnixWrapper createdAt;
    private UnixWrapper updatedAt;
    // content props
    private Course course;
    private String[] books;
    private String[] clos;
    private Curriculum curriculum;
    private CloPoMap cloPoMap;
    private WeeklyActivity[] weeklyActivities;
    private GradingSystem gradingSystem;
    private String[] references;
    // statements
    private String institutionVision;
    private String institutionMission;
    private String departmentVision;
    private String departmentMission;
    private String programEducationalObjectives;
    // users
    private User facultyInCharge;
    private User[] evaluatedBy;
    private User[] approvedBy;

    public Syllabus(JSONObject json) throws JSONException {
        this(json, null, null);
    }

    public Syllabus(JSONObject json, Curriculum latestCurriculum, GradingSystem latestGrading) throws JSONException {
        this.id = json.getInt("id");
        this.json = json.toString();
        this.version = json.getString("version");
        this.createdAt = new UnixWrapper(json.getLong("created_at"));
        this.updatedAt = new UnixWrapper(json.getLong("updated_at"));

        // set default values here, I guess?
        this.course = null;
        this.books = new String[]{};
        this.clos = new String[]{};
        this.curriculum = null;
        this.cloPoMap = null;
        this.weeklyActivities = null;
        this.gradingSystem = null;

        // parse content
        // don't bother if it doesn't parse
        try {
            // content is either String or json obj
            // in this case, it should always be a json obj
            // if not, just forget it
            JSONObject content = json.getJSONObject("content");

            // using the content, get the other data needed hehe
            this.course = new Course(content.getJSONObject("course"), true);

            // set books
            JSONArray books = content.getJSONArray("bookReferences");
            this.books = new String[books.length()];
            for (int i = 0; i < books.length(); i++) {
                this.books[i] = books.getString(i);
            }

            // set clos
            JSONArray clos = content.getJSONArray("courseLearningOutcomes");
            this.clos = new String[clos.length()];
            for (int i = 0; i < clos.length(); i++) {
                this.clos[i] = clos.getString(i);
            }

            // set curriculum
            this.curriculum = new Curriculum(content.getJSONObject("programOutcomes"), latestCurriculum);

            // set clo po map
            cloPoMap = new CloPoMap(this, content.getJSONArray("cloPoMap"), this.clos);

            // set also the weekly activities
            final JSONArray jsonWeeklyActivities = content.getJSONArray("weeklyActivities");
            this.weeklyActivities = new WeeklyActivity[jsonWeeklyActivities.length()];
            for (int i = 0; i < jsonWeeklyActivities.length(); i++) {
                this.weeklyActivities[i] = new WeeklyActivity(jsonWeeklyActivities.getJSONObject(i));
            }

            // set grading
            final JSONArray jsonGradingSystem = content.getJSONArray("gradingSystem");
            this.gradingSystem = new GradingSystem(jsonGradingSystem, latestGrading);

            // set book references
            final JSONArray jsonReferences = content.getJSONArray("bookReferences");
            this.references = new String[jsonReferences.length()];
            for (int i = 0; i < jsonReferences.length(); i++) {
                this.references[i] = jsonReferences.getString(i);
            }

            // set syllabus info
            this.institutionVision = content.getString("institutionVision");
            this.institutionMission = content.getString("institutionMission");
            this.departmentVision = content.getString("departmentVision");
            this.departmentMission = content.getString("departmentMission");
            this.programEducationalObjectives = content.getString("programEducationalObjectives");

            // set users
            this.facultyInCharge = new User(content.getJSONObject("facultyInCharge"));

            // create a map of Integer => User

            // set evaluatedBy
            HashMap<Integer, User> map;
            Iterator<Integer> iterator;

            map = this.getUsersMap(content.getJSONArray("evaluatedBy"));

            // map now contains all evaluated by users
            iterator = map.keySet().iterator();
            this.evaluatedBy = new User[map.size()];
            int i = 0;
            while (iterator.hasNext()) {
                this.evaluatedBy[i] = map.get(iterator.next());
                i++;
            }

            // set approvedBy
            map = this.getUsersMap(content.getJSONArray("approvedBy"));

            // map now contains all evaluated by users
            iterator = map.keySet().iterator();
            this.approvedBy = new User[map.size()];
            i = 0;
            while (iterator.hasNext()) {
                this.approvedBy[i] = map.get(iterator.next());
                i++;
            }
        } catch (Exception e) {
            Log.e("tagx", "Error: ", e);
        }
    }

    private HashMap<Integer, User> getUsersMap(JSONArray jsonArray) throws JSONException {
        final HashMap<Integer, User> map = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            final JSONArray users = jsonArray.getJSONArray(i);
            for (int j = 0; j < users.length(); j++) {
                // actual object
                final JSONObject jsonUser = users.getJSONObject(j);
                // has id, status, and user
                final int id = jsonUser.getInt("id");
                final User user = new User(jsonUser.getJSONObject("user"));

                // add it to map if no id yet
                if (!map.containsKey(id)) {
                    map.put(id, user);
                }
            }
        }
        return map;
    }

    public int getId() {
        return id;
    }

    public String getJSON() {
        return json;
    }

    public String getVersion() {
        return "v" + version;
    }

    public String getRawVersion() {
        return version;
    }

    public UnixWrapper getCreatedAt() {
        return createdAt;
    }

    public UnixWrapper getUpdatedAt() {
        return updatedAt;
    }

    public Course getCourse() {
        return course;
    }

    public String[] getBooks() {
        return books;
    }

    public String[] getClos() {
        return clos;
    }

    public Curriculum getCurriculum() {
        return curriculum;
    }

    public CloPoMap getCloPoMap() {
        return cloPoMap;
    }

    public WeeklyActivity[] getWeeklyActivities() {
        return weeklyActivities;
    }

    public GradingSystem getGradingSystem() {
        return gradingSystem;
    }

    public String[] getReferences() {
        return references;
    }

    public String getInstitutionVision() {
        return institutionVision;
    }

    public String getInstitutionMission() {
        return institutionMission;
    }

    public String getDepartmentVision() {
        return departmentVision;
    }

    public String getDepartmentMission() {
        return departmentMission;
    }

    public String getProgramEducationalObjectives() {
        return programEducationalObjectives;
    }

    public User getFacultyInCharge() {
        return facultyInCharge;
    }

    public User[] getEvaluatedBy() {
        return evaluatedBy;
    }

    public User[] getApprovedBy() {
        return approvedBy;
    }

    public double getTotalHours() {
        double total = 0;
        for (final WeeklyActivity a : weeklyActivities) {
            total += a.getNoOfHours();
        }
        return total;
    }

    public String getFormattedTotalHours() {
        return getFormattedDouble(this.getTotalHours());
    }

    public static String getFormattedDouble(double d) {
        final DecimalFormat df = new DecimalFormat("#.##");
        return df.format(d);
    }

    public static boolean isSyllabusOffline(Context context, Syllabus syllabus) {
        return isSyllabusOffline(context, syllabus.getId());
    }

    public static boolean isSyllabusOffline(Context context, int id) {
        // also get uid from local
        final int uid = User.getUserIdFromSharedPref(context);

        if (uid <= 0) {
            return false;
        }

        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_SYLLABI + "_" + uid, Context.MODE_PRIVATE);
        String res = preferences.getString(String.valueOf(id), null);
        return !(res == null || res.trim().isEmpty());
    }

    public static void setSyllabusToggle(Context context, Syllabus syllabus) {
        // if syllabus is null, do not proceed!
        if (syllabus == null) {
            return;
        }

        // also get uid from local
        final int uid = User.getUserIdFromSharedPref(context);

        if (uid <= 0) {
            return;
        }

        // append uid to pref
        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_SYLLABI + "_" + uid, Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = preferences.edit();
        final String sId = String.valueOf(syllabus.getId());
        // if syllabus exists, then remove it
        // else, add it
        if (isSyllabusOffline(context, syllabus)) {
            edit.remove(sId);
        } else {
            edit.putString(sId, syllabus.getJSON());
        }
        edit.apply();
    }

    public static Syllabus[] getOfflineSyllabi(Context context) throws JSONException {
        // get uid from local
        final int uid = User.getUserIdFromSharedPref(context);

        if (uid <= 0) {
            return null;
        }

        final SharedPreferences preferences = context.getSharedPreferences(PreferencesList.PREF_SYLLABI + "_" + uid, Context.MODE_PRIVATE);
        final Map<String, ?> map = preferences.getAll();

        final Syllabus[] syllabi = new Syllabus[map.size()];
        int i = 0;
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            final String s = entry.getValue().toString();
            syllabi[i] = new Syllabus(new JSONObject(s));
            i++;
        }

        return syllabi;
    }

    // adapter
    public static class SyllabusArrayAdapter extends ArrayAdapter<Syllabus> {
        private boolean showCourse;

        public SyllabusArrayAdapter(boolean showCourse, @NonNull Context context, int resource, @NonNull List<Syllabus> objects) {
            this(context, resource, objects);
            this.showCourse = showCourse;
        }

        public SyllabusArrayAdapter(@NonNull Context context, int resource, @NonNull List<Syllabus> objects) {
            super(context, resource, objects);
            this.showCourse = false;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_course_info_syllabus_view, null);
            }

            final Syllabus syllabus = getItem(position);

            // set components
            final TextView tvTitle = view.findViewById(R.id.item_course_info_syllabus_title);
            final TextView tvCourse = view.findViewById(R.id.item_course_info_syllabus_course);
            final TextView tvDatetime = view.findViewById(R.id.item_course_info_syllabus_datetime);
            final ImageView ivIcon = view.findViewById(R.id.item_course_info_syllabus_star);

            // set values

            // set other values
            tvTitle.setText(syllabus.getVersion());
            tvDatetime.setText(syllabus.getUpdatedAt().convert("MMMM dd, yyyy hh:ss a"));

            if (showCourse) {
                tvCourse.setText(syllabus.getCourse().getCode());
                tvCourse.setVisibility(View.VISIBLE);
            } else {
                tvCourse.setVisibility(View.GONE);
            }

            // check if this syllabus is saved offline
            ivIcon.setVisibility(Syllabus.isSyllabusOffline(view.getContext(), syllabus) ? View.VISIBLE : View.GONE);

            return view;
        }
    }

    public static class SyllabusUserArrayAdapter extends ArrayAdapter<User> {
        public SyllabusUserArrayAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_syllabus_user_view, null);
            }

            final User user = getItem(position);

            // set components
            final TextView tvTitle = view.findViewById(R.id.item_syllabus_user_title);
            final TextView tvSubtitle = view.findViewById(R.id.item_syllabus_user_subtitle);

            // set values

            // set other values
            tvTitle.setText(user.getName());

            if (user.getTitle() == null || user.getTitle().trim().isEmpty()) {
                tvSubtitle.setVisibility(View.GONE);
            } else {
                tvSubtitle.setVisibility(View.VISIBLE);
                tvSubtitle.setText(user.getTitle());
            }

            return view;
        }
    }
}
