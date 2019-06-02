package com.koukou.it.customizeUI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class MyImageView extends AppCompatImageView {
    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;
    //子线程不能操作UI，通过Handler设置图片
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DATA_SUCCESS:
                    Object[] result = (Object[])msg.obj;
                    MyImageView igView =  (MyImageView)result[0];
                    Bitmap bitmap = (Bitmap) result[1];
                    igView.setImageBitmap(bitmap);
                    break;
                case NETWORK_ERROR:
                    //Toast.makeText(getContext(),"网络连接失败",Toast.LENGTH_SHORT).show();
                    break;
                case SERVER_ERROR:
                    //Toast.makeText(getContext(),"服务器发生错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //设置网络图片
    public void setImageURL(String path) {
        //开启一个线程用于联网
        new MyImageSetterThread(this,path).start();
    }
    class MyImageSetterThread extends Thread{
        MyImageView imageView;
        String path;
        public MyImageSetterThread(MyImageView imageView,String path) {
            this.imageView = imageView;
            this.path = path;
        }
        @Override
        public void run() {
            try {
                if(path==null || path.trim().length()==0||path.endsWith("null") || path.contains("comnull")){
                    return;
                }else if(path.startsWith("data:image")){
                    byte[] decodedString = Base64.decode(path.substring(path.indexOf("base64,")+"base64,".length()), Base64.DEFAULT);
                    Bitmap bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Message msg = Message.obtain();
                    Object[] result = new Object[]{imageView,bitMap};
                    msg.obj = result;
                    msg.what = GET_DATA_SUCCESS;
                    handler.sendMessage(msg);
                }else if(path.startsWith("/")){
                    if(new File(path).exists()){
                        Bitmap bitMap = BitmapFactory.decodeFile(path);
                        Message msg = Message.obtain();
                        Object[] result = new Object[]{imageView,bitMap};
                        msg.obj = result;
                        msg.what = GET_DATA_SUCCESS;
                        handler.sendMessage(msg);
                    }
                }else{
                    //pull from Cache if any
                    Bitmap filMap = getCachedBitmapForUrlIfAny(path);
                    if(filMap!=null){
                        Message msg = Message.obtain();
                        Object[] result = new Object[]{imageView,filMap};
                        msg.obj = result;
                        msg.what = GET_DATA_SUCCESS;
                        handler.sendMessage(msg);
                        return;
                    }
                    //把传过来的路径转成URL
                    URL url = new URL(path);
                    //获取连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //使用GET方法访问网络
                    connection.setRequestMethod("GET");
                    //超时时间为10秒
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);
                    //获取返回码
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = connection.getInputStream();
                        //使用工厂把网络的输入流生产Bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        //利用Message把图片发给Handler
                        Message msg = Message.obtain();
//                      msg.obj = bitmap;
                        Object[] result = new Object[]{imageView,bitmap};
                        msg.obj = result;
                        msg.what = GET_DATA_SUCCESS;
                        handler.sendMessage(msg);
                        inputStream.close();
                        buildCacheForThisBitmap(bitmap,path);
                    }else {
                        //服务启发生错误
                        handler.sendEmptyMessage(SERVER_ERROR);
                    }
                }
            } catch (IOException e) {

                //网络连接错误
                handler.sendEmptyMessage(NETWORK_ERROR);
            }
        }
    }

    private Bitmap getCachedBitmapForUrlIfAny(String url){
        if(url.startsWith("http")){
            File cacheDir = new File(getContext().getCacheDir().getAbsolutePath()+"/MyImageView");
            if(cacheDir.exists()){
                String md5name = md5(url);
                File nameToFind = new File(cacheDir.getAbsolutePath()+"/"+md5name+".JPEG");
                if(nameToFind.exists()&& nameToFind.isFile()){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(nameToFind));
                        return bitmap;
                    }catch (FileNotFoundException e) {
                       e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    public void buildCacheForThisBitmap(Bitmap bitmap, String url) {
        if(url.startsWith("http")){
            File cacheDir = new File(getContext().getCacheDir().getAbsolutePath()+"/MyImageView");
            if(!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            String md5name = md5(url);
            File nameToFind = new File(cacheDir.getAbsolutePath()+"/"+md5name+".JPEG");
            if(nameToFind.exists()&& nameToFind.isFile()){
            }else{
                try {
                    FileOutputStream os = new FileOutputStream(nameToFind);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}