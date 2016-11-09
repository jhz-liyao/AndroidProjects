package com.liyao.app.bluetoothcontrolapp.protocol.pinterface;

import com.liyao.app.bluetoothcontrolapp.protocol.entity.RecvProtocolBase;

/**
 * Created by liyao on 2016/5/25.
 */
public interface ProtocolCom {
    public RecvProtocolBase distinguishProtocolType(byte[] protocolArray);
}
