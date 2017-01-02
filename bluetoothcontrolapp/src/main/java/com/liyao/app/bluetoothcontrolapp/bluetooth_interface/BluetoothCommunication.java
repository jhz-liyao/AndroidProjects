package com.liyao.app.bluetoothcontrolapp.bluetooth_interface;

import com.liyao.app.bluetoothcontrolapp.protocolframe.vo.TransmitDataVO;

/**
 * Created by liyao on 2016/5/24.
 */
public interface BluetoothCommunication{
    public void receiveCallback(TransmitDataVO vo);


}