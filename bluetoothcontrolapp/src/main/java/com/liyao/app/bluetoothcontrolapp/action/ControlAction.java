package com.liyao.app.bluetoothcontrolapp.action;

import com.liyao.app.bluetoothcontrolapp.protocol.send.RudderProtocol;
import com.liyao.app.bluetoothcontrolapp.protocol.send.RunProtocol;
import com.liyao.app.bluetoothcontrolapp.protocolframe.ProtocolManager;

import static java.lang.Math.abs;

/**
 * Created by liyao on 2016/5/26.
 */
public class ControlAction {
//    public static final int ANGLE_MAX = 180;
//    public static final int DEEPNESS_MAX = 352;

    public static final int DIR_STOP = 0;
    public static final int DIR_UP = 1;
    public static final int DIR_DOWN = 2;
    public static void controlDirection(int speed,int offset){
        RunProtocol rp = new RunProtocol();
        if(speed == 0){
            if(offset > 0){
                rp.left_dir = DIR_UP;
                rp.right_dir = DIR_DOWN;
            }else if(offset < 0){
                rp.left_dir = DIR_DOWN;
                rp.right_dir = DIR_UP;
            }else{
                rp.left_dir = DIR_STOP;
                rp.right_dir = DIR_STOP;
            }
            rp.left_speed_l =(byte)abs(offset);
            rp.right_speed_l = (byte)abs(offset);
        }else{
            if(speed > 0){//向前
                rp.left_dir = DIR_UP;
                rp.right_dir = DIR_UP;
            }else if(speed < 0){//向后
                speed = abs(speed);
                rp.left_dir = DIR_DOWN;
                rp.right_dir = DIR_DOWN;
            }
            rp.left_speed_l =(byte)(speed + speed * offset / 100);
            rp.right_speed_l = (byte)(speed - speed * offset/ 100);
        }
        ProtocolManager.sendProtocol(rp);



    }

    public static void rudderControl(int x,int y){
        RudderProtocol rudder_p = new RudderProtocol();
        rudder_p.X_Angle = (byte) x;
        rudder_p.Y_Angle = (byte) y;
        ProtocolManager.sendProtocol(rudder_p);
    }
}
