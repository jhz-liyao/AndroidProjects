package com.liyao.app.bluetoothcontrolapp.action;

import com.liyao.app.bluetoothcontrolapp.protocol.entity.sendprotocol.RunProtocol;
import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolManager;

/**
 * Created by liyao on 2016/5/26.
 */
public class ControlAction {
    public static final int ANGLE_MAX = 180;
    public static final int DEEPNESS_MAX = 176;
    public static void controlDirection(int angle,int deepness){
        RunProtocol rp = new RunProtocol();
        deepness = deepness * 100 / DEEPNESS_MAX;//百分化速度
        if(angle == 0 && deepness == 0 ) {
            rp.left_dir = RunProtocol.STOP_DIR;
            rp.right_dir = RunProtocol.STOP_DIR;
            rp.left_speed_l = (byte)0;
            rp.right_speed_l = (byte)0;
        }else if(angle == 0){                         //前
            rp.left_dir = RunProtocol.UP_DIR;
            rp.right_dir = RunProtocol.UP_DIR;
            rp.left_speed_l = (byte)deepness;
            rp.right_speed_l = (byte)deepness;
        }else if(angle == -180){                //后
            rp.left_dir = RunProtocol.DOWN_DIR;
            rp.right_dir = RunProtocol.DOWN_DIR;
            rp.left_speed_l = (byte)deepness;
            rp.right_speed_l = (byte)deepness;
        }else if(angle == -90){                 //左90°
            rp.left_dir = RunProtocol.DOWN_DIR;
            rp.right_dir = RunProtocol.UP_DIR;
            rp.left_speed_l = (byte)deepness;
            rp.right_speed_l = (byte)deepness;
        }else if(angle == 90){                  //右90°
            rp.left_dir = RunProtocol.UP_DIR;
            rp.right_dir = RunProtocol.DOWN_DIR;
            rp.left_speed_l = (byte)deepness;
            rp.right_speed_l = (byte)deepness;
        }else if(angle > 0 && angle < 90){      //右前方
            rp.left_dir = RunProtocol.UP_DIR;
            rp.right_dir = RunProtocol.UP_DIR;
            rp.left_speed_l = (byte)deepness;
            rp.right_speed_l = (byte)(deepness - angle);//右轮速度减去0-90
        }else if(angle > 90 && angle < 180){      //右下方
            rp.left_dir = RunProtocol.DOWN_DIR;
            rp.right_dir = RunProtocol.DOWN_DIR;
            rp.left_speed_l = (byte)deepness;
            rp.right_speed_l = (byte)(deepness - (180 - angle));//右轮速度减去0-90
        }else if(angle > -180 && angle < -90){      //左下方
            rp.left_dir = RunProtocol.DOWN_DIR;
            rp.right_dir = RunProtocol.DOWN_DIR;
            rp.left_speed_l = (byte)(deepness - (180 + angle));//左轮速度减去0-90
            rp.right_speed_l = (byte)deepness;
        }else if(angle > -90 && angle < 0){      //左上方
            rp.left_dir = RunProtocol.UP_DIR;
            rp.right_dir = RunProtocol.UP_DIR;
            rp.left_speed_l = (byte)(deepness + angle);
            rp.right_speed_l = (byte)deepness;//左轮速度减去0-90
        }
        ProtocolManager.addProtocolQueue(rp);

    }
}
