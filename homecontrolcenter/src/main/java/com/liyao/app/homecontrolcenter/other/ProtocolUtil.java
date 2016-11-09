package com.liyao.app.homecontrolcenter.other;


import com.liyao.app.homecontrolcenter.protocolframe.RecvProtocolBase;

/**
 * Created by liyao on 2016/5/26.
 */
public class ProtocolUtil {//implements ProtocolCom {
    public static final byte RUN_PROTOCOL_SEND_TYPE = 0x01;
    public static final byte RUDDER_PROTOCOL_SEND_TYPE = 0x06;

    public static final byte STATE_PROTOCOL_RECV_TYPE = 0x01;
    //@Override
    public RecvProtocolBase distinguishProtocolType(byte[] protocolArray) {
        if (protocolArray == null || protocolArray.length == 0)
            return null;
        try {
            switch (protocolArray[1] & 0xFF) {
                case STATE_PROTOCOL_RECV_TYPE:
                    RecvProtocolBase sp = null;//new StateProtocol(protocolArray);
                    if (sp.integrityChecking())
                        return sp;
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] arrayAppend(byte[] srcArray, byte[] tarArray){
        byte[] res = new byte[srcArray.length + tarArray.length];
        System.arraycopy(srcArray, 0, res, 0, srcArray.length);
        System.arraycopy(tarArray, 0, res, srcArray.length, tarArray.length);
        return res;
    }

    public static String hexToString(byte data){
        String hex = Integer.toHexString(data & 0xFF);
        if (hex.length() == 1)
        {
            hex = '0' + hex;
        }
        return "0x"+hex;
    }
}
