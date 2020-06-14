package com.example.android.mvvm.Utils;

import android.app.Application;

public class NoteMvvmUtils extends Application {
    private static NoteMvvmUtils sNoteMvvmUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        sNoteMvvmUtils = this;
    }
    public static NoteMvvmUtils getApp(){
        return sNoteMvvmUtils;
    }
}
