package com.code.feutech.forge.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.code.feutech.forge.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

public class GradingSystem {
    private String json;
    private Item[] items;
    private GradingSystem latestGrading;

    public GradingSystem(JSONArray json) throws JSONException {
        this(json, null);
    }

    public GradingSystem(JSONArray json, @Nullable GradingSystem latestGrading) throws JSONException {
        this.json = json.toString();
        this.latestGrading = latestGrading;
        this.items = new Item[json.length()];
        for (int i = 0; i < json.length(); i++) {
            this.items[i] = new Item(json.getJSONObject(i));
        }
    }

    public String getJSON() {
        return json;
    }

    public Item[] getItems() {
        return items;
    }

    public boolean isLatest() {
        if (latestGrading == null) {
            return true;
        }
        return json.equals(latestGrading.getJSON());
    }

    // item
    public static class Item {
        private String label;
        private String text;

        public Item(JSONObject json) throws JSONException {
            this.label = json.getString("label");
            this.text = json.getString("text");
        }

        public String getLabel() {
            return label;
        }

        public String getText() {
            return text;
        }
    }

    // adapter
    public static class GradingSystemItemArrayAdapter extends ArrayAdapter<GradingSystem.Item> {
        public GradingSystemItemArrayAdapter(@NonNull Context context, int resource, @NonNull List<GradingSystem.Item> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_syllabus_grading_view, null);
            }

            final GradingSystem.Item item = getItem(position);

            // set components
            final TextView tvTitle = view.findViewById(R.id.item_syllabus_grading_title);
            final HtmlTextView tvSubtitle = view.findViewById(R.id.item_syllabus_grading_subtitle);

            // convert to html
            // set values
            tvTitle.setText(item.getLabel());
            tvSubtitle.setHtml(item.getText());

            return view;
        }
    }
}
