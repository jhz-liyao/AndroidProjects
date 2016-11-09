package com.liyao.app.bluetoothcontrolapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by liyao on 2016/5/19.
 */

public class BluetoothServerThread  extends Thread {
    public Handler handler = null;
    BluetoothServerSocket bluetoothServerSocket = null;
    BluetoothDevice bluetoothDevice = null;
    BluetoothSocket socket = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public static final int ACCEPT_DONE = 0;
    public void run() {

        try {
            Log.d("server", "wait cilent connect...");
    /* 接受客户端的连接请求 */
            socket = bluetoothServerSocket.accept();
            Log.d("server", "accept success !");
            Message msg = new Message();
            msg.what = ACCEPT_DONE;
            handler.sendMessage(msg);
                //启动接受数据
//             mreadThread = new readThread();
//             mreadThread.start();
        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }


}