package com.liyao.app.homecontrolcenter.protocolframe;

import android.os.SystemClock;
import android.util.Log;


import com.liyao.app.homecontrolcenter.MessageManager;
import com.liyao.app.homecontrolcenter.SocketManager;
import com.liyao.app.homecontrolcenter.moduleboard.WaterMachine.receive_p.StateProtocol;
import com.liyao.app.homecontrolcenter.protocolframe.vo.TransmitDataVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liyao on 2016年10月22日
 */
public class ProtocolManager {
    static final String TAG = "ProtocolManager";
    //模块
    public static final byte COORDINATOR_MODULE = 0x00;
    public static final byte WATER_MODULE = 0x01;
    /*协议列表*/
    //接收协议
    public static final short STATE_PROTOCOL = (WATER_MODULE << 4 | COORDINATOR_MODULE) <<8 |0x20;
    //发送协议
    public static final short CMD_PROTOCOL = (COORDINATOR_MODULE << 4 | WATER_MODULE) <<8 |0x01;
    public static final short STATEGET_PROTOCOL   = (COORDINATOR_MODULE << 4 | WATER_MODULE) <<8 |0x02;


    //public static ProtocolTransfer sendThread = new ProtocolTransfer();//发送线程
    //public static ProtocolMatch    recvThread = new ProtocolMatch();//接收线程

    //接收协议匹配库
    private static List<RecvProtocolBase> templateProtocolList = new ArrayList();
    //socket零散数据
    private static BlockingQueue<TransmitDataVO> receiveData = new ArrayBlockingQueue(1024);//socket打包过来的数据
    //封装过的接收协议待处理队列
    private static BlockingQueue<RecvProtocolBase> recvProtocolQueue = new ArrayBlockingQueue(1024);
    //封装过的发送协议待处理队列
    private static BlockingQueue<SendProtocolBase> sendProtocolQueue = new ArrayBlockingQueue(1024);
//    public static Lock recvProtocolList_Lock = new ReentrantLock();
//    public static Lock sendProtocolList_Lock = new ReentrantLock();
    private static byte sendSerial = 0;
    public static void Init(){
        receiveRegister(new StateProtocol()); //接收协议注册
    }

    private static void receiveRegister(RecvProtocolBase rpb){
        templateProtocolList.add(rpb);
    }

