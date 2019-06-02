package com.koukou.it.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koukou.it.Adapter.NoteAdapter;
import com.koukou.it.R;
import com.koukou.it.bean.Note;
import com.koukou.it.util.Constants;
import com.koukou.it.util.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CourseFragment extends Fragment {
    private static final String TAG = "CourseFragment";
    //单例
    private static CourseFragment courseFragment;
    private SharedPreferences pref;
    private List<Note> noteList = new ArrayList<Note>();
    private FragmentActivity activity;

    public static CourseFragment getInstance() {
        if (courseFragment == null) {
            courseFragment = new CourseFragment();
        }
        return courseFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        activity = getActivity();
        pref = activity.getSharedPreferences("data", activity.MODE_PRIVATE);
        initNotes();
        return view;
    }

    private void initNotes() {
        String userId = pref.getString("userId", null);
        if (pref.getString("userId", null) == null) {
            Toast.makeText(activity, "请先登录，才可以查看日记哦", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = Constants.NOTE_SERVER_PREFIX + "api/v1/" + userId + "/notes";

        HttpUtil.sendOkHttpGetRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "对不起，获取信息失败，请稍后重试。", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                setResponseToNoteList(responseData);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inflateListViewWithNoteList();
                    }
                });
            }
        });
    }

    private void setResponseToNoteList(String response) {
        Gson gson = new Gson();
        Log.d(TAG, "setResponseToNoteList: response " + response);
        noteList = gson.fromJson(response, new TypeToken<List<Note>>() {
        }.getType());
    }

    private void inflateListViewWithNoteList() {
        NoteAdapter adapter = new NoteAdapter(activity, R.layout.note_item, noteList);
        ListView listView = activity.findViewById(R.id.note_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = noteList.get(position);
                Toast.makeText(activity, note.getId() + ":" + note.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        listView.setAdapter(adapter);
    }
}
