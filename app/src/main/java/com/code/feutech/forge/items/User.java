package com.code.feutech.forge.items;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.code.feutech.forge.config.PreferencesList;
import com.code.feutech.forge.config.TaskConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int id;
    private String fname;
    private String mname;
    private String lname;
    private String username;
    private String title;
    private String weight;
    private String imgSrc;
    private int[] auth;

    public User(JSONObject json) throws JSONException {
        this.id = json.getInt("id");
        this.fname = json.getString("fname");
        this.mname = json.getString("mname");
        this.lname = json.getString("lname");
        this.username = json.getString("username");
        this.title = json.getString("title");
        this.weight = json.getString("weight");
        this.imgSrc = json.getString("img_src");

        // set auth
        JSONArray auth;

        try {
            auth = json.getJSONArray("auth");
        } catch (Exception e) {
            String strAuth = json.getString("auth");
            auth = new JSONArray(strAuth);
        }

        this.auth = new int[auth.length()];
        for (int i = 0; i < auth.length(); i++) {
            this.auth[i] = auth.getInt(i);
        }
    }

    public int getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getMname() {
        return mname;
    }

    public String getLname() {
        return lname;
    }

    public String getName() {
        String name = fname + " ";
        name += mname != null && mname.length() > 0 ? mname + " " : "";
        name += lname;
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }

    public String getWeight() {
        return weight;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public int[] getAuth() {
        return auth;
    }

    public boolean hasAuth(int auth) {
        return this.hasAuth(new int[]{auth});
    }

    public boolean hasAuth(int[] auth) {
        // loop on auth param
        for (int a : auth) {
            // if a exists in the instance's auth, true
            for (int uAuth : this.auth) {
                if (a == uAuth) {
                    return true;
                }
            }
        }
        return false;
    }

    public static User getUserFromSharedPref(Activity activity) throws JSONException {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PreferencesList.PREF_LOGIN, Context.MODE_PRIVATE);
        String prefUser = sharedPreferences.getString(PreferencesList.PREF_USER_JSON, "");
        JSONObject jsonUser = new JSONObject(prefUser);
        return new User(jsonUser);
    }

    public void loadImage(Context context, ImageView imageView, TextView textView) {
        loadImage(context, imageView, textView, false);
    }

    public void loadImage(final Context context, final ImageView imageView, final TextView textView, boolean forceDefault) {
        if (forceDefault || imgSrc == null || imgSrc.trim().isEmpty()) {
            textView.setText(String.valueOf(fname.toUpperCase().charAt(0)));
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        } else {
            Glide
                    .with(context)
                    .load(TaskConfig.UPLOADED_IMAGES_URL + imgSrc)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // if load failed, force default
                            User.this.loadImage(context, imageView, textView, true);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
    }
}
