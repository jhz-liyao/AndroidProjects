package com.liyao.app.bluetoothcontrolapp.protocol;

import com.liyao.app.bluetoothcontrolapp.protocol.recv.StateProtocol;

import static com.liyao.app.bluetoothcontrolapp.protocolframe.ProtocolManager.receiveRegister;

/**
 * Created by liyao on 2017/1/2.
 */

public class Protocol {
    //模块
    public static final byte PAD_MODULE = 0x01;
    public static final byte RASPBERRY_MODULE = 0x02;
    public static final byte MOTOR_MODULE = 0x03;
    /*发送协议列表*/
    public static final short RUN_PROTOCOL = (short) ((PAD_MODULE << 4 | MOTOR_MODULE) << 8 |0x01);//运动控制协议
    public static final short RUDDER_PROTOCOL = (short) ((PAD_MODULE << 4 | MOTOR_MODULE) << 8 |0x02);//舵机控制协议
    /*接收协议列表*/
    public static final short STATE_PROTOCOL = (short) ((MOTOR_MODULE << 4 | PAD_MODULE) << 8 |0x01);//运动状态上报
    public static void Init(){
        receiveRegister(new StateProtocol()); //接收协议注册
        //receiveRegister(new StateProtocol()); //接收协议注册
    }
}
