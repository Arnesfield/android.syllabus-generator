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

import java.util.List;

public class Curriculum {
    private int id;
    private String label;
    private UnixWrapper updatedAt;
    private Item[] items;
    private Curriculum latestCurriculum;

    public Curriculum(JSONObject json) throws JSONException {
        this(json, null);
    }

    public Curriculum(JSONObject json, @Nullable Curriculum latestCurriculum) throws JSONException {
        this.id = json.getInt("id");
        this.label = json.getString("label");
        this.updatedAt = new UnixWrapper(json.getLong("updated_at"));
        this.latestCurriculum = latestCurriculum;

        // parse content
        JSONArray content = json.getJSONArray("content");
        this.items = new Item[content.length()];
        for (int i = 0; i < content.length(); i++) {
            this.items[i] = new Item(content.getJSONObject(i));
        }
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public boolean isLatest() {
        if (latestCurriculum == null) {
            return true;
        }
        return id == latestCurriculum.getId();
    }

    public UnixWrapper getUpdatedAt() {
        return updatedAt;
    }

    public Item[] getItems() {
        return items;
    }

    public Item get(int index) {
        return index > 0 || index <= items.length - 1 ? items[index] : null;
    }

    // curriculum item
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

        public String get() {
            return label + ". " + text;
        }
    }

    // adpater
    public static class CurriculumItemArrayAdapter extends ArrayAdapter<Curriculum.Item> {
        public CurriculumItemArrayAdapter(@NonNull Context context, int resource, @NonNull List<Curriculum.Item> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_label_text_view, null);
            }

            final Curriculum.Item item = getItem(position);

            // set components
            final TextView tvLabel = view.findViewById(R.id.item_label_text_label);
            final TextView tvText = view.findViewById(R.id.item_label_text_text);

            // set values
            tvLabel.setText(item.getLabel());
            tvText.setText(item.getText());

            return view;
        }
    }
}
