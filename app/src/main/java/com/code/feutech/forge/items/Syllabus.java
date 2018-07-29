package com.code.feutech.forge.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.code.feutech.forge.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

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

    public Syllabus(JSONObject json) throws JSONException {
        this(json, null);
    }

    public Syllabus(JSONObject json, Curriculum latestCurriculum) throws JSONException {
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

        // parse content
        // don't bother if it doesn't parse
        try {
            // content is either String or json obj
            // in this case, it should always be a json obj
            // if not, just forget it
            JSONObject content = json.getJSONObject("content");

            // using the content, get the other data needed hehe
            this.course = new Course(content.getJSONObject("course"));

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
        } catch (Exception e) {
            Log.e("tagx", "Error: ", e);
        }
    }

    public int getId() {
        return id;
    }

    public String getJson() {
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

    // static
    public static class SyllabusArrayAdapter extends ArrayAdapter<Syllabus> {
        public SyllabusArrayAdapter(@NonNull Context context, int resource, @NonNull List<Syllabus> objects) {
            super(context, resource, objects);
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
            final TextView tvSubtitle = view.findViewById(R.id.item_course_info_syllabus_subtitle);

            // set values

            // set other values
            tvTitle.setText(syllabus.getVersion());
            tvSubtitle.setText(syllabus.getUpdatedAt().convert("MM/dd/YY hh:ss a"));

            return view;
        }
    }
}
