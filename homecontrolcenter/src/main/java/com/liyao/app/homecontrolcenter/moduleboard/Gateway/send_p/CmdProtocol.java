package com.liyao.app.homecontrolcenter.moduleboard.Gateway.send_p;

import com.liyao.app.homecontrolcenter.protocolframe.ProtocolManager;
import com.liyao.app.homecontrolcenter.protocolframe.SendProtocolBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyao on 2016年12月13日21:31:58
 */
public class CmdProtocol extends SendProtocolBase {
    public static final byte WaterGetState = 0x00;
    public static final byte SoilSensorGetState = 0x01;

    byte para1 = 0;
    byte para2 = 0;

    public CmdProtocol() {
        super(ProtocolManager.CMD_PROTOCOL);
    }

    @Override
    public boolean integrityChecking() {
        return true;
    }

    @Override
    public List<Byte> getByteList(byte serialNum) {
        List<Byte> list = new ArrayList<>();
        list.add(this.para1);
        list.add(this.para2);
        return super.setParameter(serialNum, list);
    }

    public byte getPara1() {
        return para1;
    }

    public void setPara1(byte para1) {
        this.para1 = para1;
    }

    public byte getPara2() {
        return para2;
    }

    public void setPara2(byte para2) {
        this.para2 = para2;
    }
}
