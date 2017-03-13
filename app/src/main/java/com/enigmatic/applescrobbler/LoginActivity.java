package com.enigmatic.applescrobbler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ag.lfm.Lfm;
import com.ag.lfm.LfmError;
import com.ag.lfm.Session;

public class LoginActivity extends Activity {

    private EditText username, password;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
            if (username.getText() != null && password.getText() != null) {
                Lfm.login(username.getText().toString(), password.getText().toString(), new Lfm.LfmCallback<Session>() {
                    @Override
                    public void onResult(Session result) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(LfmError error) {
                        Log.e("Error", "onError: " + error.toString());
                        Toast.makeText(getApplicationContext(), error.errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
