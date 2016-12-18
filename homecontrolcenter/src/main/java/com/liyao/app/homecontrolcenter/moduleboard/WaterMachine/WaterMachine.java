package com.liyao.app.homecontrolcenter.moduleboard.WaterMachine;


import com.liyao.app.homecontrolcenter.moduleboard.WaterMachine.send_p.WaterCmdProtocol;
import com.liyao.app.homecontrolcenter.protocolframe.ProtocolManager;

/**
 * Created by liyao on 2016/10/22.
 */
public class WaterMachine {
    public static void open(){
        WaterCmdProtocol cp= new WaterCmdProtocol();
        cp.setCmd(WaterCmdProtocol.YSJ_OPEN);
        ProtocolManager.sendProtocol(cp);
    }

    public static void close(){
        WaterCmdProtocol cp= new WaterCmdProtocol();
        cp.setCmd(WaterCmdProtocol.YSJ_CLOSE);
        ProtocolManager.sendProtocol(cp);

    }
}
