package com.liyao.app.homecontrolcenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.liyao.app.homecontrolcenter.moduleboard.WaterMachine.WaterMachine;
import com.liyao.app.homecontrolcenter.moduleboard.WaterMachine.send_p.CmdProtocol;
import com.liyao.app.homecontrolcenter.protocolframe.ProtocolManager;
import com.liyao.app.homecontrolcenter.protocolframe.ProtocolTransferInterface;
import com.liyao.app.homecontrolcenter.protocolframe.RecvProtocolBase;
import com.liyao.app.homecontrolcenter.protocolframe.SendProtocolBase;
import com.liyao.app.homecontrolcenter.protocolframe.vo.MessageVO;
import com.liyao.app.homecontrolcenter.protocolframe.vo.TransmitDataVO;

import java.util.List;

public class MainActivity extends Activity {//extends AppCompatActivity{//
    static final String TAG = "MainActivity";
    TextView tv_messageView = null;
    ScrollView sv_messageScroll = null;

    StringBuffer messageBuffer = new StringBuffer();
    Handler flushMessageHandler = new Handler() {
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            messageBuffer.append(message);
            tv_messageView.setText(messageBuffer.toString());
            sv_messageScroll.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };
    SharedPreferences sp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_messageView = (TextView)findViewById(R.id.tv_message);
        sv_messageScroll = (ScrollView)findViewById(R.id.sv_messageScroll);
        ProtocolManager.Init();
        SocketManager.Init(new ProtocolManager.SocketCom());
        sp = getBaseContext().getSharedPreferences("MySP", Context.MODE_PRIVATE);
        EditText et_ip = (EditText)findViewById(R.id.et_ip);
        EditText et_port = (EditText)findViewById(R.id.et_port);

        if( !("".equals(sp.getString("IP","")) || "".equals(sp.getString("PORT","")))){
            et_ip.setText(sp.getString("IP",""));
            et_port.setText(sp.getString("PORT",""));
        }else{
            et_ip.setText("192.168.4.1");
            et_port.setText("8080");
        }

        new FetchMessage().start();
        new ProtocolHandle().start();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(SocketManager.state == SocketManager.SocketState.PAUSE)
            SocketManager.socketConnect(ip, port);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if(SocketManager.state == SocketManager.SocketState.PAUSE)
            SocketManager.socketConnect(ip, port);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SocketManager.state = SocketManager.SocketState.PAUSE;
        SocketManager.socketClose();
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        SocketManager.state = SocketManager.SocketState.PAUSE;
        SocketManager.socketClose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketManager.socketClose();
    }

    String ip = "";
    int port = 0;
    public void btn_connect_onClick(View v){
        ip = ((EditText)findViewById(R.id.et_ip)).getText().toString();
        port  = Integer.parseInt(((EditText)findViewById(R.id.et_port)).getText().toString());
        SocketManager.socketConnect(ip, port);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("IP",ip);
        edit.putString("PORT",String.valueOf(port));
        edit.commit();
    }

    public void btn_unconnect_onClick(View v){
        SocketManager.state = SocketManager.SocketState.CLOSE;
        SocketManager.socketClose();
    }
    public void btn_ysj_open_onClick(View v){
        WaterMachine.open();
        Toast.makeText(this, "开启饮水机", Toast.LENGTH_SHORT ).show();
    }

    public void btn_ysj_close_onClick(View v){
        WaterMachine.close();
        Toast.makeText(this, "关闭饮水机", Toast.LENGTH_SHORT ).show();
    }


    public class FetchMessage extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(true) {
                try {
                    MessageVO vo = MessageManager.fetch();
                    Message msg = new Message();
                    msg.obj = vo.getMessage()+"       "+ vo.getDateStr() + "\n";
                    Log.w(TAG, msg.obj.toString());
                    flushMessageHandler.sendMessage(msg);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public class ProtocolHandle extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(true) {
                try {
                    RecvProtocolBase rpb= ProtocolManager.fetchProtocol();
                    rpb.handle(MainActivity.this);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
