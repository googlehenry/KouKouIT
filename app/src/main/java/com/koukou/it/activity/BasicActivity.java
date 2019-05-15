package com.koukou.it.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.koukou.it.R;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.internal.framed.FrameReader;

public class BasicActivity extends AppCompatActivity {
    private static final String TAG = "BasicActivity";
    public static final int LOGIN_SUCCEED = 1;
    public static final int LOGIN_FAILURE = 2;
    public static final int SERVER_CONNECTION_FAILURE = 3;

    public TextView errorTextView;
    public OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        errorTextView=findViewById(R.id.error_text);
        client = new OkHttpClient();

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


    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCEED:
                    onBackPressed();
                    break;
                case LOGIN_FAILURE:
                    loginFailed();
                    break;
                case SERVER_CONNECTION_FAILURE:
                    serverConnectFailed();
                default:
                    break;
            }
        }
    };

    public void loginFailed() {
        Toast.makeText(this, "用户名或者密码不正确", Toast.LENGTH_SHORT).show();
    }

    public void serverConnectFailed(){
        Log.d(TAG, "serverConnectFailed");

        Toast.makeText(this,"对不起，服务器连接失败，请检查网络。",Toast.LENGTH_SHORT).show();
    }

    public void onCallException() {
        Message message = new Message();
        message.what = SERVER_CONNECTION_FAILURE;
        handler.sendMessage(message);
    }

}
