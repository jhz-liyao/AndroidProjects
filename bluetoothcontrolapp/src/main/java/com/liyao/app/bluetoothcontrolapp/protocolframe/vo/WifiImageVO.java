package com.liyao.app.bluetoothcontrolapp.protocolframe.vo;

import java.io.ByteArrayOutputStream;

/**
 * Created by thinkpad on 2016/7/13.
 */
public class WifiImageVO {
    byte[] data;
    ByteArrayOutputStream stream;
    int len;

    public WifiImageVO(){
        stream = new ByteArrayOutputStream();
    }
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public ByteArrayOutputStream getStream() {
        return stream;
    }

    public void setStream(ByteArrayOutputStream stream) {
        this.stream = stream;
    }
}
