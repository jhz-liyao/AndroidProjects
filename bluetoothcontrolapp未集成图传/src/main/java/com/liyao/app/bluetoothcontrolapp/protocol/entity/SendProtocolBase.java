package com.liyao.app.bluetoothcontrolapp.protocol.entity;


import java.util.List;

/**
 * Created by liyao on 2016/5/26.
 */
public abstract class SendProtocolBase {
    byte head = (byte)0xfd;                  //帧头 固定
    byte type;                  //帧类型 构造函数固定
    byte serial;                //序号
    byte checksum;              //校验和
    byte tail = (byte)0xf8 ;                 //帧尾 固定
    public SendProtocolBase(byte type){
        this.type = type;
    }
    //完整性检查
    public abstract boolean integrityChecking();
    //获取转换的byte数组
    public abstract List<Byte> getByteList(byte serialNum);

    public List<Byte> filterAddByte(byte data,List<Byte> list){
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
    public byte getHead() {
        return head;
    }

    public byte getTail() {
        return tail;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getSerial() {
        return serial;
    }

    public void setSerial(byte serial) {
        this.serial = serial;
    }

    public byte getChecksum() {
        return checksum;
    }

    public void setChecksum(byte checksum) {
        this.checksum = checksum;
    }

}
