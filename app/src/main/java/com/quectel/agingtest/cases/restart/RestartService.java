package com.quectel.agingtest.cases.restart;

import android.content.Context;
import android.os.PowerManager;

import com.quectel.agingtest.common.BaseService;
import com.quectel.agingtest.common.ItemCases;

import java.util.Map;

public class RestartService extends BaseService {

    private ServiceConfig restartConfig = new ServiceConfig(true, true);

    @Override
    public void beforeRun(ItemCases cases, Map<String, String> map) {
        if (getTotalTime() > 0) {
            updateCaseResultAsync(true);
        }
    }

    @Override
    public boolean runCases(int time) {
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        pm.reboot("1");
        return false;
    }

    @Override
    protected ServiceConfig getServiceConfig() {
        return restartConfig;
    }
}
