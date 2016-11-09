package com.liyao.app.bluetoothcontrolapp;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.liyao.app.bluetoothcontrolapp.bluetoothinterface.BluetoothCommunication;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.ProtocolOperationVO;
import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolManager;
import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolUtil;
import com.liyao.app.bluetoothcontrolapp.services.BlueToothControlService;
import com.liyao.app.bluetoothcontrolapp.vo.TransmitDataVO;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by liyao on 2016/5/20.
 */
public class ShowWinActivity extends Activity  {
    ScrollView  sv_scroll = null;
    TextView tv_win_content = null;
    Button btn_pause = null;
    Intent lastIntent = null;
    String bluetoothAddr = null;
    byte[] contentByte = new byte[0];
    boolean isPause = false;
    ProtocolOperationVO protocolOperationVO = new ProtocolOperationVO();
    ProtocolUtil pu = new ProtocolUtil();
    BlueToothControlService.ServiceBinder serviceBinder = null;
    //RecvReceiver recvReceiver = new RecvReceiver();
    //IntentFilter recvIFilter = new IntentFilter();
    ServiceConnection con =  new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = (BlueToothControlService.ServiceBinder)service;
            serviceBinder.setBluetoothCommunication(bluetoothAddr,new RecvCallback());
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private Runnable mScrollToBottom = new Runnable()
    {
        @Override
        public void run()
        {
            SystemClock.sleep(50);
            sv_scroll.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_win_activity);
        lastIntent = getIntent();
        bluetoothAddr = lastIntent.getStringExtra("DATA");
        //绑定接收数据服务
        Intent intent = new Intent(this, BlueToothControlService.class);
        intent.putExtra("DATA", lastIntent.getStringExtra("DATA"));
        bindService(intent, con, Service.BIND_AUTO_CREATE);
        //找到相关控件
        sv_scroll = (ScrollView)this.findViewById(R.id.sv_scroll);
        tv_win_content = (TextView)this.findViewById(R.id.tv_win_content);
        btn_pause = (Button)findViewById(R.id.btn_pause);
    }

    public  void btn_clean_onClick(View v){
        contentByte = new byte[0];
        tv_win_content.setText("");
        serviceBinder.sendBluetoothData(new TransmitDataVO(new byte[]{(byte)0xb0,(byte)0xa1},2));
    }

    public void btn_pause_onClick(View v){
        if("暂停".equals(btn_pause.getText())){
            isPause = true;
            btn_pause.setText("开始");
        }else{
            isPause = false;
            btn_pause.setText("暂停");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unbindService(con);
            //unregisterReceiver(recvReceiver);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class RecvCallback implements BluetoothCommunication{
        @Override
        public void receiveCallback(TransmitDataVO vo) {
            if(isPause) return;
            //contentByte = vo.appendByteArray(contentByte,vo.getData(),4096);
//            final String data = vo.convertCN(contentByte);
            protocolOperationVO.transmitDataVO = vo;
            ProtocolManager.fetchProtocol(protocolOperationVO, pu);//将收到的数据封装成协议
            final String data = protocolOperationVO.convertCN() ;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((TextView)findViewById(R.id.tv_win_content)).setText(data);
                        //sv_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mScrollToBottom.run();
        }
    }
}
