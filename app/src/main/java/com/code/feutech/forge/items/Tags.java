package com.code.feutech.forge.items;

import android.view.View;
import android.widget.TextView;

import com.code.feutech.forge.R;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tags {
    private String[] tags;

    public Tags(JSONObject json, String field) {
        // check first if field is json array or string
        try {
            JSONArray jsonArray = json.getJSONArray(field);
            // using the array, loop and set the strings
            this.tags = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                // note: consider non strings lol
                this.tags[i] = jsonArray.getString(i);
            }
        } catch (JSONException e) {
            // if no array, then tags should be empty
            this.tags = new String[]{};
        }
    }

    public String[] getTags() {
        return tags;
    }

    public boolean hasTag(String tag) {
        for (final String s : this.tags) {
            if (s.toLowerCase().equals(tag.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static void setTagsInLayout(View view, FlexboxLayout layoutTags, String[] tags, boolean rect) {
        setTagsInLayout(view, layoutTags, tags, rect, -1);
    }

    public static void setTagsInLayout(View view, FlexboxLayout layoutTags, String[] tags, boolean rect, int length) {
        // first, remove all views from layoutTags
        layoutTags.removeAllViews();
        if (tags != null && tags.length > 0) {
            // use either arg length or tags length
            // choose tag length if length is negative, or if tag length is less than arg length
            length = length < 0 ? tags.length : length;
            length = tags.length < length ? tags.length : length;

            // then start adding the views hehe
            for (int i = 0; i < length; i++) {
                final String tag = tags[i];
                final TextView tvTag = new TextView(view.getContext());
                tvTag.setText(tag);
                tvTag.setBackgroundResource(rect ? R.drawable.drawable_chip_rect : R.drawable.drawable_chip);

                FlexboxLayout.LayoutParams tvTagLayoutParams = (FlexboxLayout.LayoutParams) tvTag.getLayoutParams();
                if (tvTagLayoutParams == null) {
                    tvTagLayoutParams = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                }

                tvTagLayoutParams.setMargins(8, 8, 8, 8);
                tvTag.setLayoutParams(tvTagLayoutParams);

                layoutTags.addView(tvTag);
            }
            layoutTags.setVisibility(View.VISIBLE);
        } else {
            layoutTags.setVisibility(View.GONE);
        }
    }
}
