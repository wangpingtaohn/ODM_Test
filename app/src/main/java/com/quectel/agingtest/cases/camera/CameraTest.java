/*
package com.sunmi.aging.items;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Handler;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.sunmi.aging.BuildConfig;
import com.sunmi.aging.R;
import com.sunmi.aging.TestItemBase;
import com.sunmi.aging.libs.OpenGLView;
import java.util.List;
import org.openni.Device;
import org.openni.VideoMode;
import org.openni.VideoStream;
import org.openni.android.OpenNIHelper;

*/
/* loaded from: CameraTest.class *//*

public class CameraTest extends TestItemBase {
    private int TestTime;
    int count;
    private Context mContext;
    private Device mDevice;
    private OpenGLView mGLView;
    private OpenNIHelper mOpenNIHelper;
    private PowerManager mPowerManager;
    private VideoStream mStream;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private List<VideoMode> mVideoModes;
    private PowerManager.WakeLock mWakeLock;
    private Thread m_thread;
    private Button takeButton;
    TextView tv;
    private Handler mHandler = new Handler();
    private Camera mCamera = null;
    boolean mInit_Ok = false;
    private boolean ifLocked = false;
    private boolean mExit = false;
    private int mWidth = 640;
    private int mHeight = 480;
    private final int DEPTH_NEED_PERMISSION = 33;
    private Object m_sync = new Object();
    private Runnable mRunnable = new Runnable() { // from class: com.sunmi.aging.items.CameraTest.1
        @Override // java.lang.Runnable
        public void run() {
            ((ActivityManager) CameraTest.this.getActivity().getSystemService("activity")).forceStopPackage("com.mario.sdktester.debug");
        }
    };

    private void wakeLock() {
        if (!this.ifLocked) {
            this.ifLocked = true;
            this.mWakeLock.acquire();
        }
    }

    private void wakeUnlock() {
        if (this.ifLocked) {
            this.mWakeLock.release();
            this.ifLocked = false;
        }
    }

    @Override // com.sunmi.aging.TestItemBase
    public String getKey() {
        return "camera";
    }

    protected int getLayoutId() {
        return R.layout.camera_test;
    }

    @Override // com.sunmi.aging.TestItemBase
    public String getTestMessage() {
        return BuildConfig.FLAVOR;
    }

    @Override // com.sunmi.aging.TestItemBase
    public View getTestView(LayoutInflater layoutInflater) {
        View inflate = layoutInflater.inflate(R.layout.test_msg, (ViewGroup) null);
        this.mSurfaceView = (SurfaceView) inflate.findViewById(R.id.camera_surface);
        this.tv = (TextView) inflate.findViewById(R.id.test_msg);
        return inflate;
    }

    @Override // android.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
    }

    @Override // com.sunmi.aging.TestItemBase
    public void onStartTest() {
        this.mPowerManager = (PowerManager) getActivity().getSystemService("power");
        this.mWakeLock = this.mPowerManager.newWakeLock(10, "BackLight");
        wakeLock();
        TextView textView = this.tv;
        if (textView != null) {
            textView.setText("Camera正在测试中...");
        }
        this.mHandler.removeCallbacks(this.mRunnable);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.sunmi.aging_preferences", 0);
        this.TestTime = sharedPreferences.getInt(getKey() + "_time", 40);
        Intent intent = new Intent();
        intent.setClassName("com.mario.sdktester.debug", "com.mario.sdktester.ObAstraConfigActivity");
        intent.putExtra("time", this.TestTime);
        try {
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        this.mHandler.postDelayed(this.mRunnable, this.TestTime * 1000 * 60);
    }

    @Override // com.sunmi.aging.TestItemBase
    public void onStopTest() {
        wakeUnlock();
        this.mHandler.removeCallbacks(this.mRunnable);
        postSuccess();
    }
}*/
