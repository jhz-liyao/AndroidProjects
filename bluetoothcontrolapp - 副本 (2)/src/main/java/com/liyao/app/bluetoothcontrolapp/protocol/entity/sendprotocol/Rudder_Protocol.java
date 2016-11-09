package com.liyao.app.bluetoothcontrolapp.protocol.entity.sendprotocol;

import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolUtil;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.SendProtocolBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyao on 2016/5/27.
 */
public class Rudder_Protocol extends SendProtocolBase {
    public Rudder_Protocol(byte type) {
        super(ProtocolUtil.RUDDER_PROTOCOL_SEND_TYPE);
    }

    @Override
    public boolean integrityChecking() {
        if(angle > 0 && angle < 180)
            return true;
        return false;
    }

    @Override
    public List<Byte> getByteList(byte serialNum) {
        super.setSerial(serialNum);
        super.setChecksum((byte)(super.getType()+ super.getSerial() + super.getChecksum() + angle));
        List<Byte> list = new ArrayList<>();
        list.add(super.getHead());
        list = super.filterAddByte(super.getType(), list);
        list = super.filterAddByte(angle, list);
        list = super.filterAddByte(super.getSerial(), list);
        list = super.filterAddByte(super.getChecksum(), list);
        list.add(super.getTail());
        return list;
    }

    byte angle;
}
