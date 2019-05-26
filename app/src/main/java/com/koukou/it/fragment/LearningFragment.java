package com.koukou.it.fragment;

import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.koukou.it.R;
import com.koukou.it.bean.Account;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LearningFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LearningFragment";
    public static final MediaType JsonMediaType = MediaType.parse("application/json; charset=utf-8");
    private Button learningButton;

    //单例
    private static LearningFragment learningFragment;

    public static LearningFragment getInstance() {
        if (learningFragment == null) {
            learningFragment = new LearningFragment();
        }
        return learningFragment;
    }

    @Override
    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learning, container, false);
        learningButton=view.findViewById(R.id.learning);
        learningButton.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.learning:
                saveAccount();
                break;
            default:
        }
    }

    private void saveAccount(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String sdate=formatter.format(new Date());
        Log.d(TAG, "saveAccount: "+sdate);
       Account account=new Account("a.jpg","shopping",new BigDecimal("90.00"),sdate,"1");
       Gson gson=new Gson();
       final String accountStr=gson.toJson(account);
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = RequestBody.create(JsonMediaType,accountStr);
                Request request = new Request.Builder()
                        .url("http://192.168.174.1:8401/api/v1/accountToSave")
                        .post(requestBody)
                        .build();
                try {
                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d(TAG, "responseData: " + responseData);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }).start();
    }
}
