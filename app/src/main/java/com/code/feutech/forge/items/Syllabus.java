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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Syllabus {
    private int id;
    private String version;
    private UnixWrapper createdAt;
    private UnixWrapper updatedAt;

    public Syllabus(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.version = json.getString("version");
        this.createdAt = new UnixWrapper(json.getLong("created_at"));
        this.updatedAt = new UnixWrapper(json.getLong("updated_at"));

        // parse content
        // don't bother if it doesn't parse
        try {
            // content is either String or json obj
            // in this case, it should always be a json obj
            // if not, just forget it
            JSONObject content = json.getJSONObject("content");

            // using the content, get the other data needed hehe
        } catch (Exception e) {
            // set default values here, I guess?
        }
    }

    public int getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public UnixWrapper getCreatedAt() {
        return createdAt;
    }

    public UnixWrapper getUpdatedAt() {
        return updatedAt;
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
            tvTitle.setText("v" + syllabus.getVersion());
            tvSubtitle.setText(syllabus.getUpdatedAt().convert("MM/dd/YY hh:ss a"));

            return view;
        }
    }
}
