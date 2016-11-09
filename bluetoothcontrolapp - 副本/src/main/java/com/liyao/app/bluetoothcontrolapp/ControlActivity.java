package com.liyao.app.bluetoothcontrolapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.liyao.app.bluetoothcontrolapp.customcontrol.VirtualJoystick;


public class ControlActivity extends Activity {
    VirtualJoystick vj_joystick = null;
    Handler joystickFlush = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            TextView tv_show = (TextView) findViewById(R.id.tv_show);
            tv_show.setText("角度："+String.valueOf((int)vj_joystick.joystickInfoVO.getAngle()) + "   深度：" + String.valueOf((int)vj_joystick.joystickInfoVO.getDeepness())
                    + "   X:" + String.valueOf(vj_joystick.joystickInfoVO.getPosX()) + "   Y:" + String.valueOf(vj_joystick.joystickInfoVO.getPosY()));
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity);
        //创建一个自己继承于View的对象
        vj_joystick = (VirtualJoystick)this.findViewById(R.id.vj_joystick);
        vj_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        vj_joystick.setLocation(VirtualJoystick.OFFSETX,VirtualJoystick.OFFSETY);
                        break;
                    default:
                        vj_joystick.setLocation(event.getX(),event.getY());
                        break;
                }
                joystickFlush.obtainMessage(0).sendToTarget();
                return true;
            }
        });



    }

    public void test_onClick(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("我是标题");
        builder.setMessage("我是Message");
        builder.show();

    }


}
