package com.liyao.app.bluetoothcontrolapp.protocol.recv;

import android.app.Activity;
import android.widget.TextView;

import com.liyao.app.bluetoothcontrolapp.R;
import com.liyao.app.bluetoothcontrolapp.protocol.Protocol;
import com.liyao.app.bluetoothcontrolapp.protocolframe.RecvProtocolBase;

import java.util.List;

/**
 * Created by liyao on 2017/1/2.
 */

public class StateProtocol extends RecvProtocolBase {
    public static int   DataSize = 13;
    public static short ModuleAction = Protocol.STATE_PROTOCOL;
    public byte para1;
    public byte para2;
    public byte para3;
    public byte para4;
    public byte para5;
    public byte para6;
    public byte para7;
    public byte para8;
    public byte para9;
    public byte para10;
    public byte para11;
    public byte para12;
    public byte para13;
    public StateProtocol() {
        super(DataSize, ModuleAction);
    }

    @Override
    public RecvProtocolBase getEntity(List<Byte> protocolList) {
        try {
            List<Byte> para = super.MatchProtocol(protocolList);
            if(para == null)
                return null;
            this.para1 = para.get(0);
            this.para2 = para.get(1);
            this.para3 = para.get(2);
            this.para4 = para.get(3);
            this.para5 = para.get(4);
            this.para6 = para.get(5);
            this.para7 = para.get(6);
            this.para8 = para.get(7);
            this.para9 = para.get(8);
            this.para10 = para.get(9);
            this.para11 = para.get(10);
            this.para12 = para.get(11);
            this.para13 = para.get(12);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return this;
    }

    @Override
    public void handle(final Activity activity) {
        final int para1 =  this.para1;
        final int para3 =  this.para3;
        final int para4 =  this.para4;
        final int para6 =  this.para6;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //更新界面
                TextView tv_show = (TextView)activity.findViewById(R.id.tv_show);
                tv_show.setText(String.format("左方向：%d 速度：%d\r\n右方向：%d 速度：%d\r\n",para1,para3,para4,para6));
            }
        });
    }
}
