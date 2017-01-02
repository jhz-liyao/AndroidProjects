package com.liyao.app.bluetoothcontrolapp;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.liyao.app.bluetoothcontrolapp.action.ControlAction;
import com.liyao.app.bluetoothcontrolapp.customcontrol.VerticalSeekBar;
import com.liyao.app.bluetoothcontrolapp.protocol.Protocol;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.GlobalInfoVO;
import com.liyao.app.bluetoothcontrolapp.protocolframe.ProtocolManager;
import com.liyao.app.bluetoothcontrolapp.protocolframe.RecvProtocolBase;
import com.liyao.app.bluetoothcontrolapp.services.BlueToothControlService;


public class LinearControlActivity extends Activity {
    public static final String CONNECTION_SUCCESS = "com.liyao.app.bluetoothcontrolapp.receiver.connection_success";
    public static final String CONNECTION_FAIL    = "com.liyao.app.bluetoothcontrolapp.receiver.connection_fail";
    public static final String DISCONNECTION      = "com.liyao.app.bluetoothcontrolapp.receiver.disconnection";
    ScrollView  sv_scroll = null;
    //蓝牙连接初始化
    String bluetoothAddr = null;
    BlueToothControlService.ServiceBinder serviceBinder = null;
    ServiceConnection con =  new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = (BlueToothControlService.ServiceBinder)service;
            serviceBinder.setBluetoothCommunication(bluetoothAddr,new com.liyao.app.bluetoothcontrolapp.protocolframe.ProtocolManager.SocketCom());
            //ProtocolManager.serviceBinder = serviceBinder;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    //全局相关
    LinearControlActivity.SendProtocol sendProtocol = new LinearControlActivity.SendProtocol();
    LinearControlActivity.RecvProtocol recvProtocol = new LinearControlActivity.RecvProtocol();
    LinearControlActivity.ControlMessageReceiver cmr = new LinearControlActivity.ControlMessageReceiver();



    SeekBar sb_offset = null;
    TextView tv_offset = null;
    VerticalSeekBar vsb_speed = null;
    TextView tv_speed = null;

    SeekBar sb_rudder_x = null;
    TextView tv_rudder_x = null;
    VerticalSeekBar vsb_rudder_y = null;
    TextView tv_rudder_y = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_control);


        vsb_speed = (VerticalSeekBar)findViewById(R.id.vsb_speed);
        sb_offset = (SeekBar)findViewById(R.id.sb_offset);
        tv_speed = (TextView)findViewById(R.id.tv_speed);
        tv_offset = (TextView)findViewById(R.id.tv_offset);

        vsb_rudder_y = (VerticalSeekBar)findViewById(R.id.vsb_rudder_y);
        sb_rudder_x = (SeekBar)findViewById(R.id.sb_rudder_x);
        tv_rudder_y = (TextView)findViewById(R.id.tv_rudder_y);
        tv_rudder_x = (TextView)findViewById(R.id.tv_rudder_x);

        //        tv_offset.setMovementMethod(ScrollingMovementMethod.getInstance());
//        tv_speed.setMovementMethod(ScrollingMovementMethod.getInstance());
        vsb_speed.setOnSeekBarChangeListener( new OnSeekBarChangeListenerSpeed());
        sb_offset.setOnSeekBarChangeListener(new OnSeekBarChangeListenerOffset());
        vsb_rudder_y.setOnSeekBarChangeListener( new OnSeekBarChangeListenerRudderY());
        sb_rudder_x.setOnSeekBarChangeListener(new OnSeekBarChangeListenerRudderX());


        Intent lastIntent = getIntent();//准备接收参数
        //绑定service
        bluetoothAddr = lastIntent.getStringExtra("DATA");
        //绑定接收数据服务
        Intent intent = new Intent(this, BlueToothControlService.class);
        intent.putExtra("DATA", lastIntent.getStringExtra("DATA"));
        bindService(intent, con, Service.BIND_AUTO_CREATE);

        //启动协议框架
