package com.example.code.forge;

import android.content.ContentValues;
import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.code.forge.config.TaskConfig;
import com.example.code.forge.utils.SuperTask;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SuperTask.TaskListener {

    //Declare variables
    private TextView tvUsername;
    private TextView tvPassword;
    private Button btnLogin;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set views
        btnLogin = findViewById(R.id.loginButton);
        tvUsername = findViewById(R.id.idNumber);
        tvPassword = findViewById(R.id.userPassword);

        // add listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = tvUsername.getText().toString();
                password = tvPassword.getText().toString();
                // call this on login button click
                SuperTask.execute(MainActivity.this,"login",TaskConfig.LOGIN_URL);
                SuperTask.execute(MainActivity.this,"courses",TaskConfig.COURSE_URL);
            }
        });
    }

    // implement superTask listener
    @Override
    public void onTaskRespond(String id, String json) {
        String dataDetails = json;
        switch (id){
            case "login":{
                // parse json string here for user details

                try {
                    JSONObject m_userObject = new JSONObject(dataDetails);
                    //JSONObject m_courseObject = new JSONObject(dataDetails);
                    boolean isTrue = m_userObject.getBoolean("success");
                    Intent nextActivity = new Intent(MainActivity.this, BaseActivity.class);

                    if (isTrue == true){
                        JSONObject m_userSubObject = m_userObject.getJSONObject("user");
                        /*JSONObject m_courseSubObject = m_courseObject.getJSONObject("courses");
                        Log.d("HEY",m_courseSubObject.toString());*/
                        String f_name = m_userSubObject .getString("fname");
                        String m_name = m_userSubObject .getString("mname");
                        String l_name = m_userSubObject .getString("lname");
                        String m_id = m_userSubObject .getString("id");
                        String m_username = m_userSubObject .getString("username");
                        String m_password = m_userSubObject .getString("password");
                        String m_status = m_userSubObject .getString("status");
                        String m_type = m_userSubObject .getString("type");
                        //Pass to next activity

                        //Snackbar.make(btnLogin, "Replace with your own action", Snackbar.LENGTH_LONG).show();
                        nextActivity.putExtra("f_name", f_name);
                        nextActivity.putExtra("m_name", m_name);
                        nextActivity.putExtra("l_name", l_name);
                        nextActivity.putExtra("m_id", m_id);
                        nextActivity.putExtra("m_username", m_username);
                        nextActivity.putExtra("m_password", m_password);
                        nextActivity.putExtra("m_status", m_status);
                        nextActivity.putExtra("m_type", m_type);
                        startActivity(nextActivity);
                        this.finish();
                    } else {

                        Snackbar.make(btnLogin,"Login Failed!",Snackbar.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "courses": {

            }


        }

    }
    /*protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || CourseFragment.class.getName().equals(fragmentName)
                || SyllabiFragment.class.getName().equals(fragmentName)
                /*|| NotificationPreferenceFragment.class.getName().equals(fragmentName)*/;
    //}

    @Override
    public ContentValues setRequestValues(String id, ContentValues contentValues) {
        // put values to contentValues
        // put(key, value)
        // check controllers for the correct keys
        // $this->input->post(key)
        contentValues.put("username", this.username);
        contentValues.put("password", this.password);
        contentValues.put("courses","");
        return contentValues;
    }
}
