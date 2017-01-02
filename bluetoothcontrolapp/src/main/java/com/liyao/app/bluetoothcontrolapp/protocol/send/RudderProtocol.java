package com.liyao.app.bluetoothcontrolapp.protocol.send;

import com.liyao.app.bluetoothcontrolapp.protocol.Protocol;
import com.liyao.app.bluetoothcontrolapp.protocolframe.SendProtocolBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyao on 2017/1/2.
 */

public class RudderProtocol extends SendProtocolBase {
    public byte X_Angle;
    public byte Y_Angle;

    public RudderProtocol() {
        super(Protocol.RUDDER_PROTOCOL);
    }
    @Override
    public boolean integrityChecking() {
        return true;
    }

    @Override
    public List<Byte> getByteList(byte serialNum) {
        List<Byte> list = new ArrayList<>();
        list.add(this.X_Angle);
        list.add(this.Y_Angle);
        return super.setParameter(serialNum, list);
    }
}
