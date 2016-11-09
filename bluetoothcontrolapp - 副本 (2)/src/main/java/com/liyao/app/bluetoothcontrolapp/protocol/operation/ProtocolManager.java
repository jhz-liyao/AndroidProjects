package com.liyao.app.bluetoothcontrolapp.protocol.operation;

import android.os.SystemClock;
import android.util.Log;

import com.liyao.app.bluetoothcontrolapp.protocol.entity.ProtocolOperationVO;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.RecvProtocolBase;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.SendProtocolBase;
import com.liyao.app.bluetoothcontrolapp.protocol.pinterface.ProtocolCom;
import com.liyao.app.bluetoothcontrolapp.services.BlueToothControlService;
import com.liyao.app.bluetoothcontrolapp.vo.TransmitDataVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liyao on 2016/5/25.
 */
public class ProtocolManager {
    public static List<RecvProtocolBase> recvProtocolList = new ArrayList();
    public static List<SendProtocolBase> sendProtocolList = new ArrayList();
    public static Lock recvProtocolList_Lock = new ReentrantLock();
    public static Lock sendProtocolList_Lock = new ReentrantLock();
    public static byte sendSerial = 0;
    public static ProtocolTransfer protocolTransfer = new ProtocolTransfer();
    public static BlueToothControlService.ServiceBinder serviceBinder = null;
    private static String hexToString(byte data){
        String hex = Integer.toHexString(data & 0xFF);
        if (hex.length() == 1)
        {
            hex = '0' + hex;
        }
        return "0x"+hex;
    }

    /**
     * 提取协议
     * @return 返回协议之外的数据
     */

    private static void matchProtocol(ProtocolOperationVO vo,ProtocolCom protocolCom){//匹配协议
        vo.protocolByte = vo.appendByteArray(vo.partByte, vo.transmitDataVO.getData(),10240);
        byte[] tmpBuff = vo.protocolByte;
        byte[] afterBuff = new byte[0];
        int i = 0;
        while(i != tmpBuff.length) {
            int headFlag = -1;
            int tailFlag = -1;
            for (i = 0; i < tmpBuff.length; i++) {
                if ((tmpBuff[i] & 0xFF) == (0xFD & 0xFF) && headFlag == -1) {//搜索FD
                    headFlag = i;
                    continue;
                }
                if ((tmpBuff[i] & 0xFF) == (0xF8 & 0xFF) && headFlag != -1 && tailFlag == -1) {//搜索F8
                    tailFlag = i;
                }

                if (headFlag != -1 && tailFlag != -1) { //搜索到FD 和 F8
                    byte[] lastProtocol = Arrays.copyOfRange(tmpBuff, headFlag, tailFlag + 1);
                    Arrays.fill(tmpBuff, headFlag, tailFlag + 1,(byte)0);
                    RecvProtocolBase pb = protocolCom.distinguishProtocolType(lastProtocol);//Protocol_Util实现此接口
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
                        for (byte b : lastProtocol) {
                            pStr += (hexToString(b) + " ");
                        }
                        Log.v("fetchProtocol", "协议解析错误，返回null :" + pStr);
                    }
                    headFlag = -1;
                    tailFlag = -1;
                }
            }

            if(headFlag != -1){
                byte[] tmp = Arrays.copyOfRange(tmpBuff, 0, headFlag);
                afterBuff = ProtocolUtil.arrayAppend(afterBuff,tmp);
            }else{
                afterBuff = ProtocolUtil.arrayAppend(afterBuff,tmpBuff);
            }
            tmpBuff = Arrays.copyOfRange(tmpBuff, tailFlag +1, tmpBuff.length);
        }
        vo.protocolDoneByte = afterBuff;
    }

    private static void matchPrintf(ProtocolOperationVO vo) {//匹配打印信息
        byte[] dataBuff = vo.protocolDoneByte;
        int endIndex = dataBuff.length - 1;
        for(int i = dataBuff.length -1; i > 0; i--){
            if(dataBuff[i] == 0xfd){
                endIndex = i;
            }
        }
        int headFlag = -1,tailFlag = -1;
        for (int i = 0;i <= endIndex; i++){
            if((dataBuff[i] & 0xff) == ('#' & 0xff)){
                headFlag = i;
                continue;
            }
            if((dataBuff[i] & 0xff) == ('$' & 0xff) && headFlag != -1){
                tailFlag = i;
            }
            if(headFlag != -1 && tailFlag != -1){
                byte[] tmpByte = Arrays.copyOfRange(dataBuff, headFlag + 1, tailFlag);
                vo.printfByte = ProtocolUtil.arrayAppend(vo.printfByte,tmpByte);
                headFlag = -1;tailFlag = -1;
            }
        }
        vo.partByte= Arrays.copyOfRange(dataBuff, endIndex+1, dataBuff.length);
    }

    /**
     * 从缓冲区中提取出协议数据数组
     * 返回数组以外的数据
     */
    public static void fetchProtocol(ProtocolOperationVO vo, ProtocolCom protocolCom) {
       // byte[] handleByte = //vo.appendByteArray(vo.partByte,vo.transmitDataVO.getData(),10240); //将之前未匹配的协议与新数据进行组合
        matchProtocol(vo,protocolCom); //提取协议
        matchPrintf(vo);//提取打印信息
        //return tmpBuff;//完善序号看丢包率
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
                Log.v("","protocolTransferTask");
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
