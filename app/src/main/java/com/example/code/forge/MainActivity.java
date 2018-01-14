package com.example.code.forge;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.code.forge.utils.SuperTask;

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

                if (!(username.equals("201510592") && password.equals("test"))){
                    Toast.makeText(MainActivity.this, "Login failed.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Login success.", Toast.LENGTH_LONG).show();
                }

                // call this on login button click
                SuperTask.execute(MainActivity.this);
            }
        });
    }

    // implement superTask listener
    @Override
    public void onTaskRespond(String json) {
        // parse json string here
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
