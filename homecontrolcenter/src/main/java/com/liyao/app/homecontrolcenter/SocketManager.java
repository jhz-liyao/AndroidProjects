package com.liyao.app.homecontrolcenter;

import android.os.SystemClock;

import com.liyao.app.homecontrolcenter.protocolframe.ProtocolTransferInterface;
import com.liyao.app.homecontrolcenter.protocolframe.vo.TransmitDataVO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by liyao on 2016/10/23.
 */
public class SocketManager {
    static final String TAG = "SocketManager";
    public enum SocketState{ INIT, OPEN, CLOSE, PAUSE}

    public static SocketState state = SocketState.INIT;
    static Socket socket = null;
    public static String IP = "";
    public static int PORT = 0;

    static InputStream is = null;
    static OutputStream os = null;
    static ProtocolTransferInterface ptInterface = null;

    static SocketReceiver socketReceiver = null;
    static SocketSender   socketSender   = null;
    static SocketStarter socketStarter   = null;

    public static void Init(ProtocolTransferInterface _ptInterface){
        ptInterface = _ptInterface;
    }

    public static void socketConnect(String ip, int port){
        IP = ip;
        PORT = port;
        socketClose();
        socketReceiver = new SocketReceiver();
        socketSender   = new SocketSender();
        socketStarter   = new SocketStarter();
        socketStarter.start();
        socketReceiver.start();
        socketSender.start();
    }

    public static void socketClose(){
        if(socket != null) {
            socketReceiver.cancel();
            socketSender.cancel();
            is = null;
            os = null;
            try {
                socket.close();
                socket = null;
                MessageManager.send("断开控制中心");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class SocketStarter  extends Thread{
        public boolean exit = false;
        public void cancel() {
            exit = true;
            this.interrupt();
        }

        @Override
        public void run() {
            super.run();
            try {
                if(socket != null)
                    socket.close();
                socket = new Socket(IP, PORT);//ip, port
                is = socket.getInputStream();
                os = socket.getOutputStream();
                state = SocketState.OPEN;
                MessageManager.send("控制中心连接成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    static class SocketSender  extends Thread {
        public boolean exit = false;
        public void cancel() {
            exit = true;
            this.interrupt();
        }

        @Override
        public void run() {
            super.run();
            while (!this.isInterrupted() && !exit) {
                try {
                    TransmitDataVO vo = ptInterface.socketSend();
                    if(vo != null){
                        os.write(vo.getData(), 0, vo.getLen());
                        MessageManager.send("发送成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class SocketReceiver  extends Thread{
        public boolean exit = false;
        public void cancel(){
            exit = true;
            this.interrupt();
        }
        @Override
        public void run() {
            super.run();
            byte[] data = new byte[1024];
            while(!this.isInterrupted() && !exit) {
                try {
                    if(is == null){
                        SystemClock.sleep(200);
                        continue;
                    }
                    int cnt = is.read(data,0,1024);
                    if(cnt > 0) {
                        TransmitDataVO vo = new TransmitDataVO(data, cnt);
                        ptInterface.socketRead(vo);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
