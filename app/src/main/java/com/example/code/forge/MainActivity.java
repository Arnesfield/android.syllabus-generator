package com.example.code.forge;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.code.forge.utils.SuperTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

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
                SuperTask.execute(MainActivity.this);
            }
        });
    }

    // implement superTask listener
    @Override
    public void onTaskRespond(String json) {
        // parse json string here for user details
        Log.d("Success?: ", json);
        String userDetails = json;
        try {
            JSONObject m_object = new JSONObject(userDetails);
            boolean isTrue = m_object.getBoolean("success");
            Intent nextActivity = new Intent(MainActivity.this, SubActivity.class);
            if (isTrue == true){
                JSONObject uniObject = m_object.getJSONObject("user");
                String f_name = uniObject.getString("fname");
                String m_name = uniObject.getString("mname");
                String l_name = uniObject.getString("lname");
                String m_id = uniObject.getString("id");
                String m_username = uniObject.getString("username");
                String m_password = uniObject.getString("password");
                String m_status = uniObject.getString("status");
                String m_type = uniObject.getString("type");
                //Pass to next activity
                Toast.makeText(this,"Login Successful!", Toast.LENGTH_LONG).show();
                nextActivity.putExtra("f_name", f_name);
                nextActivity.putExtra("m_name", m_name);
                nextActivity.putExtra("l_name", l_name);
                nextActivity.putExtra("m_id", m_id);
                nextActivity.putExtra("m_username", m_username);
                nextActivity.putExtra("m_password", m_password);
                nextActivity.putExtra("m_status", m_status);
                nextActivity.putExtra("m_type", m_type);
                startActivity(nextActivity);
            } else {
                Toast.makeText(this,"Login Failed.", Toast.LENGTH_LONG).show();
            }
            // parse json string for courses
            String courseDetails = json;
            Log.d("These are the courses: ", "Course list: ");
            JSONObject m_courseObject = new JSONObject(courseDetails);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ContentValues setRequestValues(ContentValues contentValues) {
        // put values to contentValues
        // put(key, value)
        // check controllers for the correct keys
        // $this->input->post(key)
        contentValues.put("username", this.username);
        contentValues.put("password", this.password);
        return contentValues;
    }
}
