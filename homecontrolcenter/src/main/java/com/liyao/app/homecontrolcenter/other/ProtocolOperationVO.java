package com.liyao.app.homecontrolcenter.other;


import com.liyao.app.homecontrolcenter.protocolframe.vo.TransmitDataVO;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by liyao on 2016/5/30.
 */
public class ProtocolOperationVO {
    public static byte[] printfByte = new byte[1024];
    public static byte[] protocolByte = new byte[1024];
    public static byte[] consoleByte = new byte[0];

    public static int protocolIndex = 0;
    public static int printIndex = 0;
    public static int protocolHeadFlag = -1;
    public static int printHeadFlag = -1;

    public static byte[] err = new byte[1024];
    public static int errIndex = 0;

    public static TransmitDataVO transmitDataVO;

    public void resetFlag(){
        protocolHeadFlag = -1;
        protocolIndex = 0;
        printHeadFlag = -1;
        printIndex = 0;
    }

    public static String convertCN(){
        try {
            return new String(consoleByte, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String convertCN(byte[] byteArray){
        try {
            return new String(byteArray, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] appendByteArray(byte[] oldData, byte[] newData, int limitSize){
        int sumLen = oldData.length + newData.length;
        byte[] tmpByteArray = new byte[oldData.length + newData.length];
        System.arraycopy(oldData, 0,tmpByteArray, 0, oldData.length);
        System.arraycopy(newData, 0, tmpByteArray, oldData.length, newData.length);
        if(limitSize >0 && sumLen > limitSize){
            //过限制后减去限制的10%
            int part = (int)(limitSize * 0.1);
            tmpByteArray = Arrays.copyOfRange(tmpByteArray, sumLen - limitSize + part, sumLen);
        }
        return tmpByteArray;
    }
}
