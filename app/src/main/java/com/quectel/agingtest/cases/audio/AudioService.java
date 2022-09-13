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
        if (audioRecordTool == null){
            getServiceConfig().firstInterval = 5000;
            getServiceConfig().loopInterval = 5000;
            audioRecordTool = new AudioRecordTool();
            audioRecordTool.createAudioRecord();
        }else {
            audioRecordTool.stop();
        }
        Log.d("===wpt===","firstInterval=" + getServiceConfig().firstInterval + ",loopInterval="+ getServiceConfig().loopInterval
        + ",audioRecordTool=" + audioRecordTool);
        try {
            audioRecordTool.start();
            /*mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    audioRecordTool.stop();
                }
            },5 * 1000);*/
        } catch (Exception e){
            return false;
        }
        if (getTotalTime() == time){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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