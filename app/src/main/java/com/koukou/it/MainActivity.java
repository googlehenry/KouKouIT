package com.koukou.it;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.koukou.it.fragment.CourseFragment;
import com.koukou.it.fragment.HomeFragment;
import com.koukou.it.fragment.LearningFragment;
import com.koukou.it.fragment.MeFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replaceFragment(HomeFragment.getInstance());
                    return true;
                case R.id.navigation_course:
                    replaceFragment(CourseFragment.getInstance());
                    return true;
                case R.id.navigation_learning:
                    replaceFragment(LearningFragment.getInstance());
                    return true;
                case R.id.navigation_me:
                    replaceFragment(MeFragment.getInstance());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(HomeFragment.getInstance());


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//左上角返回按钮
//        if (actionBar != null) {
//            actionBar.hide();
//        }


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_layout, fragment);
        transaction.commit();
    }


    public boolean isLogin() {
        pref = getSharedPreferences("data", MODE_PRIVATE);
        if (pref.getString("loginName", "") == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //add logic for the items of menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shareto:
                Toast.makeText(this, "You clicked action_shareto", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_contactsupport:
                Toast.makeText(this, "You clicked action_contactsupport", Toast.LENGTH_SHORT).show();
                break;
            case R.id.search:
                Toast.makeText(this, "You clicked search", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
