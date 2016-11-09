package com.liyao.app.bluetoothcontrolapp.protocol.entity.recvprotocol;


import android.util.Log;

import com.liyao.app.bluetoothcontrolapp.protocol.entity.RecvProtocolBase;

/**
 * Created by liyao on 2016/5/25.
 */
public class RunProtocol extends RecvProtocolBase {
    public byte left_dir;
    public byte left_speed_h;
    public byte left_speed_l;
    public byte right_dir;
    public byte right_speed_h;
    public byte right_speed_l;

    public RunProtocol(byte[] protocolArr) throws Exception {
        super(6,protocolArr);
        //封装协议
        this.left_dir = super.protocolList.get(0);
        this.left_speed_h = super.protocolList.get(1);
        this.left_speed_l = super.protocolList.get(2);
        this.right_dir = super.protocolList.get(3);
        this.right_speed_h = super.protocolList.get(4);
        this.right_speed_l = super.protocolList.get(5);
    }

    @Override
    public boolean integrityChecking() {//数据正确性校验
        if(super.getHead() != (byte)0xfd || super.getTail() != (byte)0xf8){
            Log.w("RunProtocol", "integrityChecking：协议起始非0xfd和0xf8");
            return false;
        }
        if(super.getChecksum() != super.getRealsum()) {
            Log.w("RunProtocol", "integrityChecking：校验和错误");
            return false;
        }
        return true;
    }
}
