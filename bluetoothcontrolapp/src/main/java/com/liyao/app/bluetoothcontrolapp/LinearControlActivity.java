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
import android.text.method.ScrollingMovementMethod;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.liyao.app.bluetoothcontrolapp.action.ControlAction;
import com.liyao.app.bluetoothcontrolapp.bluetooth_interface.BluetoothCommunication;
import com.liyao.app.bluetoothcontrolapp.customcontrol.VerticalSeekBar;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.GlobalInfoVO;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.ProtocolOperationVO;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.RecvProtocolBase;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.recvprotocol.StateProtocol;
import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolManager;
import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolUtil;
import com.liyao.app.bluetoothcontrolapp.services.BlueToothControlService;
import com.liyao.app.bluetoothcontrolapp.vo.TransmitDataVO;

import java.util.ArrayList;

import static java.lang.Math.abs;

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
            serviceBinder.setBluetoothCommunication(bluetoothAddr,new LinearControlActivity.RecvCallback());
            ProtocolManager.serviceBinder = serviceBinder;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    //全局相关
    boolean isPause = false;
    LinearControlActivity.SendProtocol sendProtocol = new LinearControlActivity.SendProtocol();
    LinearControlActivity.RecvProtocol recvProtocol = new LinearControlActivity.RecvProtocol();
    ProtocolUtil pu = new ProtocolUtil();
    ProtocolOperationVO protocolOperationVO = new ProtocolOperationVO();
    LinearControlActivity.ControlMessageReceiver cmr = new LinearControlActivity.ControlMessageReceiver();



    SeekBar sb_offset = null;
    TextView tv_offset = null;
    VerticalSeekBar vsb_speed = null;
    TextView tv_speed = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_control);

        vsb_speed = (VerticalSeekBar)findViewById(R.id.vsb_speed);
        sb_offset = (SeekBar)findViewById(R.id.sb_offset);
        tv_speed = (TextView)findViewById(R.id.tv_speed);
        tv_offset = (TextView)findViewById(R.id.tv_offset);

        tv_offset.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_speed.setMovementMethod(ScrollingMovementMethod.getInstance());
        vsb_speed.setOnSeekBarChangeListener( new OnSeekBarChangeListenerSpeed());
        sb_offset.setOnSeekBarChangeListener(new OnSeekBarChangeListenerDir());


        Intent lastIntent = getIntent();//准备接收参数
        //绑定service
        bluetoothAddr = lastIntent.getStringExtra("DATA");
        //绑定接收数据服务
        Intent intent = new Intent(this, BlueToothControlService.class);
        intent.putExtra("DATA", lastIntent.getStringExtra("DATA"));
        bindService(intent, con, Service.BIND_AUTO_CREATE);

        //启动协议框架
        if(ProtocolManager.protocolTransfer.getState() == Thread.State.NEW)
            ProtocolManager.protocolTransfer.start();//启动协议框架线程
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

    public class RecvCallback implements BluetoothCommunication {
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
    }



    public class SendProtocol extends Thread{
        @Override
        public void run() {
            super.run();
            int speed = 0;
            int offset = 0;
            while(!this.isInterrupted()) {
                if(speed == GlobalInfoVO.speed && offset == GlobalInfoVO.offset) {
                    SystemClock.sleep(100);
                    continue;
                }
                ControlAction.controlDirection(GlobalInfoVO.speed, GlobalInfoVO.offset);
                speed = GlobalInfoVO.speed;
                offset = GlobalInfoVO.offset;
                SystemClock.sleep(200);
            }
        }
    }

    public class RecvProtocol extends Thread{
        @Override
        public void run() {
            super.run();
            int ser = -1;
            while(!this.isInterrupted()) {
                ProtocolManager.recvProtocolList_Lock.lock();
                try{
                    for(RecvProtocolBase rpb:ProtocolManager.recvProtocolList){
                        switch (rpb.getType() & 0xff){
                            case ProtocolUtil.STATE_PROTOCOL_RECV_TYPE:
                                StateProtocol sp = (StateProtocol)rpb;
                                GlobalInfoVO.left_dir       = sp.left_dir;
                                GlobalInfoVO.left_speed     = sp.left_speed_l;
                                GlobalInfoVO.right_dir      = sp.right_dir;
                                GlobalInfoVO.right_speed    = sp.right_speed_l;
                                if(ser == -1) {
                                    ser = sp.getSerial();
                                }else{
                                    ser++;
                                    if(ser == 256)
                                        ser = 0;
                                    if((sp.getSerial()&0xff) != (ser&0xff)){
                                        GlobalInfoVO.serialState = "异常";
                                        //Log.w("err","序号丢失");
                                    }
                                }
                                break;
                        }
                    }
                    ProtocolManager.recvProtocolList = new ArrayList<>();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    ProtocolManager.recvProtocolList_Lock.unlock();
                }
                SystemClock.sleep(100);
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
                sendProtocol.interrupt();//终止发送控制协议线程
                recvProtocol.interrupt();//终止接收控制协议线程
            }
        }
    }
    private class OnSeekBarChangeListenerSpeed implements VerticalSeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(VerticalSeekBar VerticalSeekBar, int progress, boolean fromUser) {
            int speed = progress - 100;
            tv_speed.setText(String.valueOf(speed));
            GlobalInfoVO.speed = speed;
        }

        @Override
        public void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar) {

        }

        @Override
        public void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar) {
            vsb_speed.setProgress(100);
        }
    }
    private class OnSeekBarChangeListenerDir implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int offset = progress - 100;
            tv_offset.setText(String.valueOf(offset));
            GlobalInfoVO.offset = offset;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //Toast.makeText(LinearControlActivity.this, String.valueOf(sb_offset.getProgress()), Toast.LENGTH_SHORT).show();
            sb_offset.setProgress(100);
        }
    }
}
