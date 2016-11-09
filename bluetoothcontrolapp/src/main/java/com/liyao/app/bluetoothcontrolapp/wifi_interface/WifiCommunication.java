package com.liyao.app.bluetoothcontrolapp.wifi_interface;

import com.liyao.app.bluetoothcontrolapp.vo.WifiImageVO;

/**
 * Created by liyao on 2016/5/24.
 */
public interface WifiCommunication {
    public void receiveCallback(WifiImageVO vo);
}