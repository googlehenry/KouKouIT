package com.koukou.it.activity;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koukou.it.R;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.internal.framed.FrameReader;

public class BasicActivity extends AppCompatActivity {
    private static final String TAG = "BasicActivity";
    public static final int LOGIN_SUCCEED = 1;
    public static final int LOGIN_FAILED= 2;
    public static final int SERVER_CONNECTION_FAILURE = 3;
    public static final int REGISTER_SUCCEED=4;
    public static final int REGISTER_FAILED=5;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public TextView errorTextView;
    public OkHttpClient client;

    //SharedPreference
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        gson = new GsonBuilder().create();

        errorTextView=findViewById(R.id.error_text);
        client = new OkHttpClient();
        pref = getSharedPreferences("data", MODE_PRIVATE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //add logic for the items of menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public JSONObject parseJson(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }






    public void serverConnectFailed(){
        Log.d(TAG, "serverConnectFailed");

        Toast.makeText(this,"对不起，处理失败，我们会尽快修复。",Toast.LENGTH_SHORT).show();
    }




}
