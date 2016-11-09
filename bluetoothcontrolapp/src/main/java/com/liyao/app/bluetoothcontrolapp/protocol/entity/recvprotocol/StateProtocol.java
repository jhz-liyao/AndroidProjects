package com.liyao.app.bluetoothcontrolapp.protocol.entity.recvprotocol;

import com.liyao.app.bluetoothcontrolapp.protocol.entity.RecvProtocolBase;

/**
 * Created by liyao on 2016/5/27.
 */
public class StateProtocol extends RecvProtocolBase {
    public static final int DATA_LEN = 13;
    public StateProtocol(byte[] protocolArr) throws Exception {
        super(DATA_LEN,protocolArr);
        //封装协议
        this.left_dir = super.protocolList.get(0);
        this.left_speed_h = super.protocolList.get(1);
        this.left_speed_l = super.protocolList.get(2);
        this.right_dir = super.protocolList.get(3);
        this.right_speed_h = super.protocolList.get(4);
        this.right_speed_l = super.protocolList.get(5);
     }

    public byte left_dir;
    public byte left_speed_h;
    public byte left_speed_l;
    public byte right_dir;
    public byte right_speed_h;
    public byte right_speed_l;
    public byte obligate1;
    public byte obligate2;
    public byte obligate3;
    public byte obligate4;
    public byte obligate5;
    public byte obligate6;
    public byte obligate7;
}
