package com.shoneworn.smartplug;

import android.app.Application;
import android.content.Context;

import com.ailin.shoneworn.mylibrary.SuperObserver;

/**
 * Created by admin on 2018/8/30.
 */


public class BaseApplication extends Application {

    public static Context context;
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        if (context == null) {
            context = getApplicationContext();
        }

        SuperObserver.getInstance().init(this);
    }

    public static Context getContext() {
        return context;
    }

}