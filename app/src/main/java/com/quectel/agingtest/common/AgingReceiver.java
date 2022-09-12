package com.quectel.agingtest.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.quectel.agingtest.MainActivity;

public class AgingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("...");
        String caseId = CasesManager.getInstance(context).getCurrentCaseId();
        if (!TextUtils.isEmpty(caseId)) {
            startMainActivity(context);
            CasesManager.getInstance(context).startCases(caseId);
        }
    }

    private void startMainActivity(Context context) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setClass(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
