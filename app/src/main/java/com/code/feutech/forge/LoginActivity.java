package com.code.feutech.forge;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.code.feutech.forge.config.PreferencesList;
import com.code.feutech.forge.config.TaskConfig;
import com.code.feutech.forge.items.User;
import com.code.feutech.forge.utils.TaskCreator;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements TaskCreator.TaskListener {

    private TextInputEditText txtUsername;
    private TextInputEditText txtPassword;
    private Button btnLogin;
    private TextInputLayout txtUsernameContainer;
    private TextInputLayout txtPasswordContainer;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.checkForUser()) {
            // do not continue below
            return;
        }

        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        txtUsername = findViewById(R.id.login_txt_username);
        txtPassword = findViewById(R.id.login_txt_password);
        btnLogin = findViewById(R.id.login_btn_login);
        txtUsernameContainer = findViewById(R.id.login_txt_username_container);
        txtPasswordContainer = findViewById(R.id.login_txt_password_container);
        progressLoading = findViewById(R.id.login_progress_bar);

        progressLoading.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLoading(true);
                TaskCreator.execute(view.getContext(), LoginActivity.this, "login", TaskConfig.LOGIN_URL);
            }
        });

        checkForLogOutMsg();
    }

    private void checkForLogOutMsg() {
        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesList.PREF_LOGIN, MODE_PRIVATE);
        boolean didLogOut = sharedPreferences.getBoolean(PreferencesList.PREF_DID_LOG_OUT, false);
        if (didLogOut) {
            Snackbar.make(btnLogin, R.string.msg_did_log_out, Snackbar.LENGTH_LONG).show();
        }
        // remove
        sharedPreferences.edit().remove(PreferencesList.PREF_DID_LOG_OUT).apply();
    }

    private void doLoading(boolean loading) {
        boolean enable = !loading;
        txtUsernameContainer.setEnabled(enable);
        txtPasswordContainer.setEnabled(enable);
        btnLogin.setEnabled(enable);
        progressLoading.setVisibility(enable ? View.GONE : View.VISIBLE);

        // if loading, remove errors
        if (loading) {
            btnLogin.setText(R.string.loading_text);
            txtUsernameContainer.setError(null);
            txtPasswordContainer.setError(null);
        } else {
            btnLogin.setText(R.string.login_btn_login);
        }
    }

    private boolean checkForUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesList.PREF_LOGIN, MODE_PRIVATE);
        int uid = sharedPreferences.getInt(PreferencesList.PREF_USER_ID, -1);

        // if no id set, do nothing
        if (uid == -1) {
            return false;
        }

        // get user
        // String prefUser = sharedPreferences.getString(PREF_USER_JSON, "");
        // JSONObject jsonUser = new JSONObject(prefUser);
        // final User user = new User(jsonUser);

        // go to next activity here
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    // task listener
    @Override
    public void onTaskRespond(String id, String json) throws Exception {
        JSONObject response = new JSONObject(json);
        boolean success = TaskCreator.isSuccessful(response);

        if (!success) {
            String error = response.getString("error");
            if (error != null && error.length() > 0) {
                txtUsernameContainer.setError("");
                txtPasswordContainer.setError(error);
                Snackbar.make(btnLogin, error, Snackbar.LENGTH_LONG).show();
            }
            doLoading(false);
            return;
        }

        // get user
        JSONObject jsonUser = response.getJSONObject("user");
        final User user = new User(jsonUser);

        // save user to sharedpref
        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesList.PREF_LOGIN, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PreferencesList.PREF_USER_ID, user.getId());
        editor.putString(PreferencesList.PREF_USER_JSON, jsonUser.toString());
        editor.putBoolean(PreferencesList.PREF_DID_LOG_IN, true);
        editor.apply();

        this.checkForUser();
    }

    @Override
    public void onTaskError(String id, Exception e) {
        Snackbar.make(btnLogin, R.string.error, Snackbar.LENGTH_LONG).show();
        doLoading(false);
    }

    @Override
    public ContentValues setRequestValues(String id, ContentValues contentValues) {
        final String username = txtUsername.getText().toString();
        final String password = txtPassword.getText().toString();
        contentValues.put("username", username);
        contentValues.put("password", password);
        return contentValues;
    }
}
