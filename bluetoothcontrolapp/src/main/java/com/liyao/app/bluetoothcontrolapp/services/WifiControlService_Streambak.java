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

public class WifiControlService_Streambak extends Service {
    static final String TAG = "@WifiControlService@";
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
        public WifiControlService_Streambak getService(WifiCommunication _wifi_com){
            wifi_com = _wifi_com;
            return WifiControlService_Streambak.this;
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
            while(true) {
                try {
                    socket = new Socket(IP, PORT);//ip, port
                    is = socket.getInputStream();
                    int image_size = 0;
                    for(int i = 3;i >= 0; i--)//获取图片大小
                        image_size |= ((is.read() & 0xff) << (i * 8));

                    int need = image_size;//计算单帧图片剩余的字节数
                    byte[] tmpBuf = new byte[image_size];//建立和一帧图片一样大小的缓冲区

                    WifiImageVO vo = new WifiImageVO();
                    while (true) {
                        int count = is.read(tmpBuf, 0, need);
                        need -= count;
                        if (count > 0)
                            vo.getStream().write(tmpBuf, 0, count);
                        else
                            break;
                        if(need == 0){
                            need = image_size;
                            if(wifi_com != null)
                                wifi_com.receiveCallback(vo);
                            vo.getStream().close();
                            vo = new WifiImageVO();
                        }

                        if (exit || this.isInterrupted())
                            throw new Exception("线程终止");
                    }
                    if (exit || this.isInterrupted())
                        throw new Exception("线程终止");
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
