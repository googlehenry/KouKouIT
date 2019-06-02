package com.koukou.it.Adapter;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.koukou.it.R;
import com.koukou.it.activity.WeatherActivity;
import com.koukou.it.bean.Note;
import com.koukou.it.customizeUI.MyImageView;
import com.koukou.it.util.Constants;

import java.util.List;
import java.util.zip.Inflater;

public class NoteAdapter extends ArrayAdapter<Note> {
    private static final String TAG = "NoteAdapter";
    private int resourceId;

    public NoteAdapter(Context context, int textViewResourceId, List<Note> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        MyImageView noteImage = view.findViewById(R.id.note_image);
        TextView noteTitle = view.findViewById(R.id.note_title);
        String imagePath = Constants.NOTE_SERVER_PREFIX + note.getImagePath();
        Log.d(TAG, "getView: " + imagePath);
//        Glide.with(getContext()).load(imagePath).into(noteImage);
        noteImage.setImageURL(imagePath);
        noteTitle.setText(note.getTitle());
        return view;
    }
}
