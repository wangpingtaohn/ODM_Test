package com.quectel.agingtest.cases.wifi;

import android.util.Log;

import com.quectel.agingtest.common.BaseService;
import com.quectel.agingtest.common.ItemCases;
import com.quectel.agingtest.common.Utils;

import java.util.Map;

public class WifiService extends BaseService {

    private WifiManagerHelper wifiManagerHelper;

    private String wifiSsid;
    private String wifiPass;


    @Override
    public void beforeRun(ItemCases cases, Map<String, String> map) {
        if (wifiManagerHelper == null) {
            wifiManagerHelper = WifiManagerHelper.getInstance(getApplicationContext());
        }
        if (map != null) {
            wifiSsid = map.get("wifi_ssid");
            wifiPass = map.get("wifi_pass");
        }
    }

    @Override
    public boolean runCases(int time) {
        return testWifi();
    }

    private boolean testWifi() {
        wifiManagerHelper.closeWifi();
        boolean res = wifiManagerHelper.openWifi();
        Log.d("WifiService", "openWifi=" + res);
        boolean res1 = wifiManagerHelper.connect("Quectel-Customer", "Customer-Quectel");
        Log.d("WifiService", "connect=" + res1);
        return res1;
    }
}
