package com.liyao.app.bluetoothcontrolapp.protocol.entity.sendprotocol;


import android.util.Log;

import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolUtil;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.SendProtocolBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyao on 2016/5/25.
 */
public class RunProtocol extends SendProtocolBase {
    public static final byte UP_DIR = 0x01;
    public static final byte DOWN_DIR = 0x02;
    public static final byte STOP_DIR = 0x00;

    public RunProtocol() {
        super(ProtocolUtil.RUN_PROTOCOL_SEND_TYPE);
    }

    public byte left_dir;
    public byte left_speed_h;
    public byte left_speed_l;
    public byte right_dir;
    public byte right_speed_h;
    public byte right_speed_l;

    @Override
    public List<Byte> getByteList(byte serialNum) {
        super.setSerial(serialNum);
        super.setChecksum((byte)(super.getType()+ super.getSerial() + super.getChecksum() + left_dir + left_speed_h + left_speed_l + right_dir + right_speed_h + right_speed_l));
        List<Byte> list = new ArrayList<>();
        list.add(super.getHead());
        list = super.filterAddByte(super.getType(), list);
        list = super.filterAddByte(left_dir, list);
        list = super.filterAddByte(left_speed_h, list);
        list = super.filterAddByte(left_speed_l, list);
        list = super.filterAddByte(right_dir, list);
        list = super.filterAddByte(right_speed_h, list);
        list = super.filterAddByte(right_speed_l, list);
        list = super.filterAddByte(super.getSerial(), list);
        list = super.filterAddByte(super.getChecksum(), list);
        list.add(super.getTail());
        return list;
    }


    @Override
    public boolean integrityChecking() {//数据正确性校验
        if(super.getHead() != (byte)0xfd || super.getTail() != (byte)0xf8){
            Log.v("RunProtocol", "integrityChecking：协议起始非0xfd和0xf8");
            return false;
        }
        return true;
    }
}
