package com.liyao.app.bluetoothcontrolapp;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.liyao.app.bluetoothcontrolapp.action.ControlAction;
import com.liyao.app.bluetoothcontrolapp.bluetoothinterface.BluetoothCommunication;
import com.liyao.app.bluetoothcontrolapp.customcontrol.VirtualJoystick;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.GlobalInfoVO;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.ProtocolOperationVO;
import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolUtil;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.RecvProtocolBase;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.recvprotocol.StateProtocol;
import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolManager;
import com.liyao.app.bluetoothcontrolapp.services.BlueToothControlService;
import com.liyao.app.bluetoothcontrolapp.vo.TransmitDataVO;

import java.util.ArrayList;


public class ControlActivity extends Activity {
    //textview
    ScrollView  sv_scroll = null;
    //摇杆初始化
    VirtualJoystick vj_joystick = null;
    Handler joystickFlush = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            TextView tv_show = (TextView) findViewById(R.id.tv_show);
            GlobalInfoVO.angle = (int)vj_joystick.joystickInfoVO.getAngle();
            GlobalInfoVO.deepness = (int)vj_joystick.joystickInfoVO.getDeepness();
            tv_show.setText("角度："+String.valueOf((int)vj_joystick.joystickInfoVO.getAngle()) + "   深度：" + String.valueOf((int)vj_joystick.joystickInfoVO.getDeepness())
                    + "   X:" + String.valueOf(vj_joystick.joystickInfoVO.getPosX()) + "   Y:" + String.valueOf(vj_joystick.joystickInfoVO.getPosY())
                    + "\r\n左轮："+GlobalInfoVO.getLeft_dir() + "  速度："+GlobalInfoVO.left_speed+ "  右轮：" + GlobalInfoVO.getRight_dir() + "  速度：" + GlobalInfoVO.right_speed


            );
            return false;
        }
    });

    //蓝牙连接初始化
    byte[] contentByte = new byte[0];
    String bluetoothAddr = null;
    Intent lastIntent = null;
    BlueToothControlService.ServiceBinder serviceBinder = null;
    ServiceConnection con =  new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = (BlueToothControlService.ServiceBinder)service;
            serviceBinder.setBluetoothCommunication(bluetoothAddr,new RecvCallback());
            ProtocolManager.serviceBinder = serviceBinder;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    //全局相关
    boolean isPause = false;
    SendProtocol sendProtocol = new SendProtocol();
    RecvProtocol recvProtocol = new RecvProtocol();
    ProtocolUtil pu = new ProtocolUtil();
    ProtocolOperationVO protocolOperationVO = new ProtocolOperationVO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity);

        Intent lastIntent = getIntent();//准备接收参数
        Toast.makeText(this, lastIntent.getStringExtra("DATA"),Toast.LENGTH_SHORT).show();
        vj_joystick = (VirtualJoystick)this.findViewById(R.id.vj_joystick);//得到摇杆对象
        vj_joystick.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                 if(event.getAction() == MotionEvent.ACTION_UP)
                     vj_joystick.setLocation(VirtualJoystick.OFFSETX,VirtualJoystick.OFFSETY);
                 else
                     vj_joystick.setLocation(event.getX(),event.getY());
                joystickFlush.obtainMessage(0).sendToTarget();
                return true;
            }
        });

        bluetoothAddr = lastIntent.getStringExtra("DATA");
        //绑定接收数据服务
        Intent intent = new Intent(this, BlueToothControlService.class);
        intent.putExtra("DATA", lastIntent.getStringExtra("DATA"));
        bindService(intent, con, Service.BIND_AUTO_CREATE);

        sendProtocol.start();//启动发送控制协议线程
        recvProtocol.start();//启动接收控制协议线程
        if(ProtocolManager.protocolTransfer.getState() == Thread.State.NEW)
            ProtocolManager.protocolTransfer.start();//启动协议框架线程

        sv_scroll = (ScrollView)this.findViewById(R.id.sv_scroll);
    }

    public void test_onClick(View v){
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("我是标题");
        builder.setMessage("我是Message");
        builder.show(); */
        TransmitDataVO vo = new TransmitDataVO();
        vo.setData(new byte[]{(byte)0xFD,(byte)0x01,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x26,(byte)0x2A,(byte)0xF8,(byte)0x23,(byte)0xB9,(byte)0xFE,(byte)0xB9,(byte)0xFE,(byte)0x0D,(byte)0x0A,(byte)0x24});
        vo.setLen(26);

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

    public class RecvCallback implements BluetoothCommunication {
        @Override
        public void receiveCallback(TransmitDataVO vo) {
            if(isPause) return;
//            contentByte = vo.appendByteArray(contentByte,vo.getData(),1000);
//            contentByte = ProtocolManager.fetchProtocol(contentByte, pu);//将收到的数据封装成协议
//            vo.setData(new byte[]{(byte)0xFD,(byte)0x01,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x26,(byte)0x2A,(byte)0xF8,(byte)0xB9,(byte)0xFE,(byte)0xB9,(byte)0xFE,(byte)0x0D,(byte)0x0A});
//            vo.setLen(24);
            //contentByte = ;

            //contentByte = vo.appendByteArray(contentByte,vo.getData(),10240);
           // contentByte = ProtocolManager.fetchProtocol(contentByte, pu);//将收到的数据封装成协议
            //final String data = vo.convertCN() ;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendProtocol.interrupt();//终止发送控制协议线程
        recvProtocol.interrupt();//终止接收控制协议线程
        try{
            unbindService(con);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class SendProtocol extends Thread{
        @Override
        public void run() {
            super.run();
            while(!this.isInterrupted()) {
                ControlAction.controlDirection(GlobalInfoVO.angle, GlobalInfoVO.deepness);
                SystemClock.sleep(100);
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
                                        Log.v("err","序号丢失");
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

}
