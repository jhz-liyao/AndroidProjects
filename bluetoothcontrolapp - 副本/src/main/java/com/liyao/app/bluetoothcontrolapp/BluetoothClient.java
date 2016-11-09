package com.liyao.app.bluetoothcontrolapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.liyao.app.bluetoothcontrolapp.vo.RecvDataVO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by liyao on 2016/5/19.
 */
public class BluetoothClient extends BroadcastReceiver {
    private static final String TAG = "BluetoothClient";
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//串口服务
    public static final String ACTION_BLUETOOTH_RECV = "bluetoothcontrolapp.bluetoothclient.back.bluetoothrecv";
    public static final String ACTION_BLUETOOTH_SEND = "bluetoothcontrolapp.bluetoothclient.back.bluetoothsend";
    IntentFilter sendIFilter = null;
    public BluetoothSocket socket = null;
    public Context context;
    public BluetoothDevice bluetoothDevice = null;
    ReadThread rt = null;
    InputStream is = null;
    OutputStream os = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /**
     * 初始化蓝牙设备
     * @param _context
     * @param _bluetoothDevice
     */
    public BluetoothClient(Context _context, BluetoothDevice _bluetoothDevice){
        context = _context;
        bluetoothDevice = _bluetoothDevice;
        try {
            socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            bluetoothAdapter.cancelDiscovery();//终止设备发现
            if(socket.isConnected()) {
                Log.v(TAG, "socket连接已存在");
                socket.close();
            }
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启串口发送 开启串口接收线程
     */
    public void start(){
        sendIFilter = new IntentFilter(ACTION_BLUETOOTH_SEND);
        context.registerReceiver(this, sendIFilter);

        rt = new ReadThread();
        rt.start();

        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭串口发送 关闭串口接收线程
     */
    public void stop(){
        rt.interrupt();
        context.unregisterReceiver(this);
    }

    /**
     * 串口接收线程
     */
    public class ReadThread extends Thread{
        @Override
        public void run() {
            try {
                is = socket.getInputStream();
                if (socket.isConnected() == false || is == null ) {
                    Log.v(TAG,"socket未连接或InputStream为null ");
                    return;
                }
                byte readBuffer[] = new byte[1024];

                RecvDataVO recvDataVO = null;
                while(!isInterrupted()){
                    int count = is.read(readBuffer);
                    recvDataVO = new RecvDataVO();
                    recvDataVO.setData(readBuffer);
                    recvDataVO.setLen(count);
                    Intent intent = new Intent(ACTION_BLUETOOTH_RECV);
                    intent.putExtra(bluetoothDevice.getAddress(),recvDataVO);
                    context.sendBroadcast(intent);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
            }
        }
    }

    /**
     * 发送串口数据广播接收者
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if(ACTION_BLUETOOTH_SEND.equals(intent.getAction())){
            if (socket.isConnected() == false || os == null) {
                Log.v(TAG,"socket未连接或OutputStream为null");
                return;
            }
            RecvDataVO data = (RecvDataVO)intent.getSerializableExtra(bluetoothDevice.getAddress());
            try {
                os.write(data.getData(), 0, data.getLen());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}