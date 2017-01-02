package com.liyao.app.homecontrolcenter.moduleboard.Coor_DHT11.receive_p;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.liyao.app.homecontrolcenter.R;
import com.liyao.app.homecontrolcenter.protocolframe.ProtocolManager;
import com.liyao.app.homecontrolcenter.protocolframe.RecvProtocolBase;

import java.util.List;

/**
 * Created by liyao on 2016/10/22.
 */
public class DHT11Protocol extends RecvProtocolBase {
    public static int   DataSize = 2;
    public static short ModuleAction = ProtocolManager.DHT11_STATE_PROTOCOL;

    public DHT11Protocol(){
        super(DataSize, ModuleAction);
    }


    public byte temperature;
    public byte humidity;

    @Override
    public RecvProtocolBase getEntity(List<Byte> protocolList) {
        try {
            List<Byte> para = super.MatchProtocol(protocolList);
            if(para == null)
                return null;
            this.temperature = para.get(0);
            this.humidity = para.get(1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


        return this;
    }

    @Override
    public void handle(final Activity activity) {
        final int temperature =  this.temperature;
        final int humidity =  this.humidity;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //更新界面
                TextView tv_temperature = (TextView)activity.findViewById(R.id.tv_temperature);
                TextView tv_humidity = (TextView)activity.findViewById(R.id.tv_humidity);
                tv_temperature.setText("温度：" + String.valueOf(temperature));
                tv_humidity.setText("湿度：" + String.valueOf(humidity));
            }
        });
        Log.i("ProtocolHandle","温湿度数据接收函数处理方法被调用");
    }
}
