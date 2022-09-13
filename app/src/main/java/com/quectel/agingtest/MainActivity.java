package com.quectel.agingtest;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quectel.agingtest.common.CaseAdapter;
import com.quectel.agingtest.common.CasesManager;
import com.quectel.agingtest.common.DialogUtils;
import com.quectel.agingtest.config.ConfigActivity;


public class MainActivity extends Activity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CaseAdapter caseAdapter;

    private Button btnStart;
    private Handler handler;
    CasesManager casesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        recyclerView = findViewById(R.id.recycler_view);
        btnStart = findViewById(R.id.btn_start);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        casesManager = CasesManager.getInstance(this);
        caseAdapter = new CaseAdapter(this, casesManager.getCases(), casesManager.isRunning(), casesManager.getSelectedCases());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(caseAdapter);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case CasesManager.MSG_RUN:
                        updateUI();
                        break;
                    case CasesManager.MSG_STOP:
                        resetUI();
                        showResultDialog();
                        break;
                    case CasesManager.MSG_UPDATE_ITEM:
                        String caseId = (String) msg.obj;
                        if (!TextUtils.isEmpty(caseId)) {
                            caseAdapter.updateSateCases(caseId, "(" + msg.arg1 + "/" + msg.arg2 + ")");
                        }
                        break;
                }
            }
        };

        casesManager.setHandler(handler);

        btnStart.setOnClickListener(view -> {
            if (casesManager.isRunning()) {
                // 强行停止
                showStopDialog();
            } else {
                // 开始
                int size = caseAdapter.getCasesSelected().size();
                if (size > 0) {
                    boolean res = casesManager.startTest(caseAdapter.getCasesSelected());
                    caseAdapter.updateStateRun(res);
                } else {
                    Toast.makeText(this, "选择测试项目", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };
    private void checkPermission(){
        int storageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (storageResult != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissions,9);
        }
        int audioResult = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (audioResult != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, permissions,9);
        }
    }

    private void updateUI(){
        updateBtnText(casesManager.isRunning());
    }

    private void resetUI() {
        caseAdapter.updateReset();
        updateBtnText(casesManager.isRunning());
    }

    private void showResultDialog() {
        String s = casesManager.getResultStr();
        if (!TextUtils.isEmpty(s)) {
            DialogUtils.showPrompt(this, s, "我知道了");
        }
    }

    private void showStopDialog() {
        DialogUtils.showAlert(this, "确定停止当前的测试任务？", "停止后，会清除当前信息不保留任何记录", "确定", (DialogInterface.OnClickListener) (dialogInterface, i) -> {
            casesManager.stopTest();
        }, "取消", null);
    }

    private void updateBtnText(boolean run) {
        btnStart.setText(run ? R.string.btn_stop_test : R.string.btn_start_test);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_setting) {
            if (!casesManager.isRunning()) {
                startActivity(new Intent(this, ConfigActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}