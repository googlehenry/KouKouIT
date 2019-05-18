package com.koukou.it.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.koukou.it.MainActivity;
import com.koukou.it.R;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class RegisterActivity extends BasicActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";
    private EditText usernameView;
    private EditText passwordView;
    private Button registerButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameView = findViewById(R.id.register_username);
        passwordView = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.register_progress);

        registerButton.setOnClickListener(this);
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
        if (!isUserNameValid(username) || !isPasswordValid(password)) {
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", username);
            obj.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String userJson = obj.toString();
        Log.d(TAG, "attemptRegister" + userJson);
        final RegisterActivity activity = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = RequestBody.create(JSON, userJson);
                Request request = new Request.Builder()
                        .url("http://192.168.174.1:8301/api/v1/user")
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    activity.onRegisterFinish(responseData, username);
                } catch (Exception e) {
                    activity.onCallException();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean isUserNameValid(String username) {
        if (TextUtils.isEmpty(username)) {
            usernameView.setError("username should not be empty!");
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (TextUtils.isEmpty(password)) {
            passwordView.setError("password should not be empty!");
            return false;
        } else if (password.length() < 6) {
            passwordView.setError("password is too short!");
            return false;
        }
        return true;
    }

    public void onRegisterFinish(String response, String username) {
        Message message = new Message();
        Log.d(TAG, "onRegisterFinish: " + response);
        if ("success".equals(response)) {
            storeDataToSharedPreference(username);
            message.what = REGISTER_SUCCEED;
            handler.sendMessage(message);
        } else {
            message.what = REGISTER_FAILED;
            handler.sendMessage(message);
        }
    }

    public void onCallException() {
        Message message = new Message();
        message.what = SERVER_CONNECTION_FAILURE;
        handler.sendMessage(message);
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER_SUCCEED:
                    hideProgressBar();
                    finish();
                    goToMainActivity();
                    break;
                case REGISTER_FAILED:
                    hideProgressBar();
                    registerFailed();
                    break;
                case SERVER_CONNECTION_FAILURE:
                    serverConnectFailed();
                default:
                    break;
            }
        }
    };

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void registerFailed() {
        Toast.makeText(this, "用户名或者密码不正确", Toast.LENGTH_SHORT).show();
    }

    private void storeDataToSharedPreference(String username) {
        editor = pref.edit();
        editor.putString("loginName", username);
        editor.apply();
        Log.d(TAG, "storeDataToSharedPreference: " + pref.getString("loginName", ""));
    }

    private void goToMainActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
