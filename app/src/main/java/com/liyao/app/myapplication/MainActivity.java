package com.liyao.app.myapplication;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(!thread.isInterrupted()){
                Log.v("123",thread.getState().toString());
                SystemClock.sleep(500);
            }
        }
    });

    public void test_onClick(View v){
        Toast.makeText(this, thread.getState().toString(), Toast.LENGTH_SHORT).show();
        if(thread.getState() == Thread.State.TERMINATED)
           thread.start();
//        else if(thread.getState() == Thread.State.RUNNABLE)
            thread.interrupt();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建一个自己继承于View的对象
        final VirtualJoystick vj_joystick = (VirtualJoystick)this.findViewById(R.id.vj_joystick);
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

                TextView tv_show = (TextView)findViewById(R.id.tv_show);
                tv_show.setText(String.valueOf(Math.atan2(event.getX()- VirtualJoystick.OFFSETX,event.getY()- VirtualJoystick.OFFSETY)*180/Math.PI));
                //tv_show.setText(String.valueOf(Math.atan((event.getY()- VirtualJoystick.OFFSETY)/(event.getX()- VirtualJoystick.OFFSETX))*180/Math.PI));
                //返回true表明算是方法已经处理该事件
                return true;
            }

        });


    //thread.start();

//
//        byte[] aa = new byte[]{(byte)0xb0,(byte)0xa1};//啊
//        byte senddata[] = new byte[0];
//        try {
//            senddata =  "啊".getBytes("UTF-8") ;
//            senddata =  "啊".getBytes("GBK") ;
//            senddata =  "啊".getBytes("ISO-8859-1") ;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            Toast.makeText(this, new String(senddata,"GBK"), Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //RelativeLayout =

        //        this.findViewById(R);
    }
}
