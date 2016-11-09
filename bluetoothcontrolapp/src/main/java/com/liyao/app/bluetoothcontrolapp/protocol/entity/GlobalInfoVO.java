package com.liyao.app.bluetoothcontrolapp.protocol.entity;

import com.liyao.app.bluetoothcontrolapp.protocol.entity.sendprotocol.RunProtocol;

/**
 * Created by liyao on 2016/5/26.
 */
public class GlobalInfoVO {
    public static int angle = 0;//摇杆角度
    public static int deepness = 0;//摇杆深度

    public static int left_dir = 0;//左轮方向
    public static int left_speed = 0;//左轮速度
    public static int right_dir = 0;//右轮方向
    public static int right_speed = 0;//右轮速度

    public static int rudder_angle = 0;//舵机角度

    public static String serialState = "正常";


    public static int getAngle() {
        return angle;
    }

    public static int getDeepness() {
        return deepness;
    }

    public static String getLeft_dir() {
        if(left_dir == RunProtocol.UP_DIR)
            return "前";
        if(left_dir == RunProtocol.DOWN_DIR)
            return "后";
        if(left_dir == RunProtocol.STOP_DIR)
            return "停";
        return "错误";
    }

    public static int getLeft_speed() {
        return left_speed;
    }

    public static String getRight_dir() {
        if(right_dir == RunProtocol.UP_DIR)
            return "前";
        if(right_dir == RunProtocol.DOWN_DIR)
            return "后";
        if(right_dir == RunProtocol.STOP_DIR)
            return "停";
        return "错误";
    }

    public static int getRight_speed() {
        return right_speed;
    }

    public static int getRudder_angle() {
        return rudder_angle;
    }
}
