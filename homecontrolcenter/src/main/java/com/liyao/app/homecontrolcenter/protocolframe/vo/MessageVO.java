package com.liyao.app.homecontrolcenter.protocolframe.vo;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by liyao on 2016/10/23.
 */
public class MessageVO {
    SimpleDateFormat    formatter    =   new SimpleDateFormat("HH:mm:ss");//new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    public MessageVO(){
        date = new Date(System.currentTimeMillis());//获取当前时间
        dateStr = formatter.format(date);
    }

    public MessageVO(String _message){
        message = _message;
        date = new Date(System.currentTimeMillis());//获取当前时间
        dateStr = formatter.format(date);
    }
    String message = "";
    Date date;
    String dateStr;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
}
