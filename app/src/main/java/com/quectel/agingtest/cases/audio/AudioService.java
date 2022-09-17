package com.quectel.agingtest.cases.audio;

import android.os.Handler;
import android.util.Log;

import com.quectel.agingtest.common.BaseService;
import com.quectel.agingtest.common.ItemCases;
import java.util.Map;

public class AudioService extends BaseService {

    private AudioRecordTool audioRecordTool;
    private Handler mHandler = new Handler();

    @Override
    public void beforeRun(ItemCases cases, Map<String, String> map) {

    }

    @Override
    public boolean runCases(int time) {
        if (time > 1){
            return false;
        }
        if (audioRecordTool == null){
            audioRecordTool = new AudioRecordTool();
            audioRecordTool.createAudioRecord();
        }
        Log.d("===wpt===","firstInterval=" + getServiceConfig().firstInterval + ",loopInterval="+ getServiceConfig().loopInterval
        + ",audioRecordTool=" + audioRecordTool);
        try {
            audioRecordTool.start();
            mHandler.postDelayed(() -> audioRecordTool.stop(),6 * 1000);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        if (audioRecordTool != null){
            audioRecordTool.stop();
        }
        super.onDestroy();
    }
}