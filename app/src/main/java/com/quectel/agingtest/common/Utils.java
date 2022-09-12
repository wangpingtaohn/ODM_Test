package com.quectel.agingtest.common;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Utils {

    private static final MMKV mmkv = MMKV.defaultMMKV();

    public static final int TYPE_TIME = 1;
    public static final int TYPE_NUMBER = 0;

    public static final String GLOBAL_KEY_TIME = "global_time";//次数
    public static final String GLOBAL_KEY_DURATION = "global_duration";//时长

    public static final String BUNDLE_KEY_ITEM_CASE = "item_case";
    public static final String BUNDLE_KEY_RESULT = "case_results";
    public static final String BUNDLE_KEY_CASE_NAME = "case_name";
    public static final String BUNDLE_KEY_CASE_SELECTED = "case_selected";
    public static final String BUNDLE_KEY_CASE_REMAIN = "case_remain";

    public static int getInt(String key, int def) {
        return mmkv.getInt(key, def);
    }

    public static void putInt(String key, int val) {
        System.out.println("key="+key+",val="+val);
        mmkv.putInt(key, val);
    }

    public static String getCaseTypeName(int type) {
        return type == TYPE_NUMBER ? "次数" : "时长";
    }

    public static String getCurrentCaseName() {
        return mmkv.getString(BUNDLE_KEY_CASE_NAME, "");
    }

    public static void saveCurrentCaseName(String val) {
        mmkv.putString(BUNDLE_KEY_CASE_NAME, val);
    }

    public static Set<String> getSelectedCases() {
        return mmkv.getStringSet(BUNDLE_KEY_CASE_SELECTED, null);
    }

    public static void saveSelectedCases(HashSet<String> selected) {
        mmkv.putStringSet(BUNDLE_KEY_CASE_SELECTED, selected);
    }

    public static Set<String> getRemainCases() {
        return mmkv.getStringSet(BUNDLE_KEY_CASE_REMAIN, null);
    }

    public static void saveRemainCases(HashSet<String> selected) {
        mmkv.putStringSet(BUNDLE_KEY_CASE_REMAIN, selected);
    }

    public static void saveCaseResults(List<CaseResult> results) {
        String res = "";
        if (results != null) {
            res = GsonUtils.toJsonFilterNullField(results);
        }
        mmkv.putString(BUNDLE_KEY_RESULT, res);
    }

    public static List<CaseResult> getCaseResults() {
        String res = mmkv.getString(BUNDLE_KEY_RESULT, "");
        if (res != null && res.length() > 0) {
            return GsonUtils.fromJson(res, new TypeToken<List<CaseResult>>() {
            }.getType());
        }
        return null;
    }


    /**
     * 获取去最原始的数据信息
     *
     * @return json data
     */
    public static List<ItemCases> getCases(Context context) {
        InputStream input = null;
        try {
            input = context.getAssets().open("cases_config.json");
            String json = convertStreamToString(input);
            return GsonUtils.fromJson(json, new TypeToken<List<ItemCases>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * input 流转换为字符串
     *
     * @param is
     * @return
     */
    private static String convertStreamToString(InputStream is) {
        String s = null;
        try {
            //格式转换
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            if (scanner.hasNext()) {
                s = scanner.next();
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
