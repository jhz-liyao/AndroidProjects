package com.liyao.app.bluetoothcontrolapp.protocolframe;


import com.liyao.app.bluetoothcontrolapp.protocolframe.vo.TransmitDataVO;

/**
 * Created by liyao on 2016/10/22.
 */
public interface ProtocolTransferInterface {
    public void socketRead(TransmitDataVO vo);
    public TransmitDataVO socketSend();
}