    public static void putSocketData(TransmitDataVO vo){//对socket提供的数据保存接口
        try {
            receiveData.put(vo);
            Log.e("","receiveData_SIZE"+receiveData.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sendProtocol(SendProtocolBase spb){
        if(SocketManager.state != SocketManager.SocketState.OPEN){
            MessageManager.send("控制中心未连接");
            return;
        }
        if(spb.integrityChecking() == true){
            try {
                sendProtocolQueue.put(spb);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static RecvProtocolBase fetchProtocol(){
        try {
            RecvProtocolBase rpb = recvProtocolQueue.take();
            Log.w(TAG, "协议take成功");
            return rpb;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    static boolean hasHead = false;
    static List<Byte> singleProtocol = null;
    public static void protocolMatch(TransmitDataVO vo){
        try {
            for(int i = 0; i < vo.getLen(); i++) {
                byte tmpData = vo.getData()[i];
                if(tmpData != (byte)0xFD && hasHead == false)
                    continue;
                if (tmpData == (byte)0xFD) {
                    if(hasHead){
                        Log.w(TAG,"FD重复");
                    }
                    hasHead = true;
                    singleProtocol = new ArrayList<>();
                }
                if(hasHead){
                    singleProtocol.add(tmpData);
                }
                if(tmpData == (byte)0xF8){
                    Log.e("","出现F8");
                    byte module = singleProtocol.get(3);
                    byte action = singleProtocol.get(5);
                    short ModuleAction = (short)((module << 8) | action);
                    for(int j = 0; j < templateProtocolList.size(); j++){
                        if(ModuleAction == templateProtocolList.get(j).ModuleAction){
                            RecvProtocolBase tmprpb = templateProtocolList.get(j).getEntity(singleProtocol);
                            if(tmprpb != null)
                                recvProtocolQueue.put(tmprpb);
                            hasHead = false;
                            singleProtocol = new ArrayList<>();
                            Log.e("","recvProtocolQueue.PUT");
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            hasHead = false;
            singleProtocol = new ArrayList<>();
        }
    }


    public static class SocketCom implements ProtocolTransferInterface{

        @Override
        public void socketRead(TransmitDataVO vo) {
            //MessageManager.send(vo.convertCN());
            protocolMatch(vo);
        }

        @Override
        public TransmitDataVO socketSend() {
            SendProtocolBase spb = null;
            try {
                spb = sendProtocolQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<Byte> data = spb.getByteList(sendSerial++);
            TransmitDataVO vo = new TransmitDataVO();
            vo.setLen(data.size());
            vo.setData(data);
            return vo;
        }
    }


    static class ProtocolMatch  extends Thread{
        public boolean exit = false;
        public void cancel(){
            exit = true;
            this.interrupt();
        }
        @Override
        public void run() {
            super.run();
            TransmitDataVO vo = null;
            boolean hasHead = false;
            List<Byte> singleProtocol = new ArrayList<Byte>();
            while(!this.isInterrupted() && !exit) {
                RecvProtocolBase rpb = null;
                byte tmpData = 0;
                try {
                    while(!this.isInterrupted()){
                        vo = receiveData.take();
                        Log.e("","收到数据");
                        for(int i = 0; i < vo.getLen(); i++) {
                            tmpData = vo.getData()[i];
                            if (tmpData == (byte)0xFD) {
                                if(hasHead){
                                    throw new Exception("FD重复");
                                }
                                hasHead = true;
                                singleProtocol = new ArrayList<>();
                            }
                            if(hasHead){
                                singleProtocol.add(tmpData);
                            }
                            if(tmpData == (byte)0xF8){
                                Log.e(TAG,"出现F8");
                                byte module = singleProtocol.get(3);
                                byte action = singleProtocol.get(5);
                                short ModuleAction = (short)((module << 8) | action);
                                for(int j = 0; j < templateProtocolList.size(); j++){
                                    if(ModuleAction == templateProtocolList.get(j).ModuleAction){
                                        RecvProtocolBase tmprpb = templateProtocolList.get(j).getEntity(singleProtocol);
                                        recvProtocolQueue.put(tmprpb);
                                        Log.w(TAG, "协议put成功");
                                        hasHead = false;
                                        singleProtocol = new ArrayList<>();
                                        Log.e("","recvProtocolQueue.PUT");
                                    }
                                }

                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    hasHead = false;
                    singleProtocol = new ArrayList<>();
                }
                //SystemClock.sleep(50);
            }
        }
    }

    static class ProtocolTransfer  extends Thread{
        public boolean exit = false;
        public void cancel(){
            exit = true;
            this.interrupt();
        }
        @Override
        public void run() {
            super.run();
            while(!this.isInterrupted() && !exit) {
                try {
                    SendProtocolBase spb = sendProtocolQueue.take();
                    List<Byte> data = spb.getByteList(sendSerial++);
                    TransmitDataVO vo = new TransmitDataVO();
                    vo.setLen(data.size());
                    vo.setData(data);
                    //pti.socketRead(vo);
                }catch(Exception e){
                    e.printStackTrace();
                }
                //SystemClock.sleep(50);
            }
        }
    }
    /*public static class ProtocolUtil implements ProtocolCom{
        @Override
        public ProtocolBase distinguishProtocolType(byte[] protocolArray) {
            switch (protocolArray[1]){
                case ProtocolBase.RUN_PROTOCOL_TYPE:
                    ProtocolBase pb = new RunProtocol(protocolArray);
                    if(pb.integrityChecking())
                        return pb;
                    return null;
            }
            return null;
        }
    }*/
}
