package com.liyao.app.bluetoothcontrolapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.liyao.app.bluetoothcontrolapp.vo.WifiImageVO;
import com.liyao.app.bluetoothcontrolapp.wifi_interface.WifiCommunication;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class WifiControlService_bak2 extends Service {
    static final String TAG = "@WifiControlService@";
    static final int BUFF_SIZE = 1024;
    WifiCommunication wifi_com;
    RecvThread recvThread = null;

    Socket socket = null;
    public String IP = "";
    public int PORT = 0;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(TAG,"onBind");
        recvThread = new RecvThread();
        recvThread.start();
        return new MBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i(TAG,"onRebind");
        recvThread = new RecvThread();
        recvThread.start();
    }

    public void call(){
        //wifi_com.receiveCallback(null);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"onUnbind");
        recvThread.cancel();
        return true;
    }
    public class MBinder extends Binder{
        public WifiControlService_bak2 getService(WifiCommunication _wifi_com){
            wifi_com = _wifi_com;
            return WifiControlService_bak2.this;
        }
    }

    public class RecvThread extends Thread {
        public boolean exit = false;
        public void cancel(){
            exit = true;
            this.interrupt();
        }
        @Override
        public void run() {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InputStream is = null;
            byte[] tmpBuf = new byte[BUFF_SIZE];//建立和一帧图片一样大小的缓冲区
            while(true) {
                try {
                    if (exit || this.isInterrupted())
                        throw new Exception("线程终止");
                    socket = new Socket(IP, PORT);//ip, port
                    is = socket.getInputStream();
                    WifiImageVO vo = new WifiImageVO();
                    while (true) {
                        int count = is.read(tmpBuf, 0, BUFF_SIZE);
                        if (count > 0)
                            vo.getStream().write(tmpBuf, 0, count);
                        else
                            break;
                    }
                    if(wifi_com != null)
                        wifi_com.receiveCallback(vo);
                    vo.getStream().close();
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        if(socket != null && socket.isConnected()){
                            is.close();
                            socket.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (exit || this.isInterrupted())
                        return;
                    try {
                        sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                }finally {

                }
            }
        }
    }
}
