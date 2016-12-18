package com.liyao.app.homecontrolcenter.moduleboard.Gateway;

import com.liyao.app.homecontrolcenter.moduleboard.Gateway.send_p.CmdProtocol;
import com.liyao.app.homecontrolcenter.moduleboard.WaterMachine.send_p.WaterCmdProtocol;
import com.liyao.app.homecontrolcenter.protocolframe.ProtocolManager;

/**
 * Created by liyao on 2016/12/13.
 */

public class Gateway {
    public static void GetDevState(){
        CmdProtocol cp= new CmdProtocol();
        cp.setPara1(CmdProtocol.WaterGetState);
        ProtocolManager.sendProtocol(cp);
        cp= new CmdProtocol();
        cp.setPara1(CmdProtocol.SoilSensorGetState);
        ProtocolManager.sendProtocol(cp);
    }
}
