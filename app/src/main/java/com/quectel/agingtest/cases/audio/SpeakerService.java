package com.quectel.agingtest.cases.audio;

/**
 * @Author yjm
 * @Date 2022/9/13-21:59
 * @desc
 */

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;
import com.quectel.agingtest.common.BaseService;
import com.quectel.agingtest.common.ItemCases;

import java.io.IOException;
import java.util.Map;

/**
 * 话筒测试
 */
public class SpeakerService extends BaseService implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    // 播放的音乐名称（可以下放至配置）
    private final static String MUSIC_NAME = "qualsound.wav";
    private MediaPlayer mPlayer;
    private final static ServiceConfig config = new ServiceConfig(false, true);

    private final static String TAG = "SpeakerService";

    @Override
    public void beforeRun(ItemCases cases, Map<String, String> map) {
        Log.e(TAG, "beforeRun");
        if (!checkParam()) {
            Toast.makeText(this,MUSIC_NAME + "文件未找到",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            AssetFileDescriptor fd = getAssets().openFd(MUSIC_NAME);
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(fd);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean runCases(int time) {
        Log.e(TAG, "runCases: time = " + time);
        if (time <= 0)
            return false;
        if (mPlayer == null || mPlayer.isPlaying()) {
            return false;
        }
        mPlayer.start();
        return true;
    }

    @Override
    protected ServiceConfig getServiceConfig() {
        return config;
    }

    /**
     * 检查条件
     * @return boolean
     */
    private boolean checkParam() {
        try {
            String[] lists = getAssets().list("");
            for (String filename : lists) {
                if (filename.equals(MUSIC_NAME))
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        updateCaseResultAsync(true);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        updateCaseResultAsync(false);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}
