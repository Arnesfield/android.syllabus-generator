package com.code.feutech.forge.items;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code.feutech.forge.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CloPoMap {
    private Item[] items;

    public CloPoMap(Syllabus syllabus, JSONArray json, String[] clos) throws JSONException {
        // json array maps to the clo itself
        this.items = new Item[json.length()];
        for (int i = 0; i < json.length(); i++) {
            this.items[i] = new Item(syllabus, json.getJSONObject(i), clos[i]);
        }
    }

    public Item[] getItems() {
        return items;
    }

    public Item get(int index) {
        return index > 0 || index <= items.length - 1 ? items[index] : null;
    }

    public ArrayList<Item.Relationship> getLegend() {
        final ArrayList<Item.Relationship> arrListRelationships = new ArrayList<>();
        // loop on all items
        for (int i = 0; i < this.getItems().length; i++) {
            final Item item = this.get(i);
            // loop on all relationships and add them to arraylist if unique
            for (int j = 0; j < item.getRelationships().length; j++) {
                final Item.Relationship rel = item.getRelationships()[j];

                // if no size yet, than add this hehe
                if (arrListRelationships.size() == 0) {
                    arrListRelationships.add(rel);
                } else {
                    // check if relationship exists
                    boolean alreadyExists = false;
                    for (int k = 0; k < arrListRelationships.size(); k++) {
                        final Item.Relationship r = arrListRelationships.get(k);

                        // if exists, then do not include this || skip
                        if (
                            r.getValue().equals(rel.getValue()) &&
                            r.getDesc().equals(rel.getDesc())
                        ) {
                            alreadyExists = true;
                            break;
                        }
                    }

                    if (!alreadyExists) {
                        arrListRelationships.add(rel);
                    }
                }
            }
        }

        return arrListRelationships;
    }

    public String[] getLegendString() {
        final ArrayList<Item.Relationship> legend = this.getLegend();
        final String[] res = new String[legend.size()];

        for (int i = 0; i < legend.size(); i++) {
            final Item.Relationship r = legend.get(i);
            res[i] = r.getValue() + " — " + r.getDesc();
        }

        return res;
    }

    // item
    public static class Item {
        private Syllabus syllabus;
        private String clo;
        private Relationship[] relationships;

        public Item(Syllabus syllabus, JSONObject json, String clo) throws JSONException {
            // I consist of weird props lol
            this.syllabus = syllabus;
            this.clo = clo;
            this.relationships = new Relationship[json.length()];

            Iterator<String> keys = json.keys();
            int i = 0;
            while (keys.hasNext()) {
                String key = keys.next();
                this.relationships[i] = new Relationship(json, key);
                i++;
            }
        }

        public Syllabus getSyllabus() {
            return syllabus;
        }

        public String getClo() {
            return clo;
        }

        public Relationship[] getRelationships() {
            return relationships;
        }

        // another sub item lol
        public static class Relationship {
            private String key;
            private String value;
            private String desc;

            public Relationship(String value, String desc) {
                this.value = value;
                this.desc = desc;
            }

            public Relationship(JSONObject json, String key) throws JSONException {
                final JSONObject result = json.getJSONObject(key);

                this.key = key;
                this.value = result.getString("symbol");
                this.desc = result.getString("text");
            }

            public String getKey() {
                return key;
            }

            public String getValue() {
                return value;
            }

            public String getDesc() {
                return desc;
            }
        }
    }

    // adapter
    public static class CloArrayAdapter extends ArrayAdapter<CloPoMap.Item> {
        public CloArrayAdapter(@NonNull Context context, int resource, @NonNull List<CloPoMap.Item> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = convertView;

            if (view == null) {
                view = inflater.inflate(R.layout.item_clo_view, null);
            }

            final CloPoMap.Item item = getItem(position);
            final Syllabus syllabus = item.getSyllabus();

            // set components
            final TextView tvLabel = view.findViewById(R.id.item_label_text_label);
            final HtmlTextView tvClo = view.findViewById(R.id.item_label_text_text);
            final LinearLayout linearLayout = view.findViewById(R.id.item_clo_linear_layout);

            // set values
            tvLabel.setText(String.valueOf(position + 1));
            tvClo.setHtml(item.getClo());

            // using the linear layout, add views in there
            // loop on curriculum
            // but first! clear children
            linearLayout.removeAllViews();

            final Curriculum.Item[] curriculum = syllabus.getCurriculum().getItems();
            for (int i = 0; i < curriculum.length; i++) {
                // components
                final View subCloView = inflater.inflate(R.layout.item_sub_clo_view, null);
                final TextView subCloLabel = subCloView.findViewById(R.id.item_sub_clo_label);
                final TextView subCloMark = subCloView.findViewById(R.id.item_sub_clo_mark);

                // values
                final Curriculum.Item curriculumItem = curriculum[i];

                // set current curriculum label
                final String curriculumLabel = curriculumItem.getLabel();
                subCloLabel.setText(curriculumLabel);

                // check the label
                final Item.Relationship[] relationships = item.getRelationships();
                for (int j = 0; j < relationships.length; j++) {
                    final Item.Relationship r = relationships[j];

                    // if relation exists, then add a text to cloMark!
                    if (curriculumLabel.equals(r.getKey())) {
                        subCloMark.setText(r.getValue());
                        break;
                    } else {
                        subCloMark.setText("");
                    }
                }

                // set params if you want :P
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) subCloLabel.getLayoutParams();

                if (layoutParams == null) {
                    layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                layoutParams.gravity = Gravity.CENTER;
                layoutParams.weight = 1;
                layoutParams.setMargins(0, 8, 0, 8);
                subCloView.setLayoutParams(layoutParams);

                // after all that, add dat subCloView!
                linearLayout.addView(subCloView);

                // if last iteration, remove the dividerVertical
                final View dividerVertical = subCloView.findViewById(R.id.item_sub_clo_divider_vertical);
                dividerVertical.setVisibility(i == curriculum.length - 1 ? View.GONE : View.VISIBLE);
            }


            return view;
        }
    }
}
