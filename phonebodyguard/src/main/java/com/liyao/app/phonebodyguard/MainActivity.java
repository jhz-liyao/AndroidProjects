package com.liyao.app.phonebodyguard;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liyao.app.phonebodyguard.contentprovider.SmsOperation;
import com.liyao.app.phonebodyguard.homepage_event.MenuItemEvent;
import com.liyao.app.phonebodyguard.service.MainService;

import java.io.File;

public class MainActivity extends Activity {
    private SmsOperation smsOperation;
    private Intent mainServiceIntent;
    private MainService.MainServiceBinder mainServiceBinder;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mainServiceBinder = (MainService.MainServiceBinder)service;
        }
    };

    public void test(View v){
        mainServiceBinder.getData();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //--设置动画 获取SD卡空间
        final ImageView iv_outflash = (ImageView)(this.findViewById(R.id.iv_outflash));
        final ImageView iv_inflash = (ImageView)(this.findViewById(R.id.iv_inflash));
        final TextView tv_top_title = (TextView)this.findViewById(R.id.tv_top_title);
        final TextView tv_top_detail = (TextView)this.findViewById(R.id.tv_top_detail);
        RotateAnimation rotateAnimation = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(3);
        iv_outflash.startAnimation(rotateAnimation);

        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(3);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_inflash.setVisibility(View.GONE);

                //--获取SD卡剩余空间
                File file = Environment.getExternalStorageDirectory();
                tv_top_detail.setText( Formatter.formatFileSize(MainActivity.this, file.getFreeSpace()));
                //--展示控件
                tv_top_title.setVisibility(View.VISIBLE);
                tv_top_detail.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "动画完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv_inflash.startAnimation(rotateAnimation);
        //--绑定Service
        mainServiceIntent = new Intent(this, MainService.class);
        this.startService(mainServiceIntent);
        this.bindService(mainServiceIntent, connection, Service.BIND_AUTO_CREATE);
        //--设置下部按钮事件
        this.findViewById(R.id.homepage_rl_menu_left_1 ).setOnTouchListener(new MenuItemEvent(this));
        this.findViewById(R.id.homepage_rl_menu_right_1).setOnTouchListener(new MenuItemEvent(this));
        this.findViewById(R.id.homepage_rl_menu_left_2 ).setOnTouchListener(new MenuItemEvent(this));
        this.findViewById(R.id.homepage_rl_menu_right_2).setOnTouchListener(new MenuItemEvent(this));
        this.findViewById(R.id.homepage_rl_menu_left_3 ).setOnTouchListener(new MenuItemEvent(this));
        this.findViewById(R.id.homepage_rl_menu_right_3).setOnTouchListener(new MenuItemEvent(this));
        //更新当前备份短信条数
        smsOperation = new SmsOperation(this);
        smsOperation.flushSmsBakCount();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(connection);
//        this.stopService(mainService);
    }

//    public class MainServiceBindOperation1 implements ServiceConnection {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.v("$$$$", "bind_onServiceConnected");
//            mainServiceBinder = (MainService.MainServiceBinder)service;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.v("$$$$", "bind_onServiceDisconnected");
//
//        }
//    }
}
