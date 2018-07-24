package com.code.feutech.forge.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.feutech.forge.R;
import com.google.android.flexbox.FlexboxLayout;

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

    public Course(JSONObject json) throws JSONException {
        this(json, false, false);
    }

    public Course(JSONObject json, boolean parseRelated) throws JSONException {
        this(json, parseRelated, false);
    }

    public Course(JSONObject json, boolean parseRelated, boolean deepParse) throws JSONException {
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
        if (parseRelated) {

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
            // first, remove all views from layoutTags
            layoutTags.removeAllViews();
            final String[] tags = course.getTags();
            if (tags.length > 0) {
                // then start adding the views hehe
                for (final String tag : tags) {
                    final TextView tvTag = new TextView(view.getContext());
                    tvTag.setText(tag);
                    tvTag.setBackgroundResource(R.drawable.drawable_chip);

                    FlexboxLayout.LayoutParams tvTagLayoutParams = (FlexboxLayout.LayoutParams) tvTag.getLayoutParams();
                    if (tvTagLayoutParams == null) {
                        tvTagLayoutParams = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                    }

                    tvTagLayoutParams.setMargins(8, 8,8,8);
                    tvTag.setLayoutParams(tvTagLayoutParams);

                    layoutTags.addView(tvTag);
                }
                layoutTags.setVisibility(View.VISIBLE);
            } else {
                layoutTags.setVisibility(View.GONE);
            }

            // set other values
            tvTitle.setText(course.getCode());
            tvSubtitle.setText(course.getTitle());

            return view;
        }
    }
}
