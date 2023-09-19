package com.android.testtinker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

public class MyApp extends SophixApplication {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initSophix();
    }


    private void initSophix() {
        String appVersion = "0.0.0";
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;

            Log.e("appVersion", appVersion);
        } catch (Exception e) {
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setUsingEnhance(true) // 适配加固模式,如果app使用了加固则需要加上此方法
                .setAppVersion(appVersion)
                .setSecretMetaData(null, null, null)
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        String msg = new StringBuilder("").append("Mode:").append(mode)
                                .append(" Code:").append(code)
                                .append(" Info:").append(info)
                                .append(" HandlePatchVersion:").append(handlePatchVersion).toString();

                        Log.e(TAG, "onCreate://...... " + msg.toString());

                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.e("TAG", "sophix load patch success!");
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
                            Log.e("TAG", "sophix preload patch success. restart app to make effect.");
                        }
                    }
                }).initialize();

    }

    @Keep
    @SophixEntry(RealApp.class)
    static class RealApplicationStub {
    }
}
