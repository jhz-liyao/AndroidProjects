package com.liyao.app.bluetoothcontrolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.liyao.app.bluetoothcontrolapp.bluetooth_activity.BluetoothActivity;
import com.liyao.app.bluetoothcontrolapp.wifi_activity.Fpv_Activity;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sp ;
    SharedPreferences.Editor edit;

    EditText et_ip = null;
    EditText et_port = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = this.getSharedPreferences("AppInfo", Context.MODE_PRIVATE);
        et_ip = (EditText)findViewById(R.id.et_ip);
        et_port = (EditText)findViewById(R.id.et_port);
        if(sp.contains("IP")){
            et_ip.setText(sp.getString("IP",""));
            et_port.setText(sp.getString("PORT",""));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void btn_bluecontrol_onClick(View v){
        Toast.makeText(MainActivity.this, "进入蓝牙控制器", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, BluetoothActivity.class);
        startActivity(intent);
    }

    public void btn_wificontrol_onClick(View v){
        Toast.makeText(MainActivity.this, "进入WIFI控制器", Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, "进入图传测试界面", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Fpv_Activity.class);

        //获取ip和端口
        String IP = et_ip.getText().toString();
        int PORT = Integer.parseInt(et_port.getText().toString());
        //保存ip和端口
        edit = sp.edit();
        edit.clear();
        edit.putString("IP",et_ip.getText().toString());
        edit.putString("PORT",et_port.getText().toString());
        edit.commit();
        //传参ip和端口
        intent.putExtra("IP",IP);
        intent.putExtra("PORT",PORT);
        startActivity(intent);
    }



    public void btn_test_onClick(View v){

    }
}
