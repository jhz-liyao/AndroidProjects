package com.liyao.app.bluetoothcontrolapp.protocolframe.vo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by liyao on 2016/5/20.
 */
public class TransmitDataVO implements Serializable {
    public TransmitDataVO(){
        createDate = new Date(System.currentTimeMillis());
    }
    public TransmitDataVO(byte[] _data, int _len){
        createDate = new Date(System.currentTimeMillis());
        data = _data;
        len = _len;
    }
    byte[] data = null;
    int len = 0;
    Date createDate = null;
    public String convertCN(){
        try {
            return new String(data, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public byte[] getData() {
        return Arrays.copyOfRange(data,0,len);
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    public void setData(List<Byte> data) {
        this.data = new byte[data.size()];
        for(int i = 0;i < data.size(); i++)
            this.data[i] = data.get(i);
    }
}
