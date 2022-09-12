package com.quectel.agingtest.common;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.quectel.agingtest.AgingApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CasesManager {

    private static List<ItemCases> mCases = new ArrayList<>();
    private static final String TAG = "CasesManager";
    private static Context mContext;
    private volatile static CasesManager mCaseManager;
    public final static String SPLIT = ",";

    private boolean isRunning = false; // 正在进程AgingTest

    private String currentCaseId = ""; // 当前正在进行的Case名称
    private HashSet<String> selectCases = new HashSet<>(); // 本次所选中的cases
    private HashSet<String> remainCases = new HashSet<>(); // 剩余

    private List<ItemParams> itemParamsCache = new ArrayList<>();
    private HashMap<String, String> paramsValues = new HashMap<>();// 存在所需配置参数的值

    private List<CaseResult> results;

    private Handler serviceHandler;
    private Handler mHandler;

    public final static int MSG_RUN = 0;
    public final static int MSG_STOP = 1;
    public final static int MSG_UPDATE_ITEM = 2;

    public CasesManager() {
        long time = System.currentTimeMillis();
        mCases = Utils.getCases(mContext);
        paramsValues = (HashMap<String, String>) ParamsUtils.getParams();
        itemParamsCache.add(new ItemParams(Utils.GLOBAL_KEY_TIME, "设置全局次数"));
        itemParamsCache.add(new ItemParams(Utils.GLOBAL_KEY_DURATION, "设置全局时长", "单位是分钟"));
        int global_time = 0, global_duration = 0;
        String kt = paramsValues.get(Utils.GLOBAL_KEY_TIME);
        String kd = paramsValues.get(Utils.GLOBAL_KEY_DURATION);
        try {
            if (kt != null && !TextUtils.isEmpty(kt)) {
                global_time = Integer.parseInt(kt);
            }
            if (kd != null && !TextUtils.isEmpty(kd)) {
                global_duration = Integer.parseInt(kd);
            }
        } catch (Exception e) {

        }

        if (mCases != null) {
            for (int i = 0; i < mCases.size(); i++) {
                ItemCases cases = mCases.get(i);
                if (cases.type == Utils.TYPE_NUMBER && global_time > 0) {
                    cases.time = global_time;
                }
                if (cases.type == Utils.TYPE_TIME && global_duration > 0) {
                    cases.time = global_duration;
                }
                if (cases.params != null) {
                    itemParamsCache.addAll(cases.params);
                }
            }
        }

        currentCaseId = Utils.getCurrentCaseName();
        Set<String> selected = Utils.getSelectedCases();
        if (selected != null) {
            selectCases.addAll(selected);
        }

        Set<String> remain = Utils.getRemainCases();
        if (remain != null) {
            remainCases.addAll(remain);
        }

        results = Utils.getCaseResults();
        System.out.println("time = " + (System.currentTimeMillis() - time) + "ms");
    }

    public static CasesManager getInstance(Context context) {
        if (mCaseManager == null) {
            mContext = context;
            synchronized (CasesManager.class) {
                if (mCaseManager == null) {
                    mCaseManager = new CasesManager();
                }
            }
        }
        return mCaseManager;
    }

    public List<ItemCases> getCases() {
        return mCases;
    }

    public List<ItemParams> getItemParams() {
        return itemParamsCache;
    }

    public String getParam(String key) {
        return paramsValues.get(key);
    }

    public boolean saveParam(String key, String val) {
        paramsValues.put(key, val);
        // 更新到
        ParamsUtils.saveParam(key, val);
        return true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public HashSet<String> getSelectedCases() {
        return selectCases;
    }

    public String getCurrentCaseId() {
        return currentCaseId;
    }

    public void setServiceHandler(Handler handler) {
        this.serviceHandler = handler;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void sendMsg(int what) {
        sendMsg(what, 0, 0, 0);
    }

    public void sendMsg(int what, int arg1, int arg2, Object obj) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();
            msg.what = what;
            msg.arg1 = arg1;
            msg.arg2 = arg2;
            msg.obj = obj;
            mHandler.sendMessage(msg);
        } else {
            Log.d(TAG, "sendMsg: mHandler is not null");
        }
    }

    /**
     * 开始进行case 测试
     *
     * @param selected 选择需要进行测试的cases
     */
    public boolean startTest(HashSet<String> selected) {
        if (results != null) results.clear();
        Utils.saveCaseResults(null);

        currentCaseId = "";
        Utils.saveCurrentCaseName("");

        selectCases.clear();
        selectCases.addAll(selected);
        Utils.saveSelectedCases(selectCases);

        remainCases.clear();
        remainCases.addAll(selected);
        return getNextAndRun();
    }

    public boolean getNextAndRun() {
        System.out.println("remainCases size=" + remainCases.size());
        if (remainCases.iterator().hasNext()) {
            String caseId = remainCases.iterator().next();
            currentCaseId = caseId;
            Utils.saveCurrentCaseName(caseId);
            remainCases.remove(caseId);
            System.out.println("remainCases size=" + remainCases.size());
            Utils.saveRemainCases(remainCases);
            return startCases(caseId);
        } else {
            stopTest();
            return false;
        }
    }


    /**
     * 强行停止测试
     */
    public void stopTest() {
        if (serviceHandler != null) {
            serviceHandler.sendEmptyMessage(BaseService.MSG_EVENT_STOP_SERVICE);
        }
        // 清除结果等待...
        isRunning = false;
        selectCases.clear();
        Utils.saveSelectedCases(selectCases);

        remainCases.clear();
        Utils.saveRemainCases(remainCases);

        currentCaseId = "";
        Utils.saveCurrentCaseName("");

        sendMsg(MSG_STOP);
    }

    public boolean startCases(String caseId) {
        boolean res = false;
        ItemCases cases = null;
        for (int i = 0; i < mCases.size(); i++) {
            if (caseId.equals(mCases.get(i).id)) {
                cases = mCases.get(i);
                break;
            }
        }

        if (cases != null) {
            String clz = getServiceClzName(caseId);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName(mContext, clz);
            intent.putExtra(Utils.BUNDLE_KEY_ITEM_CASE, cases);
            if (cases.params != null) {
                for (int i = 0; i < cases.params.size(); i++) {
                    ItemParams params = cases.params.get(i);
                    intent.putExtra(params.key, paramsValues.get(params.key));
                }
            }
            ComponentName name = mContext.startService(intent);
            System.out.println(name);
            res = true;
        }
        isRunning = res;
        if (isRunning) {
            sendMsg(MSG_RUN, 0, 0, currentCaseId);
        }
        return res;
    }

    /**
     * Service名称命名 包名+cases+caseid(小写)+'caseid'Service
     *
     * @param caseId
     * @return
     */
    private String getServiceClzName(String caseId) {
        return mContext.getPackageName() + ".cases." + caseId.toLowerCase() + "." + caseId + "Service";
    }

    /**
     * 更新Case测试结果后，会进行下一项的测试
     *
     * @param result
     */
    public void updateCaseResult(CaseResult result) {
        System.out.println(result);
        if (results == null) results = new ArrayList<>();
        results.add(result);
        Utils.saveCaseResults(results);
        Utils.saveCurrentCaseName("");
        getNextAndRun();
    }

    public void updateState(String id, int success, int fail) {
        if (mHandler != null) {
            sendMsg(MSG_UPDATE_ITEM, success, fail, id);
        }
    }

    public String getResultStr() {
        StringBuilder builder = new StringBuilder();
        if (results != null) {
            for (int i = 0; i < results.size(); i++) {
                CaseResult result = results.get(i);
                builder.append(result.id).append(",成功:").append(result.rightTime).append(",失败:").append(result.failTime).append("\n");
            }
        }
        return builder.toString();
    }
}
