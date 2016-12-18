package com.liyao.app.homecontrolcenter.moduleboard.SoilSensor.receive_p;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.liyao.app.homecontrolcenter.MessageManager;
import com.liyao.app.homecontrolcenter.R;
import com.liyao.app.homecontrolcenter.protocolframe.ProtocolManager;
import com.liyao.app.homecontrolcenter.protocolframe.RecvProtocolBase;

import java.util.List;

/**
 * Created by liyao on 2016/10/22.
 */
public class SoilSensorStateProtocol extends RecvProtocolBase {
    public static int   DataSize = 8;
    public static short ModuleAction = ProtocolManager.SOIL_SENSOR_STATE_PROTOCOL;

    public SoilSensorStateProtocol(){
        super(DataSize, ModuleAction);
    }


    public byte vcc;
    public byte adc1;
    public byte adc2;
    public byte adc3;
    public byte adc4;
    public byte adc5;
    public byte adc6;
    public byte adc7;

    @Override
    public RecvProtocolBase getEntity(List<Byte> protocolList) {
        try {
            List<Byte> para = super.MatchProtocol(protocolList);
            if(para == null)
                return null;
            this.vcc = para.get(0);
            this.adc1 = para.get(1);
            this.adc2 = para.get(2);
            this.adc3 = para.get(3);
            this.adc4 = para.get(4);
            this.adc5 = para.get(5);
            this.adc6 = para.get(6);
            this.adc7 = para.get(7);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


        return this;
    }

    @Override
    public void handle(final Activity activity) {
        final int vcc =  this.vcc;
        final int adc1 =  this.adc1;
        final int adc2 =  this.adc2;
        final int adc3 =  this.adc3;
        final int adc4 =  this.adc4;
        final int adc5 =  this.adc5;
        final int adc6 =  this.adc6;
        final int adc7 =  this.adc7;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //更新界面
                TextView tv_vcc = (TextView)activity.findViewById(R.id.tv_labv0);
                TextView tv_adc1 = (TextView)activity.findViewById(R.id.tv_labv1);
                TextView tv_adc2 = (TextView)activity.findViewById(R.id.tv_labv2);
                TextView tv_adc3 = (TextView)activity.findViewById(R.id.tv_labv3);
                TextView tv_adc4 = (TextView)activity.findViewById(R.id.tv_labv4);
                TextView tv_adc5 = (TextView)activity.findViewById(R.id.tv_labv5);
                TextView tv_adc6 = (TextView)activity.findViewById(R.id.tv_labv6);
                TextView tv_adc7 = (TextView)activity.findViewById(R.id.tv_labv7);
                tv_vcc.setText(String.valueOf(vcc));
                tv_adc1.setText(String.valueOf(adc1));
                tv_adc2.setText(String.valueOf(adc2));
                tv_adc3.setText(String.valueOf(adc3));
                tv_adc4.setText(String.valueOf(adc4));
                tv_adc5.setText(String.valueOf(adc5));
                tv_adc6.setText(String.valueOf(adc6));
                tv_adc7.setText(String.valueOf(adc7));
            }
        });
//        if(state == (byte)0x0 )
//            MessageManager.send("饮水机待机");
//        else if(state == (byte)0x10)
//            MessageManager.send("饮水机水已烧开");
//        else if(state == (byte)0x11)
//            MessageManager.send("饮水机正在烧水");
        Log.i("ProtocolHandle","土壤监测数据接收函数处理方法被调用");
    }
}
