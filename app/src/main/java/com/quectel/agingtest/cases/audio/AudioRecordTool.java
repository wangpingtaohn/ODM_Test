package com.quectel.agingtest.cases.audio;

/**
 * @Author yjm
 * @Date 2022/9/13-21:08
 * @desc
 */

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于AudioRecord的录音工具类
 */
public class AudioRecordTool{
    private AudioRecord audioRecord;
    private int recordBufSize = 0;
    private byte data[];
    private PcmToWavTool tool;
    private boolean isRecording = false;
    private int mIndex = 0;
    //录音得到的文件 的储存位置及文件名
//    private final String pcmFileName = Environment.getExternalStorageDirectory() + "/Download/record.pcm";
    private final String pcmFileName = Environment.getExternalStorageDirectory() + "/Download/record";
    //转换成wav文件后新文件的存储位置及文件名
//    private final String wavFileName = Environment.getExternalStorageDirectory() + "/Download/record1.wav";
//    private final String wavFileName = Environment.getExternalStorageDirectory() + "/Download/record";
    // 音频源：音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    // 采样率：音频的采样频率，每秒钟能够采样的次数，采样率越高，音质越高
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_SAMPLE_RATE = 44100;

    // 声道设置：android支持双声道立体声和单声道。MONO单声道，STEREO立体声
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;

    // 编码制式和采样大小：采集来的数据当然使用PCM编码
    // (脉冲代码调制编码，即PCM编码。PCM通过抽样、量化、编码三个步骤将连续变化的模拟信号转换为数字编码。)
    // android支持的采样大小16bit 或者8bit。当然采样大小越大，那么信息量越多，音质也越高，现在主流的采样
    // 大小都是16bit，在低质量的语音传输的时候8bit 足够了。
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private List<String> pcmList = new ArrayList<>();
    private List<String> wavList = new ArrayList<>();

    //初始化
    @SuppressLint("MissingPermission")
    public void createAudioRecord() {
        recordBufSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);  //audioRecord能接受的最小的buffer大小
        //构造方法，传入的参数上面在有解析
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, recordBufSize);
        data = new byte[recordBufSize];
        tool = new PcmToWavTool(AUDIO_SAMPLE_RATE,AUDIO_CHANNEL,AUDIO_ENCODING);
    }
    //开始录音
    public void start(){
        mIndex++;
        Log.i("AUDIO","开始录音");
        isRecording = true;
        //调用startRecording()方法开始录制
        createAudioRecord();
        audioRecord.startRecording();
        MyThread myThread = new MyThread();
        myThread.start();
    }
    //停止录音
    public void stop(){
        isRecording = false;
        Log.d("===wpt===","audioRecord=" + audioRecord);
        if (audioRecord != null){
            Log.i("AUDIO","停止录音");
            //调用stop()方法停止录制
            audioRecord.stop();
            //调用release() 释放本机录音资源。
//            audioRecord.release();
//            audioRecord = null;
        }
        //利用自定义工具类将pcm格式的文件转换为wav格式文件才能进行播放
        for (String fileName: pcmList){
            int index = fileName.lastIndexOf(".");
            String wavFileName = fileName.substring(0,index) + ".wav";
            tool.pcmToWav(fileName,wavFileName);
        }
    }

    private String getPcmFileName(){
        return pcmFileName + "_" + mIndex + ".pcm";
    }

    /*private String getWavFileName(){
        return wavFileName + "_" + mIndex + ".wav";
    }*/
    private class MyThread extends Thread{
        @Override
        public void run() {
            super.run();
            FileOutputStream os = null;
            try {
                //如果文件不存在，就创建文件
                String fileName = getPcmFileName();
                pcmList.add(fileName);
                if(!new File(fileName).exists()){
                    new File(fileName).createNewFile();
                }
                os = new FileOutputStream(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (null != os) {
                while (isRecording) {
                    //调用read(@NonNull byte[] audioData, int offsetInBytes, int sizeInBytes)方法
                    // 从音频硬件读取音频数据，以便记录到字节数组中。
                    int read = audioRecord.read(data, 0, recordBufSize);

                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
                            os.write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    //关闭文件
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


