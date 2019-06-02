package com.koukou.it.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koukou.it.MainActivity;
import com.koukou.it.R;
import com.koukou.it.bean.Note;
import com.koukou.it.util.Constants;
import com.koukou.it.util.HttpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    //单例
    private static HomeFragment homeFragment;
    private FloatingActionButton takePhotoButton;
    private FloatingActionButton chooseFromAlbumButton;
    private FloatingActionButton saveButton;
    private ImageView photoView;
    private Uri imageUri;
    private File photoFile = null;
    private FragmentActivity activity;
    private Bitmap bitmap;
    private String imagHttpPath;
    private EditText addTitle;
    private EditText addContent;
    private SharedPreferences pref;
    private Gson gson;

    public static HomeFragment getInstance() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        takePhotoButton = (FloatingActionButton) view.findViewById(R.id.take_photo);
        chooseFromAlbumButton = (FloatingActionButton) view.findViewById(R.id.choose_from_album);
        saveButton = (FloatingActionButton) view.findViewById(R.id.save);
        photoView = view.findViewById(R.id.photo_view);
        addTitle = view.findViewById(R.id.add_title);
        addContent = view.findViewById(R.id.add_content);
        activity = getActivity();
        pref = activity.getSharedPreferences("data", activity.MODE_PRIVATE);
        gson = new GsonBuilder().create();

        takePhotoButton.setOnClickListener(this);
        chooseFromAlbumButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        photoView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo:
                Log.d(TAG, "onClick: take photo");
                Toast.makeText(getActivity(), "click take photo", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    android.support.v4.app.ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, Constants.TAKE_PHOTO);
                    takePhoto();
                } else {
                    takePhoto();
                }
                break;
            case R.id.choose_from_album:
                Log.d(TAG, "onClick: choose from album");
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    android.support.v4.app.ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.CHOOSE_FROM_ALBUM);
                    chooseFromAlbum();
                } else {
                    chooseFromAlbum();
                }
                break;
            case R.id.save:
                save();
                break;
            case R.id.photo_view:
                showDialogImage();
                break;
            default:
                break;
        }
    }

    private void takePhoto() {
        Log.d(TAG, "begin takePhoto...");
        File outputImage = new File(activity.getExternalCacheDir(), "note_image.jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(activity, "com.koukou.it.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Constants.TAKE_PHOTO);
    }

    private void chooseFromAlbum() {
        Log.d(TAG, "begin choose from album...");
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, Constants.CHOOSE_FROM_ALBUM);
    }

    private void showDialogImage() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View imgEntryView = inflater.inflate(R.layout.dialog_image, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        ImageView dialogImageView = imgEntryView.findViewById(R.id.dialog_image);
        dialogImageView.setImageBitmap(bitmap);
        dialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setView(imgEntryView);
        dialog.show();
    }

    private void save() {
        if (pref.getString("loginName", null) == null) {
            Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (photoFile != null) {
            saveImage(photoFile);
        }
    }

    private void saveNote() {
        String title = addTitle.getText().toString();
        String content = addContent.getText().toString();
        String userId = pref.getString("userId", null);
        Note note = new Note(title, content, imagHttpPath, userId);
        Log.d(TAG, "saveNote: note"+note.toString());
        Log.d(TAG, "saveNote: userId" + userId + "imagHttpPath: " + imagHttpPath);
        String noteJson = gson.toJson(note);
        String url = Constants.NOTE_SERVER_PREFIX + "api/v1/note";
        HttpUtil.sendOkHttpPostRequest(noteJson, url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
                        clearFragment();
                    }
                });
            }
        });
    }

    private void clearFragment() {
        addTitle.setText("");
        addContent.setText("");
        photoView.setVisibility(View.GONE);
    }

    private void saveImage(File photoFile) {
        String url = Constants.NOTE_SERVER_PREFIX + "/api/v1/image";
        HttpUtil.uploadImage(url, photoFile, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String httpPath = response.body().string();
                setResponseToImageHttpPathAndSaveNote(httpPath);
            }
        });
    }

    private void setResponseToImageHttpPathAndSaveNote(String httpPath) {
        imagHttpPath = httpPath;
        Log.d(TAG, "setResponseToImageHttpPath: " + imagHttpPath);
        saveNote();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.TAKE_PHOTO:
                Log.d(TAG, "Handling after take photo...");
                if (resultCode == activity.RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri));
                        photoFile = new File(activity.getExternalCacheDir(), "note_image.jpg");
                        photoView.setImageBitmap(bitmap);
                        photoView.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Constants.CHOOSE_FROM_ALBUM:
                Log.d(TAG, "Handling after choose from album...");
                if (resultCode == activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent intent) {
        String imagePath = "";
        Uri uri = intent.getData();
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        photoFile = new File(imagePath);
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent intent) {
        Uri uri = intent.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
        photoFile = new File(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            bitmap = BitmapFactory.decodeFile(imagePath);
            photoView.setImageBitmap(bitmap);
            photoView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(activity, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

}
