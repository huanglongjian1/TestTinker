package com.android.testtinker;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

public class RealApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("real","oncreate");
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        CrashReport.initCrashReport(getApplicationContext(), "c0d44f8e6a", true);
    }
}
