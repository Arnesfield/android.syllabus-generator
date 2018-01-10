package com.example.code.forge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    //Declare variables
    TextView username;
    TextView password;
    String s_username;
    String s_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loginButton = findViewById(R.id.loginButton);
        username = findViewById(R.id.idNumber);
        password = findViewById(R.id.userPassword);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_username = username.getText().toString();
                s_password = password.getText().toString();
                if (!(s_username.equals("201510592") && s_password.equals("test"))){
                    Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Login success.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
