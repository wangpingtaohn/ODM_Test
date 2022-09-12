package com.quectel.agingtest.common;

import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.Map;

public class ParamsUtils {

    private static MMKV mmkv = MMKV.mmkvWithID("params");

    public static Map<String, String> getParams() {
        String[] keys = mmkv.allKeys();
        HashMap<String,String> params = new HashMap<>();
        if(keys !=null){
            for (int i = 0; i < keys.length; i++) {
                params.put(keys[i],mmkv.getString(keys[i],""));
            }
        }
        return params;
    }

    public static void saveParam(String key, String val) {
        mmkv.putString(key, val);
    }

    public static String getParam(String key) {
        return mmkv.getString(key, "");
    }
}
