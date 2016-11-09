package com.liyao.app.phonebodyguard.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.liyao.app.phonebodyguard.MainActivity;
import com.liyao.app.phonebodyguard.service.MainService;

/**
 * Created by liyao on 2016/5/18.
 */
public class PowerBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent intent1 = new Intent(context, MainActivity.class);
//        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent1);
        Intent mainServiceIntent = new Intent(context, MainService.class);
        context.startService(mainServiceIntent);
    }
}
