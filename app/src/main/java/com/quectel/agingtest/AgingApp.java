package com.quectel.agingtest;

import android.app.Application;
import android.content.Context;

import com.quectel.agingtest.common.CasesManager;
import com.tencent.mmkv.MMKV;

public class AgingApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MMKV.initialize(this);
        CasesManager.getInstance(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
