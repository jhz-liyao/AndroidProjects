package com.liyao.app.phonebodyguard.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by liyao on 2016/5/16.
 */
public class MainService extends Service {
    public String data = "获取数据";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("$$$$", "MainService已启动");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("$$$$", "MainService.onBind");
        return new MainServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("$$$$", "MainService.onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("$$$$", "MainService.onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("$$$$", "MainService已销毁");
    }

    public class MainServiceBinder extends Binder {
        public void getData(){
            Toast.makeText( MainService.this, "模拟下载2秒钟,开始下载...", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Toast.makeText( MainService.this, "下载结束...", Toast.LENGTH_SHORT).show();
        }
    }
}
