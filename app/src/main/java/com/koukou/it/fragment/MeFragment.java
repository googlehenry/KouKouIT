package com.koukou.it.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koukou.it.MainActivity;
import com.koukou.it.R;
import com.koukou.it.activity.MyLoginActivity;

import org.w3c.dom.Text;

public class MeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MeFragment";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private LinearLayout notloginLayout;
    private LinearLayout loginLayout;
    private LinearLayout logoutLayout;
    private TextView loginTextView;
    //单例
    private static MeFragment meFragment;

    public static MeFragment getInstance() {
        if (meFragment == null) {
            meFragment = new MeFragment();
        }
        return meFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getContext().getSharedPreferences("data", getContext().MODE_PRIVATE);

        View view = inflater.inflate(R.layout.fragment_me, container, false);
        notloginLayout = view.findViewById(R.id.me_not_login_layout);
        loginLayout = view.findViewById(R.id.me_login_layout);
        logoutLayout = view.findViewById(R.id.me_log_out);
        loginTextView = view.findViewById(R.id.me_login_text);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_not_login_layout:
                goToLoginPage();
                break;
            case R.id.me_log_out:
                editor = pref.edit();
                editor.remove("loginName");
                editor.remove("userId");
                editor.apply();
                Log.d(TAG, "logout, " + pref.getString("loginName", ""));
                refreshInternal();
                break;
            default:
                break;
        }
    }

    private void goToLoginPage() {
        Log.d(TAG, "goToLoginPage");
        Intent intent = new Intent(getContext(), MyLoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshInternal();
    }

    private void refreshInternal() {
        if (pref == null) {
            pref = getContext().getSharedPreferences("data", getContext().MODE_PRIVATE);
        }
        String loginName = pref.getString("loginName", "");
        Log.d(TAG, "refreshInternal: " + loginName);
        if ("".equals(loginName)) {
            notloginLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
            logoutLayout.setVisibility(View.GONE);
            notloginLayout.setOnClickListener(this);
        } else {
            loginLayout.setVisibility(View.VISIBLE);
            logoutLayout.setVisibility(View.VISIBLE);
            notloginLayout.setVisibility(View.GONE);

            loginTextView.setText("你好， " + loginName);
            logoutLayout.setOnClickListener(this);
        }

    }
}
