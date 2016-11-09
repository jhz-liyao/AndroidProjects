package com.liyao.app.homecontrolcenter.moduleboard.WaterMachine;


import com.liyao.app.homecontrolcenter.moduleboard.WaterMachine.send_p.CmdProtocol;
import com.liyao.app.homecontrolcenter.protocolframe.ProtocolManager;

/**
 * Created by liyao on 2016/10/22.
 */
public class WaterMachine {
    public static void open(){
        CmdProtocol cp= new CmdProtocol();
        cp.setCmd(CmdProtocol.YSJ_OPEN);
        ProtocolManager.sendProtocol(cp);
    }

    public static void close(){
        CmdProtocol cp= new CmdProtocol();
        cp.setCmd(CmdProtocol.YSJ_CLOSE);
        ProtocolManager.sendProtocol(cp);

    }
}
