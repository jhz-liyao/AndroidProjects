package com.liyao.app.bluetoothcontrolapp.bluetoothinterface;

import com.liyao.app.bluetoothcontrolapp.vo.TransmitDataVO;

/**
 * Created by liyao on 2016/5/24.
 */
public interface BluetoothCommunication{
    public void receiveCallback(TransmitDataVO vo);
}