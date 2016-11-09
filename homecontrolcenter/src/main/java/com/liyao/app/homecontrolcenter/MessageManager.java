package com.liyao.app.homecontrolcenter;

import android.util.Log;

import com.liyao.app.homecontrolcenter.protocolframe.vo.MessageVO;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by liyao on 2016/10/23.
 */
public class MessageManager {
    static final String TAG = "MessageManager";
    static BlockingQueue<MessageVO> messageQueue = new ArrayBlockingQueue<MessageVO>(1024);

    public static void send(String message){
        try {
            messageQueue.put(new MessageVO(message));
            Log.w(TAG,message);
            Log.w(TAG,"put成功");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static MessageVO fetch(){
        try {
            MessageVO vo = messageQueue.take();
            Log.w(TAG, "take成功");
            return vo;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
