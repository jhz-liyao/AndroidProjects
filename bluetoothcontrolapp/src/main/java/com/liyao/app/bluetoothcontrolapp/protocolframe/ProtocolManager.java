package com.liyao.app.bluetoothcontrolapp.protocolframe;

import android.util.Log;


import com.liyao.app.bluetoothcontrolapp.protocolframe.vo.TransmitDataVO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by liyao on 2016年10月22日
 */
public class ProtocolManager {
    static final String TAG = "ProtocolManager";
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


    public static void receiveRegister(RecvProtocolBase rpb){
        templateProtocolList.add(rpb);
    }

    /**
     * 将协议添加至发送缓冲区
     * @param spb
     */
    public static void sendProtocol(SendProtocolBase spb){
        if(spb.integrityChecking() == true){
            try {
                sendProtocolQueue.put(spb);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从接收协议缓冲区中将协议取出，下一步执行handle
     * @return
     */
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

    /**
     * 处理socket读到的数据进行封装，添加到接收缓冲区
     * @param vo
     */
    public static void protocolMatch(TransmitDataVO vo){
        try {
            for(int i = 0; i < vo.getLen(); i++) {
                byte tmpData = vo.getData()[i];
                //Log.i("",Integer.toHexString(tmpData));
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
                    Log.i(TAG,"收到完整协议");
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
                            Log.i("","协议加入缓存队列");
                            break;
                        }
                    }
                    hasHead = false;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            hasHead = false;
            singleProtocol = new ArrayList<>();
        }
    }


    public static class SocketCom implements ProtocolTransferInterface{
        /**
         * socket调用此函数将数据进行处理
         * @param vo
         */
        @Override
        public void socketRead(TransmitDataVO vo) {
            //MessageManager.send(vo.convertCN());
            protocolMatch(vo);
        }

        /**
         * socket调用此函数获取准备发送的数据（提取发送协议缓冲区数据）
         * @return
         */
        @Override
        public TransmitDataVO socketSend() {
            SendProtocolBase spb = null;
            try {
                spb = sendProtocolQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(spb != null) {
                List<Byte> data = spb.getByteList(sendSerial++);
                TransmitDataVO vo = new TransmitDataVO();
                vo.setLen(data.size());
                vo.setData(data);
                return vo;
            }
            return null;
        }
    }
}
