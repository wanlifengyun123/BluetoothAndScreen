package com.yajun.app.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.yajun.app.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by yajun on 2017/5/31.
 *
 */

public class NetDemo {

    private String mBaseUrl = "http://192.168.19.111:8080/Imooc_okhttp/";

    private OkHttpClient okHttpClient;

    public NetDemo (){
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookiesManager()).build();
    }

    public void doGet(){
        Request.Builder builder = new Request.Builder();
        Request request = builder.get()
                .url(mBaseUrl + "login?userName=wangyajun&passWord=123456")
                .build();
        executeRequest(request);
    }

    public void doPost(){
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("userName","wangyajun");
        formBuilder.add("passWord","123456");

        Request.Builder builder = new Request.Builder();
        Request request = builder.get()
                .url(mBaseUrl + "login")
                .post(formBuilder.build())
                .build();
        executeRequest(request);
    }

    public void doPostString(){
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;chaset=utf-8"), "{userName:wangyajun,passWord:123456}");
        Request.Builder builder = new Request.Builder();
        Request request = builder.get()
                .url(mBaseUrl + "postString")
                .post(requestBody)
                .build();
        executeRequest(request);
    }

    public void doPostFile(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/night3.jpg");
        if(!file.exists()){
            Log.e("","doPostFile file is not exists !");
            return;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("applcation/octet-stream"), file);
        Request.Builder builder = new Request.Builder();
        Request request = builder.get()
                .url(mBaseUrl + "postFile")
                .post(requestBody)
                .build();
        executeRequest(request);
    }

    public void doPostUploadFile(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/night3.jpg");
        if(!file.exists()){
            Log.e("","doPostFile file is not exists !");
            return;
        }

        MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("userName", "李志峰")
                .addFormDataPart("passWord", "123456")
                .addFormDataPart("mPhoto", "lizhifeng.jpg", RequestBody.create(MediaType.parse("applcation/octet-stream"), file))
                .build();

        CountingRequestBody countingRequestBody= new CountingRequestBody(body, new CountingRequestBody.RequestProgressListener() {
            @Override
            public void onRequestProgressListener(long byteWrited, long contentLength) {
                Log.e("","CountingRequestBody : " + byteWrited + "/" + contentLength);
            }
        });

        Request.Builder builder = new Request.Builder();
        Request request = builder.get()
                .url(mBaseUrl + "uploadInfo")
                .post(countingRequestBody)
                .build();
        executeRequest(request);
    }

    private void doDownLoad() {
        Request.Builder builder = new Request.Builder();
        Request request = builder.get()
                .url(mBaseUrl + "files/desert.jpg")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("","onFailure:" + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("","onResponse : " );

                long total = response.body().contentLength();
                long sum = 0L;

                InputStream is = response.body().byteStream();
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/desert.jpg");

                FileOutputStream fos = new FileOutputStream(file);

                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf,0,len);
                    sum += len;
                    Log.e("","Progress:" + sum + "/" + total);
                }

                fos.flush();
                fos.close();
                is.close();

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTvLog.setText("DownLoad Success !");
//                    }
//                });
            }
        });
    }

    private void doDownLoadImage() {
        Request.Builder builder = new Request.Builder();
        Request request = builder.get()
                .url(mBaseUrl + "files/desert.jpg")
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("","onFailure:" + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("","onResponse : " );
                InputStream is = response.body().byteStream();

                final Bitmap bitmap = BitmapFactory.decodeStream(is);

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        imageView.setImageBitmap(bitmap);
//                    }
//                });

                is.close();

            }
        });
    }

    private void executeRequest(Request request) {
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("","onFailure:" + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("","onResponse : " );
                final String res = response.body().string();
                Log.e("",res);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTvLog.setText(res);
//                    }
//                });
            }
        });
    }

    public class CookiesManager implements CookieJar {

        private final PersistentCookieStore cookieStore = new PersistentCookieStore(App.getInstance());

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    cookieStore.add(url, item);
                }
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url);
            return cookies;
        }
    }
}
