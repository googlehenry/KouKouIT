package com.koukou.it.activity;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.koukou.it.R;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends BasicActivity implements View.OnClickListener {
    private EditText usernameView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameView = findViewById(R.id.register_username);
        passwordView = findViewById(R.id.register_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                attemptRegister();
                break;
            default:
                break;
        }
    }

    private void attemptRegister() {
        final String username = usernameView.getText().toString();
        final String password = passwordView.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.174.1:8301/api/v1/user")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().toString();

                } catch (Exception e) {

                }
            }
        }).start();
    }

    public void onCallFinish(String response) {

    }
}
