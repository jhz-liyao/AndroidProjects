package com.liyao.app.homecontrolcenter.moduleboard.WaterMachine.send_p;

import com.liyao.app.homecontrolcenter.protocolframe.ProtocolManager;
import com.liyao.app.homecontrolcenter.protocolframe.SendProtocolBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyao on 2016/10/22.
 */
public class WaterCmdProtocol extends SendProtocolBase {
    public static final byte YSJ_CLOSE = 0x00;
    public static final byte YSJ_OPEN = 0x01;

    byte cmd = 0;
    byte reserved = 0;

    public WaterCmdProtocol() {
        super(ProtocolManager.WATER_CMD_PROTOCOL);
    }

    @Override
    public boolean integrityChecking() {
        return true;
    }

    @Override
    public List<Byte> getByteList(byte serialNum) {
        List<Byte> list = new ArrayList<>();
        list.add(this.cmd);
        list.add(this.reserved);
        return super.setParameter(serialNum, list);
    }

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getReserved() {
        return reserved;
    }

    public void setReserved(byte reserved) {
        this.reserved = reserved;
    }
}
