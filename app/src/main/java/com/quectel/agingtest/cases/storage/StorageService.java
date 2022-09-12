package com.quectel.agingtest.cases.storage;


import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import com.quectel.agingtest.common.BaseService;
import com.quectel.agingtest.common.ItemCases;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.Map;

/* loaded from: Storage.class */
public class StorageService extends BaseService {
    private static final float MB = 1048576.0f;
    private static String testFilePath = "/storage/emulated/0/sunmi_stroage_test_file";
    private long count;
    private FileWriter fw;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() { // from class: com.sunmi.aging.items.Storage.1
        @Override // java.lang.Runnable
        public void run() {
            StatFs statFs = new StatFs(testFilePath);
            long blockSizeLong = statFs.getBlockSizeLong();
            long blockCountLong = statFs.getBlockCountLong();
            long availableBlocksLong = statFs.getAvailableBlocksLong();
            Log.d("aging", "storage:blockSize = " + blockSizeLong + ";totalBlocks = " + blockCountLong + ";availables = " + availableBlocksLong);
            float f = (float) (availableBlocksLong * blockSizeLong);
            if (f < 2.097152E8f) {
                if (StorageService.this.tv != null) {
                    StorageService.this.tv.setText("存储空间已满，正在删除文件...");
                }
                try {
                    StorageService.this.fw.close();
                    StorageService.deleteTestFile();
                    StorageService.this.fw = new FileWriter(new File(StorageService.testFilePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                StorageService.this.mHandler.postDelayed(StorageService.this.mRunnable, 2000L);
                StorageService.this.count = 0L;
                return;
            }
            char[] cArr = new char[409600];
            Array.setChar(cArr, 0, (char) Math.random());
            if (StorageService.this.fw != null) {
                try {
                    StorageService.this.fw.write(cArr, 0, cArr.length);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                StorageService.this.count += cArr.length;
            }
            String str = (((("正在写入文件，已写入" + (((float) StorageService.this.count) / StorageService.MB) + "MB\n") + "存储空间不足200M时将删除文件再次写入\n\n\n") + "设备存储使用情况如下：\n") + "总容量:" + (((float) (blockSizeLong * blockCountLong)) / StorageService.MB) + "MB\n") + "可用:" + (f / StorageService.MB) + "MB";
            if (StorageService.this.tv != null) {
                StorageService.this.tv.setText(str);
            }
            StorageService.this.mHandler.postDelayed(StorageService.this.mRunnable, 100L);
        }
    };
    TextView tv;

    /* JADX INFO: Access modifiers changed from: private */
    public static void deleteTestFile() {
        File file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /*@Override // com.sunmi.aging.TestItemBase
    public String getKey() {
        return "storage";
    }

    @Override // com.sunmi.aging.TestItemBase
    public String getTestMessage() {
        return "";
    }*/

    /*@Override // com.sunmi.aging.TestItemBase
    public View getTestView(LayoutInflater layoutInflater) {
        View inflate = layoutInflater.inflate(R.layout.test_msg, (ViewGroup) null);
        this.tv = (TextView) inflate.findViewById(R.id.test_msg);
        return inflate;
    }

    @Override // com.sunmi.aging.TestItemBase
    public void onStartTest() {
        deleteTestFile();
        this.count = 0L;
        try {
            this.fw = new FileWriter(new File(testFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mHandler.postDelayed(this.mRunnable, 100L);
    }*/

    /*@Override // com.sunmi.aging.TestItemBase
    public void onStopTest() {
        this.mHandler.removeCallbacks(this.mRunnable);
        FileWriter fileWriter = this.fw;
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        deleteTestFile();
    }*/

    @Override
    public void beforeRun(ItemCases cases, Map<String, String> map) {
    }

    @Override
    public boolean runCases(int time) {
        this.count = 0L;
        try { this.fw = new FileWriter(testFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mHandler.postDelayed(this.mRunnable, 100L);
        return true;
    }
}
