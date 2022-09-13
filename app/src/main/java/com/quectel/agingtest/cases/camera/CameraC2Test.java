/*
package com.quectel.agingtest.cases.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.sunmi.aging.BuildConfig;
import com.sunmi.aging.R;
import com.sunmi.aging.TestItemBase;
import com.sunmi.aging.libs.OpenGLView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.OpenNI;
import org.openni.PixelFormat;
import org.openni.SensorType;
import org.openni.VideoMode;
import org.openni.VideoStream;
import org.openni.android.OpenNIHelper;

*/
/* loaded from: CameraC2Test.class *//*

public class CameraC2Test extends TestItemBase implements SurfaceHolder.Callback, OpenNIHelper.DeviceOpenListener {
    int count;
    private Context mContext;
    private Device mDevice;
    private OpenGLView mGLView;
    private OpenNIHelper mOpenNIHelper;
    private VideoStream mStream;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private List<VideoMode> mVideoModes;
    private Thread m_thread;
    private Button takeButton;
    private Handler mHandler = new Handler();
    private Camera mCamera = null;
    boolean mInit_Ok = false;
    private boolean mExit = false;
    private int mWidth = 640;
    private int mHeight = 480;
    private final int DEPTH_NEED_PERMISSION = 33;
    private Object m_sync = new Object();
    private Runnable mStartDepthTask = new Runnable() { // from class: com.sunmi.aging.items.CameraC2Test.1
        @Override // java.lang.Runnable
        public void run() {
            CameraC2Test cameraC2Test = CameraC2Test.this;
            cameraC2Test.mOpenNIHelper = new OpenNIHelper(cameraC2Test.getActivity());
            CameraC2Test.this.mOpenNIHelper.requestDeviceOpen(CameraC2Test.this);
            CameraC2Test.this.mExit = false;
            CameraC2Test.this.mHandler.postDelayed(CameraC2Test.this.mStopDepthTask, 20000L);
        }
    };
    private Runnable mStopDepthTask = new Runnable() { // from class: com.sunmi.aging.items.CameraC2Test.2
        @Override // java.lang.Runnable
        public void run() {
            CameraC2Test.this.stopDepth();
            CameraC2Test.this.mHandler.postDelayed(CameraC2Test.this.mStartDepthTask, 10000L);
        }
    };
    private Runnable mRunnable = new Runnable() { // from class: com.sunmi.aging.items.CameraC2Test.3
        @Override // java.lang.Runnable
        public void run() {
            CameraC2Test.this.takePicture();
            CameraC2Test.this.mHandler.postDelayed(CameraC2Test.this.mRunnable, 8000L);
        }
    };
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() { // from class: com.sunmi.aging.items.CameraC2Test.4
        @Override // android.hardware.Camera.ShutterCallback
        public void onShutter() {
            try {
                CameraC2Test.this.success();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Camera.PictureCallback rawPictureCallback = new Camera.PictureCallback() { // from class: com.sunmi.aging.items.CameraC2Test.5
        @Override // android.hardware.Camera.PictureCallback
        public void onPictureTaken(byte[] bArr, Camera camera) {
            try {
                CameraC2Test.this.success();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() { // from class: com.sunmi.aging.items.CameraC2Test.6
        @Override // android.hardware.Camera.PictureCallback
        public void onPictureTaken(byte[] bArr, Camera camera) {
            try {
                CameraC2Test.this.success();
                CameraC2Test.this.mHandler.post(new Runnable() { // from class: com.sunmi.aging.items.CameraC2Test.6.1
                    @Override // java.lang.Runnable
                    public void run() {
                        CameraC2Test.this.mCamera.startPreview();
                    }
                });
                CameraC2Test.this.count++;
                CameraC2Test.this.toast("已拍摄" + CameraC2Test.this.count + "次");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    */