//        if(ProtocolManager.protocolTransfer.getState() == Thread.State.NEW)
//            ProtocolManager.protocolTransfer.start();//启动协议框架线程
        Protocol.Init();
        //赋值滚动条
        sv_scroll = (ScrollView)this.findViewById(R.id.sv_scroll);

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(CONNECTION_SUCCESS);
        iFilter.addAction(CONNECTION_FAIL);
        iFilter.addAction(DISCONNECTION);
        registerReceiver(cmr, iFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(cmr);
        try{
            unbindService(con);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

   /* public class RecvCallback implements BluetoothCommunication {
        @Override
        public void receiveCallback(TransmitDataVO vo) {
            if(isPause) return;
            protocolOperationVO.transmitDataVO = vo;
            ProtocolManager.fetchProtocol(protocolOperationVO, pu);//将收到的数据封装成协议
            final String data = protocolOperationVO.convertCN() ;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((TextView)findViewById(R.id.tv_win_content)).setText(data);
                        sv_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }*/



    public class SendProtocol extends Thread{
        public boolean exit = false;
        public void cancel(){
            exit = true;
            this.interrupt();
        }
        @Override
        public void run() {
            super.run();
            int speed = 0;
            int offset = 0;
            int rudder_x = 0;
            int rudder_y = 0;
            while(!this.isInterrupted() && !exit) {
                if(speed == GlobalInfoVO.speed && offset == GlobalInfoVO.offset) {
                    //SystemClock.sleep(100);
                }else{
                    ControlAction.controlDirection(GlobalInfoVO.speed, GlobalInfoVO.offset);
                    speed = GlobalInfoVO.speed;
                    offset = GlobalInfoVO.offset;
                    SystemClock.sleep(200);
                }
                if(rudder_x == GlobalInfoVO.rudder_x && rudder_y == GlobalInfoVO.rudder_Y) {
                    //SystemClock.sleep(100);
                }else{
                    ControlAction.rudderControl(GlobalInfoVO.rudder_x, GlobalInfoVO.rudder_Y);
                    rudder_x = GlobalInfoVO.rudder_x;
                    rudder_y = GlobalInfoVO.rudder_Y;
                    SystemClock.sleep(200);
                }
                SystemClock.sleep(100);
            }
        }
    }
    public void btn_rudder_homing_onClick(View v){
        sb_rudder_x.setProgress(90);
        vsb_rudder_y.setProgress(90);
    }
    public class RecvProtocol extends Thread {
        public boolean exit = false;
        public void cancel(){
            exit = true;
            this.interrupt();
        }
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(!this.isInterrupted() && !exit) {
                try {
                    RecvProtocolBase rpb =ProtocolManager.fetchProtocol();
                    rpb.handle(LinearControlActivity.this);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    public class ControlMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(CONNECTION_SUCCESS.equals(action)){
                Toast.makeText(LinearControlActivity.this, "蓝牙客户端连接成功", Toast.LENGTH_SHORT).show();
                sendProtocol.start();//启动发送控制协议线程
                recvProtocol.start();//启动接收控制协议线程
            }else  if(CONNECTION_FAIL.equals(action)){
                Toast.makeText(LinearControlActivity.this, "蓝牙客户端连接失败", Toast.LENGTH_SHORT).show();
            }else if(DISCONNECTION.equals(action)){
                Toast.makeText(LinearControlActivity.this, "蓝牙客户端掉线", Toast.LENGTH_SHORT).show();
                sendProtocol.cancel();//终止发送控制协议线程
                recvProtocol.cancel();//终止接收控制协议线程
            }
        }
    }
    /*private class OnSeekBarChangeListenerSpeed implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            int speed = progress - seekBar.getMax()/2;
            tv_speed.setText(String.valueOf(speed));
            GlobalInfoVO.speed = speed;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            vsb_speed.setProgress(vsb_speed.getMax()/2);
        }
    }*/
    private class OnSeekBarChangeListenerSpeed implements VerticalSeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(VerticalSeekBar VerticalSeekBar, int progress, boolean fromUser) {
            int speed = progress - VerticalSeekBar.getMax()/2;
            tv_speed.setText(String.valueOf(speed));
            GlobalInfoVO.speed = speed;
        }

        @Override
        public void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar) {

        }

        @Override
        public void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar) {
            VerticalSeekBar.setProgress(VerticalSeekBar.getMax()/2);
        }
    }
    private class OnSeekBarChangeListenerOffset implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int offset = progress - seekBar.getMax()/2;
            tv_offset.setText(String.valueOf(offset));
            GlobalInfoVO.offset = offset;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //Toast.makeText(LinearControlActivity.this, String.valueOf(sb_offset.getProgress()), Toast.LENGTH_SHORT).show();
            seekBar.setProgress(seekBar.getMax()/2);
        }
    }
    private class OnSeekBarChangeListenerRudderY implements VerticalSeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(VerticalSeekBar VerticalSeekBar, int progress, boolean fromUser) {
            int RudderY = progress;
            tv_rudder_y.setText(String.valueOf(RudderY));
            GlobalInfoVO.rudder_Y = RudderY;
        }

        @Override
        public void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar) {

        }

        @Override
        public void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar) {

        }
    }

    /*private class OnSeekBarChangeListenerRudderY implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            int RudderY = progress;
            tv_rudder_y.setText(String.valueOf(RudderY));
            GlobalInfoVO.rudder_Y = RudderY;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }*/
    private class OnSeekBarChangeListenerRudderX implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int RudderX = 180 - progress;
            tv_rudder_x.setText(String.valueOf(RudderX));
            GlobalInfoVO.rudder_x = RudderX;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
