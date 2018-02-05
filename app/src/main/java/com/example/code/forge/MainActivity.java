package com.example.code.forge;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private String f_name;
    private String m_name;
    private String l_name;


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
                        f_name = m_userSubObject .getString("fname");
                        m_name = m_userSubObject .getString("mname");
                        l_name = m_userSubObject .getString("lname");
                        //Pass to next activity

                        //Snackbar.make(btnLogin, "Replace with your own action", Snackbar.LENGTH_LONG).show();
                        nextActivity.putExtra("f_name", f_name);
                        nextActivity.putExtra("m_name", m_name);
                        nextActivity.putExtra("l_name", l_name);
                        saveAccountAccess();
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
        }

    }

    private void saveAccountAccess(){
        String username = tvUsername.getText().toString();
        String password = tvPassword.getText().toString();
        String firstName = f_name.toString();
        String middleName = m_name.toString();
        String lastName = l_name.toString();
        SharedPreferences preferences = getSharedPreferences("UsernameAndPassword",0);
        preferences.edit().putString("test",username);
        preferences.edit().putString("test",password);
        preferences.edit().putString("firstName",firstName);
        preferences.edit().putString("middleName", middleName);
        preferences.edit().putString("lastName", lastName);
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || CourseFragment.class.getName().equals(fragmentName)
                || SyllabiFragment.class.getName().equals(fragmentName)
                || SchedulesFragment.class.getName().equals(fragmentName)
                || TeachingPlanFragment.class.getName().equals(fragmentName);
    }

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