/* loaded from: CameraC2Test$AutoFocusCallback.class *//*

    public final class AutoFocusCallback implements Camera.AutoFocusCallback {
        public AutoFocusCallback() {
        }

        @Override // android.hardware.Camera.AutoFocusCallback
        public void onAutoFocus(boolean z, Camera camera) {
            if (z) {
                CameraC2Test.this.takePicture();
            }
        }
    }

    private Rect calculateTapArea(float f, float f2, float f3, Camera.Size size) {
        Display defaultDisplay = ((WindowManager) getActivity().getSystemService("window")).getDefaultDisplay();
        int height = defaultDisplay.getHeight();
        int width = defaultDisplay.getWidth();
        int intValue = Float.valueOf(f3 * 200.0f).intValue();
        int i = (int) ((((-f) / width) * 2000.0f) + 1000.0f);
        int i2 = (int) (((f2 / height) * 2000.0f) - 1000.0f);
        int i3 = intValue / 2;
        int clamp = clamp(i2 - i3, -1000, 1000);
        int clamp2 = clamp(i - i3, -1000, 1000);
        RectF rectF = new RectF(clamp, clamp2, clamp + intValue, clamp2 + intValue);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private static int clamp(int i, int i2, int i3) {
        return i > i3 ? i3 : i < i2 ? i2 : i;
    }

    private void initDepth(UsbDevice usbDevice) {
        OpenNI.setLogAndroidOutput(true);
        int i = 0;
        OpenNI.setLogMinSeverity(0);
        OpenNI.initialize();
        List enumerateDevices = OpenNI.enumerateDevices();
        if (enumerateDevices.size() <= 0) {
            Toast.makeText(getActivity(), " openni enumerateDevices 0 devices", 1).show();
            return;
        }
        this.mDevice = null;
        while (true) {
            if (i >= enumerateDevices.size()) {
                break;
            } else if (((DeviceInfo) enumerateDevices.get(i)).getUsbProductId() == usbDevice.getProductId()) {
                this.mDevice = Device.open();
                break;
            } else {
                i++;
            }
        }
        if (this.mDevice == null) {
            Activity activity = getActivity();
            Toast.makeText(activity, " openni open devices failed: " + usbDevice.getDeviceName(), 1).show();
        }
    }

    private boolean needAutoFocus(Camera camera) {
        return false;
    }

    private void startCamera() {
        Camera camera = this.mCamera;
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPictureFormat(256);
                if (isFlashModeOn()) {
                    parameters.setFlashMode("on");
                }
                this.mCamera.setDisplayOrientation(getRotation());
                this.mCamera.setParameters(parameters);
                this.mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopCamera() {
        Camera camera = this.mCamera;
        if (camera != null) {
            try {
                camera.setPreviewCallback(null);
                this.mCamera.stopPreview();
                this.mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    */
