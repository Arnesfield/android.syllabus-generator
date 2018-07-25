package com.code.feutech.forge.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.code.feutech.forge.R;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Course {
    private int id;
    private int status;
    private UnixWrapper createdAt;
    private UnixWrapper updatedAt;
    private int unitsLec;
    private int unitsLab;
    private String code;
    private String title;
    private String description;
    private String objectives;
    private Tags tags;
    private String json;
    private Syllabus[] syllabi;
    private Course[] prerequisites;
    private Course[] corequisites;

    public Course(JSONObject json) throws JSONException {
        this(json, false, false);
    }

    public Course(JSONObject json, boolean withRelated) throws JSONException {
        this(json, withRelated, false);
    }

    public Course(JSONObject json, boolean withRelated, boolean withSyllabi) throws JSONException {
        this.json = json.toString();
        this.id = json.getInt("id");
        this.status = json.getInt("status");
        this.createdAt = new UnixWrapper(json.getLong("created_at"));
        this.updatedAt = new UnixWrapper(json.getLong("updated_at"));
        this.unitsLec = json.getInt("unitsLec");
        this.unitsLab = json.getInt("unitsLab");
        this.code = json.getString("code");
        this.title = json.getString("title");
        this.description = json.getString("description");
        this.objectives = json.getString("objectives");
        this.tags = new Tags(json, "tags");

        // remember to parse content
        if (withRelated) {
            // then, there is a prerequisites and corequisites prop
            JSONArray jsonPrerequisites = json.getJSONArray("prerequisites");
            this.prerequisites = new Course[jsonPrerequisites.length()];

            // loop on json array and set it
            for (int i = 0; i < jsonPrerequisites.length(); i++) {
                final Course course = new Course(jsonPrerequisites.getJSONObject(i));
                this.prerequisites[i] = course;
            }

            // then do this again for the corequisites prop
            JSONArray jsonCorequisites = json.getJSONArray("corequisites");
            this.corequisites = new Course[jsonCorequisites.length()];

            // loop on json array and set it
            for (int i = 0; i < jsonCorequisites.length(); i++) {
                final Course course = new Course(jsonCorequisites.getJSONObject(i));
                this.corequisites[i] = course;
            }
        } else {
            this.prerequisites = new Course[]{};
            this.corequisites = new Course[]{};
        }

        if (withSyllabi) {
            // then, there is a syllabi prop
            JSONArray jsonSyllabi = json.getJSONArray("syllabi");
            this.syllabi = new Syllabus[jsonSyllabi.length()];

            // loop on json array and set it
            for (int i = 0; i < jsonSyllabi.length(); i++) {
                final Syllabus syllabus = new Syllabus(jsonSyllabi.getJSONObject(i));
                this.syllabi[i] = syllabus;
            }
        } else {
            this.syllabi = new Syllabus[]{};
        }
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public UnixWrapper getCreatedAt() {
        return createdAt;
    }

    public UnixWrapper getUpdatedAt() {
        return updatedAt;
    }

    public int getUnitsLec() {
        return unitsLec;
    }

    public int getUnitsLab() {
        return unitsLab;
    }

    public String getUnitsText() {
        String res = "";
        if (unitsLec > 0) {
            res += unitsLec + " " + (unitsLec == 1 ? "Unit" : "Units") + " LEC";
            // insert / if unitsLab exists
            res += unitsLab > 0 ? " / " : "";
        }
        if (unitsLab > 0) {
            res += unitsLab + " " + (unitsLab == 1 ? "Unit" : "Units") + " LAB";
        }
        return res;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getObjectives() {
        return objectives;
    }

    public Tags getTagsObject() {
        return tags;
    }

    public String[] getTags() {
        return tags.getTags();
    }

    public String getJSON() {
        return json;
    }

    public Syllabus[] getSyllabi() {
        return syllabi;
    }

    public Course[] getPrerequisites() {
        return prerequisites;
    }

    public Course[] getCorequisites() {
        return corequisites;
    }

    public String[] getRelatedCoursesNames(Course[] courses) {
        // get code only
        String[] names = new String[courses.length];
        for (int i = 0; i < courses.length; i++) {
            names[i] = courses[i].getCode();
        }
        return names;
    }

    // static
    public static class CourseArrayAdapter extends ArrayAdapter<Course> {
        public CourseArrayAdapter(@NonNull Context context, int resource, @NonNull List<Course> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_course_view, null);
            }

            final Course course = getItem(position);

            // set components
            final TextView tvTitle = view.findViewById(R.id.item_course_title);
            final TextView tvSubtitle = view.findViewById(R.id.item_course_subtitle);
            final ConstraintLayout layoutColor = view.findViewById(R.id.item_course_color_container);
            final FlexboxLayout layoutTags = view.findViewById(R.id.item_course_tags_container);

            // set values

            // set color
            final int[] colors = { R.color.colorDA, R.color.colorAGD, R.color.colorWMA, R.color.colorSMBA };
            final String[] strTags = { "da", "agd", "wma", "smba" };

            // default
            layoutColor.setBackgroundColor(view.getResources().getColor(R.color.colorCourseDefault));
            // loop colors
            for (int i = 0; i < strTags.length; i++) {
                if (course.getTagsObject().hasTag(strTags[i])) {
                    layoutColor.setBackgroundColor(view.getResources().getColor(colors[i]));
                    break;
                }
            }

            // set tags
            Tags.setTagsInLayout(view, layoutTags, course.getTags(), false, 3);

            // set other values
            tvTitle.setText(course.getCode());
            tvSubtitle.setText(course.getTitle());

            return view;
        }
    }
}
