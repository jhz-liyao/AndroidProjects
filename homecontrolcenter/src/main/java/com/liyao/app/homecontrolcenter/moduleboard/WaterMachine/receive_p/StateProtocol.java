package com.liyao.app.homecontrolcenter.moduleboard.WaterMachine.receive_p;

import android.app.Activity;
import android.util.Log;

import com.liyao.app.homecontrolcenter.MessageManager;
import com.liyao.app.homecontrolcenter.protocolframe.RecvProtocolBase;
import com.liyao.app.homecontrolcenter.protocolframe.ProtocolManager;

import java.util.List;

/**
 * Created by liyao on 2016/10/22.
 */
public class StateProtocol extends RecvProtocolBase {
    public static int   DataSize = 1;
    public static short ModuleAction = ProtocolManager.STATE_PROTOCOL;
    public static final String STATE_CODE0 = "待机";
    public static final String STATE_CODE2 = "烧水完毕";
    public static final String STATE_CODE3 = "烧水中";

    public StateProtocol(){
        super(DataSize, ModuleAction);

    }


    public byte state;

    @Override
    public RecvProtocolBase getEntity(List<Byte> protocolList) {
        try {
            List<Byte> para = super.MatchProtocol(protocolList);
            if(para == null)
                return null;
            this.state = para.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


        return this;
    }

    @Override
    public void handle(Activity activity) {
        if(state == (byte)0x0 )
            MessageManager.send("饮水机待机");
        else if(state == (byte)0x10)
            MessageManager.send("饮水机水已烧开");
        else if(state == (byte)0x11)
            MessageManager.send("饮水机正在烧水");


        Log.e("receive:","接收函数处理方法被调用");
    }
}
