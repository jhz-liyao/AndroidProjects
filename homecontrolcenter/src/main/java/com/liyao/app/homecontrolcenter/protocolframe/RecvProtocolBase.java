package com.liyao.app.homecontrolcenter.protocolframe;

import android.app.Activity;
import android.util.Log;

import com.liyao.app.homecontrolcenter.other.ProtocolUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyao on 2016/5/25.
 */
public abstract class RecvProtocolBase {
    static final String TAG = "RecvProtocolBase";
    public RecvProtocolBase(int len,short _ModuleAction){
        ModuleAction = _ModuleAction;
        this.len = len + 8;
    }


    public List<Byte> MatchProtocol(List<Byte> protocolList) throws Exception {
        if (protocolList == null)
            return null;
        //解转义
        List<Byte> parameterList = new ArrayList<Byte>();
        boolean FEFlag = false;
        realsum = 0;
        for (int i = 0,match_state = 0; i < protocolList.size(); i++){
            byte tmpData = protocolList.get(i);
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

            switch (match_state){
                case 0://获取帧头
                    this.head = (tmpData); match_state++; break;
                case 1://获取预留
                    this.reserved = (tmpData);realsum += tmpData; match_state++; break;
                case 2://获取帧长
                    this.plen = (tmpData);realsum += tmpData; match_state++; break;
                case 3://获取目标模块和方向
                    this.module = (tmpData);realsum += tmpData; match_state++; break;
                case 4://获取历史编号
                    this.serial = (tmpData);realsum += tmpData; match_state++; break;
                case 5://获取指令动作
                    this.action =  (tmpData);realsum += tmpData; match_state++; break;
                case 6://获取参数列表
                    parameterList.add(tmpData); realsum += tmpData;
                    if(i == this.len - 3)
                        match_state++;
                    break;
                case 7:
                    this.checksum = (tmpData); match_state++; break;

                case 8:
                    this.tail = (tmpData); match_state++; break;
            }
        }
        if (len - 8 != parameterList.size()) {
            String pStr = "";
            for (byte b : protocolList) {
                pStr += (ProtocolUtil.hexToString(b) + " ");
            }
            Log.e(TAG, "协议长度不符:" + pStr);
            return null;
        }

        if(this.checksum != realsum){
            Log.e(TAG, "校验和校验失败:"+realsum);
            return null;
        }
        return parameterList;
    }


    byte head;                  //帧头 固定
    byte reserved;               //预留
    byte plen;                  //帧长度
    byte module;                //模块代码和方向
    byte serial;                //序号
    byte action;                  //指令码
    byte checksum;              //校验和
    byte realsum;               //系统计算校验和
    byte tail ;                 //帧尾 固定
    int len;                    //数据长度 构造函数固定

    public short ModuleAction = 0;
    //完整性检查
    public boolean integrityChecking(){
        return true;
    }
    public abstract RecvProtocolBase getEntity(List<Byte> protocolList);
    public abstract void handle(Activity activity);
}