/* JADX INFO: Access modifiers changed from: private *//*

    public void takePicture() {
        Camera camera = this.mCamera;
        if (camera != null) {
            try {
                camera.takePicture(this.mShutterCallback, this.rawPictureCallback, this.jpegCallback);
            } catch (Exception e) {
                fail(getString(R.string.capture_fail));
            }
        } else {
            fail(getString(R.string.camera_fail_open));
            postFail();
        }
    }

    void fail(Object obj) {
        toast(obj);
    }

    public int getCameraId() {
        return 0;
    }

    @Override // com.sunmi.aging.TestItemBase
    public String getKey() {
        return "cameraC2";
    }

    protected int getLayoutId() {
        return R.layout.camera_test;
    }

    public int getRotation() {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(getCameraId(), cameraInfo);
        return (360 - ((cameraInfo.orientation + rotation) % 360)) % 360;
    }

    @Override // com.sunmi.aging.TestItemBase
    public String getTestMessage() {
        return BuildConfig.FLAVOR;
    }

    @Override // com.sunmi.aging.TestItemBase
    public View getTestView(LayoutInflater layoutInflater) {
        View inflate = layoutInflater.inflate(getLayoutId(), (ViewGroup) null);
        this.mSurfaceView = (SurfaceView) inflate.findViewById(R.id.camera_surface);
        this.mSurfaceHolder = this.mSurfaceView.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(3);
        this.mGLView = (OpenGLView) inflate.findViewById(R.id.glView);
        this.mGLView.setVisibility(8);
        return inflate;
    }

    public boolean isFlashModeOn() {
        return false;
    }

    public void onDeviceOpenFailed(String str) {
        toast("深度摄像头打开失败");
    }

    public void onDeviceOpened(UsbDevice usbDevice) {
        initDepth(usbDevice);
        this.mStream = VideoStream.create(this.mDevice, SensorType.DEPTH);
        this.mVideoModes = this.mStream.getSensorInfo().getSupportedVideoModes();
        Log.d("aging", "zyb mVideoModes" + this.mVideoModes);
        if (usbDevice.getProductId() == 1549) {
            this.mWidth = 400;
            this.mHeight = 640;
            Camera camera = this.mCamera;
            if (camera != null) {
                try {
                    camera.setDisplayOrientation(90);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (VideoMode videoMode : this.mVideoModes) {
            int resolutionX = videoMode.getResolutionX();
            int resolutionY = videoMode.getResolutionY();
            int fps = videoMode.getFps();
            Log.d("aging", " support resolution: " + resolutionX + " x " + resolutionY + " fps: " + fps + ", (" + videoMode.getPixelFormat() + ")");
            if (resolutionX == this.mWidth && resolutionY == this.mHeight && videoMode.getPixelFormat() == PixelFormat.DEPTH_1_MM) {
                this.mStream.setVideoMode(videoMode);
                Log.v("aging", " setmode");
            }
        }
        this.mExit = false;
        startThread();
    }

    @Override // com.sunmi.aging.TestItemBase
    public void onStartTest() {
        this.mHandler.removeCallbacks(this.mRunnable);
        this.count = 0;
    }

    @Override // com.sunmi.aging.TestItemBase
    public void onStopTest() {
        this.mHandler.removeCallbacks(this.mRunnable);
        stopCamera();
        stopDepth();
    }

    @Override // com.sunmi.aging.TestItemBase, android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0) {
            return true;
        }
        startFocus(new Point((int) motionEvent.getX(), (int) motionEvent.getY()));
        return true;
    }

    public void startFocus(Point point) {
    }

    void startThread() {
        this.mInit_Ok = true;
        this.m_thread = new Thread() { // from class: com.sunmi.aging.items.CameraC2Test.7
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                ArrayList arrayList = new ArrayList();
                arrayList.add(CameraC2Test.this.mStream);
                CameraC2Test.this.mStream.start();
                while (!CameraC2Test.this.mExit) {
                    try {
                        OpenNI.waitForAnyStream(arrayList, 2000);
                        synchronized (CameraC2Test.this.m_sync) {
                            CameraC2Test.this.mGLView.update(CameraC2Test.this.mStream);
                        }
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.m_thread.start();
    }

    void stopDepth() {
        if (this.mInit_Ok) {
            this.mExit = true;
            Thread thread = this.m_thread;
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            VideoStream videoStream = this.mStream;
            if (videoStream != null) {
                videoStream.stop();
                this.mStream.destroy();
                this.mStream = null;
            }
            Device device = this.mDevice;
            if (device != null) {
                try {
                    device.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            OpenNIHelper openNIHelper = this.mOpenNIHelper;
            if (openNIHelper != null) {
                try {
                    openNIHelper.shutdown();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    void success() {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        startCamera();
        this.mHandler.postDelayed(this.mRunnable, 8000L);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (this.mCamera == null) {
            this.mCamera = Camera.open(getCameraId());
        }
        if (this.mCamera == null) {
            Log.d("xs", "33");
            fail(getString(R.string.camera_fail_open));
            postFail();
            return;
        }
        try {
            Log.d("xs", "44");
            this.mCamera.setPreviewDisplay(surfaceHolder);
            this.mCamera.setParameters(this.mCamera.getParameters());
            this.mCamera.startPreview();
        } catch (IOException e) {
            Log.d("xs", "55");
            this.mCamera.release();
            this.mCamera = null;
            fail(getString(R.string.camera_fail_open));
            postFail();
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopCamera();
    }

    public void toast(Object obj) {
        if (obj != null) {
            Activity activity = getActivity();
            Toast.makeText(activity, obj + BuildConfig.FLAVOR, 0).show();
        }
    }
}*/
