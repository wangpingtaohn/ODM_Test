package com.quectel.agingtest.common;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * BaseService提供的基础能力
 * 1. beforeRun
 * 2. runCases
 * 3. updateAsyncResult
 * 4. postDelay
 * 5. getServiceConfig
 */
public abstract class BaseService extends Service {

    private String TAG = this.getClass().getSimpleName();
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;

    private Map<String, String> params = new HashMap<>();
    private ItemCases currentCase;
    private Intent currentIntent;
    private static final int DEFAULT_TIME_INTERVAL = 300; // 上下两次间隔
    private static final int DEFAULT_FIRST_INTERVAL = 3000;// 执行runCases间隔
    private final ServiceConfig DF = new ServiceConfig(false, false, DEFAULT_TIME_INTERVAL, 0);

    private static final String KEY_FAIL = "case.async.fail";
    private static final String KEY_SUCCESS = "case.async.success";
    private static final String KEY_TOTAL = "case.async.total";

    public final static int MSG_EVENT_STOP_SERVICE = 100;
    private final static int MSG_EVENT_RUN = 10;
    private final static int MSG_EVENT_INIT = 1;
    private int totalTime = 0;
    private long endTime = 0;
    private int successNumber = 0;
    private int failNumber = 0;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_EVENT_INIT:
                    onHandleIntent((Intent) msg.obj);
                    break;
                case MSG_EVENT_STOP_SERVICE:
                    stopCaseService();
                    break;
                case MSG_EVENT_RUN:
                    totalTime++;
                    saveLocalAsync(0);
                    Log.d(TAG, "handleMessage: " + totalTime + "," + successNumber + "," + failNumber);
                    if (checkEnd()) {
                        setResult(successNumber, failNumber);
                    } else {
                        boolean res = runCases(totalTime);
                        if (!getConfig().isAsync) {
                            updateCaseResultAsync(res);
                        }
                    }
                    break;
            }

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("IntentService[" + TAG + "]");
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.what = MSG_EVENT_INIT;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        onStart(intent, 0);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void onHandleIntent(@Nullable Intent intent) {
        logd("onHandleIntent =" + intent);
        this.currentIntent = intent;
        CasesManager.getInstance(this).setServiceHandler(mServiceHandler);
        if (intent != null) {
            loadLocal();
            loadParams(intent);
            beforeRun(currentCase, params);
            if (currentCase.type == Utils.TYPE_TIME) {
                endTime = System.currentTimeMillis() / 1000 + currentCase.time * 60L;
            }
            runCasesHandlerFirst();
        }
    }

    private void loadLocal() {
        totalTime = Utils.getInt(KEY_TOTAL, 0);
        successNumber = Utils.getInt(KEY_SUCCESS, 0);
        failNumber = Utils.getInt(KEY_FAIL, 0);
    }

    private void loadParams(Intent intent) {
        currentCase = intent.getParcelableExtra(Utils.BUNDLE_KEY_ITEM_CASE);
        if (currentCase != null && currentCase.params != null) {
            for (int i = 0; i < currentCase.params.size(); i++) {
                ItemParams itemParams = currentCase.params.get(i);
                params.put(itemParams.key, intent.getStringExtra(itemParams.key));
            }
        }
    }

    public void logd(String msg) {
        Log.d(TAG, msg);
    }

    private void runCasesHandlerFirst() {
        if (mServiceHandler != null) {
            mServiceHandler.sendEmptyMessageDelayed(MSG_EVENT_RUN, getConfig().firstInterval);
        }
    }

    /**
     * 发送执行一次handler的信息
     */
    private void runCasesHandler() {
        if (mServiceHandler != null) {
            mServiceHandler.sendEmptyMessageDelayed(MSG_EVENT_RUN, getConfig().loopInterval);
        }
    }

    protected int getTotalTime() {
        return totalTime;
    }

    public abstract void beforeRun(ItemCases cases, Map<String, String> map);

    public abstract boolean runCases(int time);

    protected final void postDelayed(Runnable runnable, int delay) {
        if (mServiceHandler != null && runnable != null) {
            mServiceHandler.postDelayed(runnable, delay);
        }
    }

    private void stopCaseService() {
        if (mServiceHandler != null) {
            mServiceHandler.removeMessages(MSG_EVENT_RUN);
        }
        totalTime = 0;
        failNumber = 0;
        successNumber = 0;
        saveLocalAsync(-1);

    }

    private void setResult(int success, int fail) {
        CasesManager.getInstance(this).setServiceHandler(null);
        CasesManager.getInstance(this).updateCaseResult(new CaseResult(currentCase.id, success, fail));
        stopCaseService();
    }

    private void updateState(int success, int fail) {
        CasesManager.getInstance(this).updateState(currentCase.name, success, fail);
    }

    private boolean checkEnd() {
        if (currentCase.type == Utils.TYPE_NUMBER) {
            return totalTime > currentCase.time;
        } else {
            return endTime < System.currentTimeMillis() / 1000;
        }
    }

    private ServiceConfig getConfig() {
        return getServiceConfig() == null ? DF : getServiceConfig();
    }

    protected ServiceConfig getServiceConfig() {
        return DF;
    }

    protected final void updateCaseResultAsync(boolean res) {
        if (res) {
            successNumber++;
            saveLocalAsync(1);
        } else {
            failNumber++;
            saveLocalAsync(2);
        }
        updateState(successNumber, failNumber);
        // 是否继续
        if (!getConfig().isNoLoop) {
            runCasesHandler();
        }
    }

    // 0:total,1:success,2:fail other:all
    private void saveLocalAsync(int type) {
        if (type != 1 && type != 2) {
            Utils.putInt(KEY_TOTAL, totalTime);
        }
        if (type != 2 && type != 0) {
            Utils.putInt(KEY_SUCCESS, successNumber);
        }

        if (type != 0 && type != 1) {
            Utils.putInt(KEY_FAIL, failNumber);
        }
    }


    public static class ServiceConfig {

        /**
         * 是否循环执行 false 不需要，自己手动运行，true 自动执行
         */
        public boolean isNoLoop;
        /**
         * 执行结果是否异步返回，如果异步返回需要调用 updateCaseResultAsync(boolean result)
         */
        public boolean isAsync;
        /**
         * 间隔
         */
        public int loopInterval = DEFAULT_TIME_INTERVAL;

        /**
         * 首次间隔
         */
        public int firstInterval = DEFAULT_FIRST_INTERVAL;

        public ServiceConfig() {
        }

        public ServiceConfig(boolean isNoLoop, boolean isAsync) {
            this.isNoLoop = isNoLoop;
            this.isAsync = isAsync;
        }

        public ServiceConfig(boolean isNoLoop, boolean isAsync, int loopInterval, int firstInterval) {
            this.isNoLoop = isNoLoop;
            this.isAsync = isAsync;
            this.loopInterval = loopInterval;
            this.firstInterval = firstInterval;
        }


    }
}
