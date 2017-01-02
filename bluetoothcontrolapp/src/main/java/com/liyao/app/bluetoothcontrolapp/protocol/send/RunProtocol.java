package com.liyao.app.bluetoothcontrolapp.protocol.send;

import com.liyao.app.bluetoothcontrolapp.protocol.Protocol;
import com.liyao.app.bluetoothcontrolapp.protocolframe.SendProtocolBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyao on 2017/1/2.
 */

public class RunProtocol extends SendProtocolBase {

    public byte left_dir;
    public byte left_speed_h;
    public byte left_speed_l;
    public byte right_dir;
    public byte right_speed_h;
    public byte right_speed_l;

    public RunProtocol() {
        super(Protocol.RUN_PROTOCOL);
    }

    @Override
    public boolean integrityChecking() {
        return true;
    }

    @Override
    public List<Byte> getByteList(byte serialNum) {
        List<Byte> list = new ArrayList<>();
        list.add(this.left_dir);
        list.add(this.left_speed_h);
        list.add(this.left_speed_l);
        list.add(this.right_dir);
        list.add(this.right_speed_h);
        list.add(this.right_speed_l);
        return super.setParameter(serialNum, list);
    }
}
