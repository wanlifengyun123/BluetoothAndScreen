package com.yajun.app.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yajun.app.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by yajun on 2017/5/22.
 *
 */
public class LoginActivity extends AppCompatActivity {

    public static final int PAGE_LIMIT = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // http://image.baidu.com/data/imgs?col=%E5%8A%A8%E6%BC%AB&tag=%E5%85%A8%E9%83%A8&pn=0&rn=20&from=1
//        http://wangyi.butterfly.mopaasapp.com/news/api?type=war&page=1&limit=10
    }

    public String getImagesListUrl(String category, int pageNum) {
        StringBuffer sb = new StringBuffer();
        sb.append("http://image.baidu.com/data/imgs");
        sb.append("?col=");
        try {
            sb.append(URLEncoder.encode(category, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("&tag=");
        try {
            sb.append(URLEncoder.encode("全部", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("&pn=");
        sb.append(pageNum * PAGE_LIMIT);
        sb.append("&rn=");
        sb.append(PAGE_LIMIT);
        sb.append("&from=1");
        return sb.toString();
    }
}
