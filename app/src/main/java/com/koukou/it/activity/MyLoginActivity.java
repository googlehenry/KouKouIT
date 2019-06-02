package com.koukou.it.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koukou.it.MainActivity;
import com.koukou.it.R;
import com.koukou.it.util.Constants;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyLoginActivity extends BasicActivity implements View.OnClickListener {
    private static final String TAG = "MyLoginActivity";

    //UI
    private AutoCompleteTextView usernameView;
    private TextView passwordView;
    private Button signInButton;
    private CheckBox remeberPass;
    private TextView registerText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);
        remeberPass = findViewById(R.id.remember_pass);
        registerText = findViewById(R.id.register_text);
        progressBar = findViewById(R.id.login_progress);
        signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(this);
        registerText.setOnClickListener(this);

        setStoredToViewIfRemember();
    }

    private void setStoredToViewIfRemember() {
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String username = pref.getString("username", "");
            String password = pref.getString("password", "");
            usernameView.setText(username);
            passwordView.setText(password);
            remeberPass.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                attemptLogin();
                break;
            case R.id.register_text:
                goToRegister();
            default:
                break;
        }
    }

    private void attemptLogin() {
        usernameView.setError(null);
        passwordView.setError(null);

        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        if (isUserNameValid(username) && isPasswordValid(password)) {
            isUserExisted(username, password, this);
        }
    }

    private void goToRegister() {
        Log.d(TAG, "goToRegister");
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
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

    private void isUserExisted(final String username, final String password, final MyLoginActivity activity) {
        Log.d(TAG, "check if user is existed in server. username: " + username + " password: " + password);
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .build();
                String url = Constants.USER_SERVER_PREFIX + "api/v1/login";
                Log.d(TAG, "url: " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d(TAG, "responseData: " + responseData);
                    activity.onCallFinish(responseData);
                } catch (Exception e) {
                    activity.onCallException();
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void onCallFinish(String response) {
        String password = passwordView.getText().toString();
        try {

            Message message = new Message();
            if (!"".equals(response)) {
                JSONObject user = new JSONObject(response);
                String username = user.getString("name");
                String userId = user.getString("id");
                Log.d(TAG, "onCallFinish: " + username);
                storeDataToSharedPreference(userId, username, password);
                message.what = LOGIN_SUCCEED;
                handler.sendMessage(message);
            } else {
                message.what = LOGIN_FAILED;
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCallException() {
        Message message = new Message();
        message.what = SERVER_CONNECTION_FAILURE;
        handler.sendMessage(message);
    }

    private void storeDataToSharedPreference(String userId, String username, String password) {
        editor = pref.edit();
        if (remeberPass.isChecked()) {
            editor.putBoolean("remember_password", true);
            editor.putString("username", username);
            editor.putString("password", password);
        } else {
            editor.clear();
        }
        editor.putString("loginName", username);
        editor.putString("userId", userId);
        Log.d(TAG, "storeDataToSharedPreference: " + pref.getString("loginName", ""));
        editor.apply();
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCEED:
                    hideProgressBar();
                    onBackPressed();
                    break;
                case LOGIN_FAILED:
                    hideProgressBar();
                    loginFailed();
                    break;
                case SERVER_CONNECTION_FAILURE:
                    hideProgressBar();
                    serverConnectFailed();
                default:
                    break;
            }
        }
    };


    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void loginFailed() {
        Toast.makeText(this, "用户名或者密码不正确", Toast.LENGTH_SHORT).show();
    }

}
