package com.liyao.app.bluetoothcontrolapp.protocol.entity;


import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyao on 2016/5/25.
 */
public abstract class RecvProtocolBase {
    public List<Byte> protocolList;
    public RecvProtocolBase(int len,byte[] protocolArr) throws Exception {
        this.len = len+5;
        if (protocolArr == null)
            return;
        //解转义
        protocolList = new ArrayList<>();
        boolean FEFlag = false;
        for (int i = 0,j = 0; i < protocolArr.length; i++){
            byte tmpData = protocolArr[i];
            //转义判断
            if (tmpData == (byte) 0xfe) {
                FEFlag = true;
                continue;
            } else if (FEFlag) {
                switch ((tmpData & 0xff)) {
                    case 0x7d:
                        tmpData = (byte) 0xfd;
                        break;
                    case 0x78:
                        tmpData = (byte) 0xf8;
                        break;
                    case 0x7e:
                        tmpData = (byte) 0xfe;
                        break;
                }
                FEFlag = false;
            }
            if(j == 0)this.head = (tmpData);
            else if(j==1) {this.type = (tmpData);realsum += tmpData;}
            else if(j == (this.len-3)) {this.setSerial(tmpData);realsum += tmpData;}
            else if(j == (this.len-2)) {this.setChecksum(tmpData);}
            else if(j == (this.len-1)) {this.tail = (tmpData);}
            else { protocolList.add(tmpData); realsum += tmpData; }
            j++;//因为有continue的出现故不能使用i
        }
        if (len != protocolList.size()) {
            String pStr = "";
            for (byte b : protocolList) {
                pStr += (ProtocolUtil.hexToString(b) + " ");
            }
            throw new Exception("协议长度不符:" + pStr);
        }
        this.len = (protocolList.size());
    }

    byte head;                  //帧头 固定
    byte type;                  //帧类型 构造函数固定
    byte serial;                //序号
    byte checksum;              //校验和
    byte realsum;               //系统计算校验和
    byte tail ;                 //帧尾 固定
    int len;                    //数据长度 构造函数固定

    //完整性检查
    public boolean integrityChecking(){
        return true;
    }

    public byte getChecksum() {
        return checksum;
    }

    public byte getType() {
        return type;
    }

    public int getLen() {
        return len;
    }


    public byte getHead() {
        return head;
    }


    public byte getTail() {
        return tail;
    }

    public byte getSerial() {
        return serial;
    }

    public void setSerial(byte serial) {
        this.serial = serial;
    }

    public void setChecksum(byte checksum) {
        this.checksum = checksum;
    }

    public byte getRealsum() {
        return realsum;
    }
}
