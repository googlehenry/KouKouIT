package com.koukou.it.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koukou.it.R;

public class HomeFragment extends Fragment {
    //单例
    private static HomeFragment homeFragment;

    public static  HomeFragment getInstance(){
        if(homeFragment==null){
            homeFragment=new HomeFragment();
        }
        return homeFragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);
        return view;
    }
}
