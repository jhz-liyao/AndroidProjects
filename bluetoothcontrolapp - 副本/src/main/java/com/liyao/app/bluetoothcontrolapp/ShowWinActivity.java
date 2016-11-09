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
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.liyao.app.bluetoothcontrolapp.services.BlueToothControlService;
import com.liyao.app.bluetoothcontrolapp.vo.RecvDataVO;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;


/**
 * Created by liyao on 2016/5/20.
 */
public class ShowWinActivity extends Activity {
    ScrollView  sv_scroll = null;
    TextView tv_win_content = null;
    Button btn_pause = null;
    Intent lastIntent = null;
    String bluetoothAddr = null;
    RecvReceiver recvReceiver = new RecvReceiver();
    IntentFilter recvIFilter = new IntentFilter();
    ServiceConnection con =  new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private Runnable mScrollToBottom = new Runnable()
    {  @Override
        public void run()
        {
            sv_scroll.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_win_activity);
        lastIntent = getIntent();
        bluetoothAddr = lastIntent.getStringExtra("DATA");
        //接收服务数据接收者
        recvReceiver = new RecvReceiver();
        recvIFilter = new IntentFilter();
        recvIFilter.addAction(BluetoothClient.ACTION_BLUETOOTH_RECV);
        registerReceiver(recvReceiver, recvIFilter);
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
        contentByte = new byte[RECVBUFFSIZE];
        contentIndex = 0;
        ((TextView)this.findViewById(R.id.tv_win_content)).setText("");

        Intent intent1 = new Intent(BluetoothClient.ACTION_BLUETOOTH_SEND);
        intent1.putExtra(bluetoothAddr,new RecvDataVO(new byte[]{(byte)0xb0,(byte)0xa1},2));
        sendBroadcast(intent1);
    }

    public void btn_pause_onClick(View v){
        if("暂停".equals(btn_pause.getText())){
            unregisterReceiver(recvReceiver);
            btn_pause.setText("开始");
        }else{
            registerReceiver(recvReceiver, recvIFilter);
            btn_pause.setText("暂停");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unbindService(con);
            unregisterReceiver(recvReceiver);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //定义接收数据缓冲区和增加数据操作
    static final int RECVBUFFSIZE = 1024*100;
    byte[] contentByte = new byte[RECVBUFFSIZE];
    int contentIndex = 0;
    byte[] appendByteArray(byte[] targetData, int len){
        if((len + contentIndex) >= RECVBUFFSIZE){
            System.arraycopy(contentByte,RECVBUFFSIZE/2,contentByte,0,RECVBUFFSIZE/2);
            System.arraycopy(contentByte,RECVBUFFSIZE/2,new byte[RECVBUFFSIZE/2],0,RECVBUFFSIZE/2);
            contentIndex = RECVBUFFSIZE/2;
        }
        System.arraycopy(targetData, 0, contentByte, contentIndex, len);
        contentIndex += len;
        return contentByte;
    }
    public class RecvReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(BluetoothClient.ACTION_BLUETOOTH_RECV.equals(intent.getAction())){
                RecvDataVO vo = (RecvDataVO)intent.getSerializableExtra(lastIntent.getStringExtra("DATA"));
                contentByte = appendByteArray(vo.getData(), vo.getLen());
                try {
                    tv_win_content.setText(new String(Arrays.copyOfRange(contentByte, 0, contentIndex),"GBK"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mScrollToBottom.run();
            }
        }
    }


}
