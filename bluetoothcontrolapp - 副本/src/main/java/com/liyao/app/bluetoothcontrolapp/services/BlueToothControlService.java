package com.liyao.app.bluetoothcontrolapp.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.liyao.app.bluetoothcontrolapp.BluetoothClient;
import com.liyao.app.bluetoothcontrolapp.R;
import com.liyao.app.bluetoothcontrolapp.receiver.BluetoothReceiver;
import com.liyao.app.bluetoothcontrolapp.vo.RecvDataVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by liyao on 2016/5/20.
 */
public class BlueToothControlService extends Service {
    public static final String TAG = "BlueToothControlService";
    public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
    public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
    public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";
    private static final UUID HC_05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String ACTION_CALL_BLUETOOTHFIND    = "bluetoothcontrolapp.bluetoothcontrolservice.call.bluetoothfind";
    public static final String ACTION_CALL_BLUETOOTHBIND = "bluetoothcontrolapp.bluetoothcontrolservice.call.bluetoothbind";
    public static final String ACTION_BACK_BLUETOOTHBIND = "bluetoothcontrolapp.bluetoothcontrolservice.back.bluetoothbind";
    ServiceReceiver serviceReceiver = new ServiceReceiver();
    Set<BluetoothDevice> bindDevList = null;//已配对设备列表
    BluetoothClient bc = null;
    byte recvData[] = new byte[0];
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
    List<BluetoothClient> bluetoothClients = new ArrayList<>();
    public BluetoothClient getBluetoothClient(BluetoothDevice bluetoothDevice){
        for(BluetoothClient tmpBC : bluetoothClients ){
            if(bluetoothDevice.getAddress().equals(tmpBC.bluetoothDevice.getAddress()))
                return tmpBC;
        }
        BluetoothClient bc = new BluetoothClient(this,bluetoothDevice);
        bluetoothClients.add(bc);
        return bc;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bindDevList = bluetoothAdapter.getBondedDevices();//已配对设备列表
        //注册系统广播接收者
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ifilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, ifilter);//注册系统蓝牙广播接收者
        ifilter = new IntentFilter();
        ifilter.addAction(ACTION_CALL_BLUETOOTHFIND);
        ifilter.addAction(ACTION_CALL_BLUETOOTHBIND);
        registerReceiver(serviceReceiver, ifilter);//注册系统蓝牙广播接收者
        Log.v(TAG,"onCreate");
    }
    Handler mhandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                byte[] tmArray = new byte[msg.arg1 + recvData.length];
                System.arraycopy(recvData,0,tmArray,0,recvData.length);
                System.arraycopy(msg.obj,0,tmArray,recvData.length,msg.arg1);
                recvData = tmArray;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    });
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(intent.getStringExtra("DATA"));
        bc = this.getBluetoothClient(bluetoothDevice);
        bc.start();
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(intent.getStringExtra("DATA"));
        bc = this.getBluetoothClient(bluetoothDevice);
       bc.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bc.stop();
        super.onUnbind(intent);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);//注销系统蓝牙广播接收者
        unregisterReceiver(serviceReceiver);
        Log.v(TAG,"onDestroy");
    }

    /**
     *
     */
    public void bluetoothFind(){
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        bluetoothAdapter.startDiscovery();
    }

    /**
     *
     */
    public void broadcastBindDev(){
        Intent intent = new Intent(ACTION_BACK_BLUETOOTHBIND);
        for(BluetoothDevice bd : bindDevList) {
            intent.putExtra("DATA", bd.getName() + "\n" + bd.getAddress());
            sendBroadcast(intent);
        }
    }

    /**
     *
     */
    public class ServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_CALL_BLUETOOTHFIND.equals(intent.getAction())){
                bluetoothFind();
            }else if(ACTION_CALL_BLUETOOTHBIND.equals(intent.getAction())){
                broadcastBindDev();
            }
        }
    }
}
