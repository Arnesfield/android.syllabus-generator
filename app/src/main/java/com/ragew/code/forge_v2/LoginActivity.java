package com.ragew.code.forge_v2;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ragew.code.forge_v2.Config.TaskConfig;
import com.ragew.code.forge_v2.Utils.SuperTask;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity implements SuperTask.TaskListener{

    /*
    * Views
    * Place your TextViews or image views here
     */

    //Static variables for shared preferences
    private static final String LOGIN_PREF = "login_pref";
    private static final String LOGIN_ID = "login_id";
    private Button login_button;

    /*
    * Variables
    * Declare your variables here
     */
    private TextView username_view;
    private TextView password_view;
    private String username;
    private String password;
    //User details
    private String fname;
    private String lname;
    //Alert Dialog
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Alert Dialog
        //Initialize the Dialog here
        progressDialog = new SpotsDialog(LoginActivity.this,R.style.Loader);

        /*
        * Find Views
        * Locate your views here
         */
        login_button = findViewById(R.id.login);
        username_view = findViewById(R.id.username);
        password_view = findViewById(R.id.password);

        /*
        * Set OnClickListener
        * login_button onclick listener
        */

        //Login button listeners
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = username_view.getText().toString();
                password = password_view.getText().toString();
                //Execute the SuperTask
                //SuperTask.execute(ActivityName.this,"id",TasConfig.URL);
                SuperTask.execute(LoginActivity.this,"login", TaskConfig.LOGIN_URL);
                SuperTask.execute(LoginActivity.this,"courses", TaskConfig.COURSE_URL);
                SuperTask.execute(LoginActivity.this, "assign", TaskConfig.ASSIGN_URL);
            }
        });

        //Shared Preferences

        SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE);
        int loggedOut = getIntent().getIntExtra("logout", -1);
        int loginID = sharedPreferences.getInt(LOGIN_ID,-1);

        //If user is logged out
        if (loggedOut != -1){
            //Edit shared preferences
            SharedPreferences.Editor editor = sharedPreferences .edit();
            editor.putInt(LOGIN_ID, -1);
            editor.apply();

            //Toast logout message
            Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_LONG).show();
        }

        if (loginID != -1 && loggedOut == -1){
            successfulLogin(loginID,fname,lname);
        }
    }

    private void successfulLogin(int uid, String fname, String lname){
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("uid",uid);
        intent.putExtra("fname",fname);
        intent.putExtra("lname",lname);

        //Here, after the login button has been pressed with the right credentials
        //The dialog will display and persist until the next activity is loaded
        progressDialog.show();


        startActivity(intent);
        finish();
    }


    @Override
    public void onTaskRespond(String id, String json) {
        //Get the JSON Array
        String retrievedData = json;

        //Separate the operations using IDs
        switch (id){
            case "login":{
                //Parse the JSON String to get the details
                try {
                    JSONObject jsonObject = new JSONObject(retrievedData);
                    boolean isValidAccount = jsonObject.getBoolean("success");

                    //If the account is valid, get the sub values of the json array
                    if (isValidAccount){
                     //Get the JSON Object
                        JSONObject userDetails = jsonObject.getJSONObject("user");
                        fname = userDetails.getString("fname");
                        lname = userDetails.getString("lname");
                        int uid = userDetails.getInt("id");

                        //Save id to shared pref
                        SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(LOGIN_ID,uid);
                        editor.apply();
                        //successful
                        successfulLogin(uid, fname, lname);

                        //End of shared preferences
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public ContentValues setRequestValues(String id, ContentValues contentValues){
        // put values to contentValues
        // put(key, value)
        // check controllers for the correct keys
        // $this->input->post(key)
        contentValues.put("username",this.username);
        contentValues.put("password",this.password);
        contentValues.put("courses","");
        return contentValues;
    }
}
