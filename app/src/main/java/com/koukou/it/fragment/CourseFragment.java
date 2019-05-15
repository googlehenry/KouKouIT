package com.koukou.it.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koukou.it.R;

public class CourseFragment extends Fragment {
    //单例
    private static CourseFragment courseFragment;

    public static  CourseFragment getInstance(){
        if(courseFragment==null){
            courseFragment=new CourseFragment();
        }
        return courseFragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_course,container,false);
        return view;
    }
}
