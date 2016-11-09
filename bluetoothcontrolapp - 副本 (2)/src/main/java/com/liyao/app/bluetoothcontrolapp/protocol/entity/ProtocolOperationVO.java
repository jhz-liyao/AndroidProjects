package com.liyao.app.bluetoothcontrolapp.protocol.entity;

import com.liyao.app.bluetoothcontrolapp.vo.TransmitDataVO;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by liyao on 2016/5/30.
 */
public class ProtocolOperationVO {
    public static byte[] printfByte = new byte[0];
    public static byte[] protocolByte = new byte[0];
    public static byte[] partByte = new byte[0];
    public static byte[] protocolDoneByte = new byte[0];
    public static TransmitDataVO transmitDataVO;

    public String convertCN(){
        try {
            return new String(printfByte, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String convertCN(byte[] byteArray){
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
