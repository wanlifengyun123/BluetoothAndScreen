package com.yajun.app;

import android.app.Application;

import com.yajun.app.util.LogUtil;
import com.yajun.app.util.ToastUtil;

/**
 * Created by yajun on 2017/5/31.
 *
 */
public class App extends Application {

    static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        ToastUtil.init(this);
        LogUtil.init(true,"om.yajun.app");
    }
}
