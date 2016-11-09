package com.liyao.app.bluetoothcontrolapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.liyao.app.bluetoothcontrolapp.bluetooth_activity.ControlActivity;
import com.liyao.app.bluetoothcontrolapp.bluetooth_interface.BluetoothCommunication;
import com.liyao.app.bluetoothcontrolapp.vo.TransmitDataVO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by liyao on 2016/5/19.
 */
public class BluetoothClient{
    private static final String TAG = "BluetoothClient";
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//串口服务
    public BluetoothSocket socket = null;
    public BluetoothDevice bluetoothDevice = null;
    Context context = null;
    ReadThread rt = null;
    InputStream is = null;
    OutputStream os = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public BluetoothCommunication bluetoothCommunication;

    /**
     * 初始化蓝牙设备
     * @param _bluetoothDevice
     */
    public BluetoothClient( Context context, BluetoothDevice _bluetoothDevice){
        this.context = context;
        bluetoothDevice = _bluetoothDevice;
        try {
            socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            bluetoothAdapter.cancelDiscovery();//终止设备发现
            if(socket.isConnected()) {
                Log.w(TAG, "socket连接已存在");
                socket.close();
            }
            socket.connect();
            context.sendBroadcast(new Intent(ControlActivity.CONNECTION_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            context.sendBroadcast(new Intent(ControlActivity.CONNECTION_FAIL));
            Log.w(TAG,"未连接到蓝牙设备");
        }
    }

    /**
     * 开启串口发送 开启串口接收线程
     */
    public void start(){
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
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    Log.w(TAG,"socket未连接或InputStream为null ");
                    return;
                }
                byte readBuffer[] = new byte[1024];
                TransmitDataVO recvDataVO = null;
                while(!isInterrupted()){
                    int count = is.read(readBuffer);
                    recvDataVO = new TransmitDataVO();
                    recvDataVO.setData(readBuffer);
                    recvDataVO.setLen(count);
                    bluetoothCommunication.receiveCallback(recvDataVO);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                    context.sendBroadcast(new Intent(ControlActivity.DISCONNECTION));
            }
        }
    }

    /**
     * 发送串口数据
     */
    public void sendBluetoothData(TransmitDataVO data) {
        if (socket.isConnected() == false || os == null) {
            Log.w(TAG,"socket未连接或OutputStream为null");
            return;
        }
        try {
            os.write(data.getData(), 0, data.getLen());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}