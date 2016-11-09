package com.liyao.app.homecontrolcenter.protocolframe;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyao on 2016/5/26.
 */
public abstract class SendProtocolBase {
    static final String TAG = "SendProtocolBase";

    byte head = (byte)0xfd;                  //帧头 固定
    byte reserved;               //预留
    byte plen;                  //帧长度
    byte module;                //模块代码和方向
    byte serial;                //序号
    byte action;                  //指令码
    byte checksum;              //校验和
    byte tail = (byte)0xf8 ;                 //帧尾 固定
    public SendProtocolBase(short module_action){
        this.module = (byte)(module_action >> 8);
        this.action = (byte)(module_action & 0xff);
    }
    //完整性检查
    public abstract boolean integrityChecking();
    //获取转换的byte数组
    public abstract List<Byte> getByteList(byte serialNum);

    protected List<Byte> setParameter(byte serialNum, List<Byte> para){
        this.serial = serialNum;
        this.plen = (byte) (para.size() + 3);
        List<Byte> list = new ArrayList<>();
        list.add(this.head);
        list = filterAddByte(this.reserved, list); this.checksum += this.reserved;
        list = filterAddByte(this.plen, list);     this.checksum += this.plen;
        list = filterAddByte(this.module, list);   this.checksum += this.module;
        list = filterAddByte(this.serial, list);   this.checksum += this.serial;
        list = filterAddByte(this.action, list);   this.checksum += this.action;
        for(int i = 0; i < para.size(); i++) {
            list = filterAddByte(para.get(i), list);
            this.checksum += para.get(i);
        }
        list = filterAddByte(this.checksum, list);
        list.add(this.tail);
        return list;
    }

    List<Byte> filterAddByte(byte data,List<Byte> list){
        switch (data & 0xff){
            case 0xfd:
                list.add((byte)0xfe);
                list.add((byte)0x7d);break;
            case 0xfe:
                list.add((byte)0xfe);
                list.add((byte)0x7e);break;
            case 0xf8:
                list.add((byte)0xfe);
                list.add((byte)0x78);break;
            default:
                list.add(data);
        }
        return list;
    }
}
