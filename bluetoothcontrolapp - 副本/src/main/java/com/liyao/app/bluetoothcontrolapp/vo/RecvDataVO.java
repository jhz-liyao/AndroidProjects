package com.liyao.app.bluetoothcontrolapp.vo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.Timestamp;
import java.util.Date;

/**
 * Created by liyao on 2016/5/20.
 */
public class RecvDataVO implements Serializable {
    public RecvDataVO(){
        createDate = new Date(System.currentTimeMillis());
    }
    public RecvDataVO(byte[] _data,int _len){
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
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
