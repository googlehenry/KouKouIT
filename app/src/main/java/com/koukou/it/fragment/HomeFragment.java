package com.koukou.it.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.koukou.it.MainActivity;
import com.koukou.it.R;
import com.koukou.it.util.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    //单例
    private static HomeFragment homeFragment;
    private FloatingActionButton takePhotoButton;
    private FloatingActionButton chooseFromAlbumButton;
    private FloatingActionButton saveButton;
    private ImageView photoView;
    private Uri imageUri;
    private File photoFile;
    private FragmentActivity activity;

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
        activity = getActivity();

        takePhotoButton.setOnClickListener(this);
        chooseFromAlbumButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
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
            default:
                break;
        }
    }

    private void chooseFromAlbum() {
        Log.d(TAG, "begin choose from album...");
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, Constants.CHOOSE_FROM_ALBUM);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.TAKE_PHOTO:
                Log.d(TAG, "Handling after take photo...");
                if (resultCode == activity.RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri));
                        photoFile = new File(activity.getExternalCacheDir(), "note_image.jpg");
                        photoView.setImageBitmap(bitmap);
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
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            photoView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(activity, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}
