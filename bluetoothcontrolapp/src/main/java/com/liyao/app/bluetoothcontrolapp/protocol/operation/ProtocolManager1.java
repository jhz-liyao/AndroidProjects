package com.liyao.app.bluetoothcontrolapp.protocol.operation;

import android.os.SystemClock;
import android.util.Log;

import com.liyao.app.bluetoothcontrolapp.protocol.entity.ProtocolOperationVO;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.RecvProtocolBase;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.SendProtocolBase;
import com.liyao.app.bluetoothcontrolapp.protocol.pinterface.ProtocolCom;
import com.liyao.app.bluetoothcontrolapp.protocolframe.vo.TransmitDataVO;
import com.liyao.app.bluetoothcontrolapp.services.BlueToothControlService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liyao on 2016/5/25.
 */
public class ProtocolManager1 {
    public static List<RecvProtocolBase> recvProtocolList = new ArrayList();
    public static List<SendProtocolBase> sendProtocolList = new ArrayList();
    public static Lock recvProtocolList_Lock = new ReentrantLock();
    public static Lock sendProtocolList_Lock = new ReentrantLock();
    public static byte sendSerial = 0;
    public static ProtocolTransfer protocolTransfer = new ProtocolTransfer();
    public static BlueToothControlService.ServiceBinder serviceBinder = null;



    /**
     * 从缓冲区中提取出协议数据数组
     */

    public static void fetchProtocol(ProtocolOperationVO vo, ProtocolCom protocolCom) {
        byte[] curByteArray = vo.transmitDataVO.getData();
        for (int i = 0; i < curByteArray.length; i++) {
            if (vo.protocolHeadFlag != 1 && (curByteArray[i] & 0xFF) == (0xFD & 0xFF)) {//如果之前没有标记协议头
                vo.protocolHeadFlag = 1;    //标记协议头
                vo.protocolByte[vo.protocolIndex++] = curByteArray[i];//将此数据增加到数组
            } else if (vo.protocolHeadFlag == 1) {//如果之前已经标记协议头
                if ((curByteArray[i] & 0xFF) != (0xF8 & 0xFF)) {
                    vo.protocolByte[vo.protocolIndex++] = curByteArray[i];//将此数据增加到数组
                } else {//如果是F8
                    vo.protocolByte[vo.protocolIndex++] = curByteArray[i];
                    byte[] protocolArray = Arrays.copyOfRange(vo.protocolByte, 0, vo.protocolIndex);
                    //将此协议封装
                    RecvProtocolBase pb = protocolCom.distinguishProtocolType(protocolArray);//Protocol_Util实现此接口
                    if (pb != null) {
                        recvProtocolList_Lock.lock();
                        try {
                            recvProtocolList.add(pb);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            recvProtocolList_Lock.unlock();
                        }
                    } else {
                        String pStr = "";
                        for (byte b : protocolArray) {
                            pStr += (ProtocolUtil.hexToString(b) + " ");
                        }
                        Log.w("fetchProtocol", "协议解析错误，返回null :" + pStr);
                    }
                    vo.resetFlag();
                }
            } else if (vo.printHeadFlag != 1 && (curByteArray[i] & 0xFF) == ('#' & 0xFF)) {//如果之前没有标记协议头
                vo.printHeadFlag = 1;    //标记协议头
                vo.printfByte[vo.printIndex++] = curByteArray[i];//将此数据增加到数组
            } else if (vo.printHeadFlag == 1) {//如果之前已经标记协议头
                if ((curByteArray[i] & 0xFF) != ('$' & 0xFF)) {
                    vo.printfByte[vo.printIndex++] = curByteArray[i];//将此数据增加到数组
                } else {//如果是'$'
                    vo.printfByte[vo.printIndex++] = curByteArray[i];
                    byte[] printArray = Arrays.copyOfRange(vo.printfByte, 1, vo.printIndex-1);
                    vo.consoleByte = vo.appendByteArray(vo.consoleByte, printArray, 1024);
                    vo.resetFlag();
                }
            }else{
                vo.err[vo.errIndex++] = curByteArray[i];
                Log.w("fetchProtocol","未解析数据"+String.valueOf(vo.errIndex));
            }
        }
    }

    public static void addProtocolQueue(SendProtocolBase spb){
        if(spb.integrityChecking() == true){
            sendProtocolList_Lock.lock();
            try{
                sendProtocolList.add(spb);}catch (Exception e){
                e.printStackTrace();
            }finally {
                sendProtocolList_Lock.unlock();
            }

        }
    }

    public static class ProtocolTransfer  extends Thread{
        @Override
        public void run() {
            super.run();
            while(!this.isInterrupted()) {
                SendProtocolBase spb = null;
                sendProtocolList_Lock.lock();
                try {
                    if (sendProtocolList.size() > 0) {
                        spb = sendProtocolList.get(0);
                        List<Byte> data = spb.getByteList(sendSerial++);
                        TransmitDataVO vo = new TransmitDataVO();
                        vo.setLen(data.size());
                        vo.setData(data);
                        try {
                            serviceBinder.sendBluetoothData(vo);
                            sendProtocolList.remove(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    sendProtocolList_Lock.unlock();
                }
                SystemClock.sleep(50);
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
