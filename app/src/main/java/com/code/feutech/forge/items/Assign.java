package com.code.feutech.forge.items;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Assign {
    private int id;
    private int status;
    private UnixWrapper createdAt;
    private UnixWrapper updatedAt;
    private User createdBy;

    // content
    private String remarks;
    private ArrayList<Levels> levelsList;

    // assigned
    private Assigned assigned;

    // course
    private Course course;

    public Assign(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.status = json.getInt("status");
        this.createdAt = new UnixWrapper(json.getLong("created_at"));
        this.updatedAt = new UnixWrapper(json.getLong("updated_at"));

        JSONObject jsonUser = json.getJSONObject("created_by");
        this.createdBy = new User(jsonUser);

        // parse content
        JSONObject content = json.getJSONObject("content");

        // set remarks
        this.remarks = content.getString("remarks");

        // set assigned
        JSONObject assigned = content.getJSONObject("assigned");
        this.assigned = new Assigned(assigned);

        // set course
        JSONObject jsonCourse = content.getJSONObject("course");
        this.course = new Course(jsonCourse);

        // set levels
        this.levelsList = new ArrayList<>();
        JSONArray jsonLevelsList = content.getJSONArray("levels");
        // loop through jsonLevels
        for (int i = 0; i < jsonLevelsList.length(); i++) {
            final Levels levels = new Levels();
            JSONArray jsonLevels = jsonLevelsList.getJSONArray(i);

            for (int j = 0; j < jsonLevels.length(); j++) {
                JSONObject jsonLevel = jsonLevels.getJSONObject(j);
                final Level level = new Level(jsonLevel);
                levels.add(level);
            }

            // add it to levelsList
            this.levelsList.add(levels);
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

    public User getCreatedBy() {
        return createdBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public ArrayList<Levels> getLevelsList() {
        return levelsList;
    }

    public Assigned getAssigned() {
        return assigned;
    }

    public Course getCourse() {
        return course;
    }

    // static
    public static class ItemAdapter extends ArrayAdapter<Assign> {
        public ItemAdapter(@NonNull Context context, int resource, @NonNull List<Assign> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_assign_view, null);
            }

            final Assign assign = getItem(position);

            // set components
            final TextView tvTitle = view.findViewById(R.id.item_assign_title);
            final TextView tvSubtitle = view.findViewById(R.id.item_assign_subtitle);
            final TextView tvDate = view.findViewById(R.id.item_assign_date_text);
            final TextView tvTime = view.findViewById(R.id.item_assign_time_text);
            final ImageView imgIcon = view.findViewById(R.id.item_assign_img_icon);

            // set values
            tvTitle.setText(assign.getCourse().getCode());
            tvSubtitle.setText(assign.getCourse().getTitle());
            // set date and time
            tvDate.setText(assign.getUpdatedAt().convert("MM/dd/YY"));
            tvTime.setText(assign.getUpdatedAt().convert("hh:ss a"));

            // set icon
            int imageResource = -1;
            if (assign.getStatus() == 0) {
                imageResource = R.drawable.ic_status_cancel;
            } else if (assign.getStatus() == 1) {
                imageResource = R.drawable.ic_status_check;
            } else if (assign.getStatus() == 2) {
                imageResource = R.drawable.ic_status_indeterminate;
            } else if (assign.getStatus() == 3) {
                imageResource = R.drawable.ic_status_block;
            }
            imgIcon.setImageResource(imageResource);

            return view;
        }
    }
}
