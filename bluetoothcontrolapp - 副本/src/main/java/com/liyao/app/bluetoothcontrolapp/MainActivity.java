package com.liyao.app.bluetoothcontrolapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liyao.app.bluetoothcontrolapp.receiver.BluetoothReceiver;
import com.liyao.app.bluetoothcontrolapp.services.BlueToothControlService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    public static String ACTION_SEARCH_DONE = "BluetoothReceiver.action_search_done";
    public static String ACTION_SEARCH_INFO = "BluetoothReceiver.action_search_info";
    Intent serviceIntent = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    List<String> findDevList = new ArrayList<>();//搜索的设备列表
    List<String> bindDevList = new ArrayList<>();//已配对设备列表
    BroadcastReceiver bluetoothMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BlueToothControlService.ACTION_BACK_BLUETOOTHBIND.equals(intent.getAction())){
                bindDevList.add(intent.getStringExtra("DATA"));
                ListView lv_binddevlist = (ListView)findViewById(R.id.lv_binddevlist);
                lv_binddevlist.setAdapter( new ArrayAdapter<>(MainActivity.this, R.layout.main_listview_item, R.id.tv_main_listview_detail,bindDevList));
                //Toast.makeText(MainActivity.this, "搜索到设备", Toast.LENGTH_SHORT).show();
            }else if(ACTION_SEARCH_INFO.equals(intent.getAction())){
                findDevList.add(intent.getStringExtra("DATA"));
                ListView lv_devlist = (ListView)findViewById(R.id.lv_devlist);
                lv_devlist.setAdapter( new ArrayAdapter<>(MainActivity.this, R.layout.main_listview_item, R.id.tv_main_listview_detail,findDevList));
                //Toast.makeText(MainActivity.this, "搜索到设备", Toast.LENGTH_SHORT).show();
            }else if(ACTION_SEARCH_DONE.equals(intent.getAction())){
//                Set<BluetoothDevice> deviceSet;
//                deviceSet = bluetoothAdapter.getBondedDevices();
                Toast.makeText(MainActivity.this, "搜索完毕"+ findDevList.size(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, ControlActivity.class);
        startActivity(intent);
        if(1==1)
        return;

        serviceIntent = new Intent(this, BlueToothControlService.class);
        startService(serviceIntent);//开启服务
        //增加搜索到的蓝牙列表的单击事件 ps：xml配置文件中的无效
        ((ListView)this.findViewById(R.id.lv_devlist)).setOnItemClickListener(new Tv_main_listview_onClick());
        ((ListView)this.findViewById(R.id.lv_binddevlist)).setOnItemClickListener(new Tv_main_listview_onClick());
        //增加蓝牙监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SEARCH_INFO);
        intentFilter.addAction(ACTION_SEARCH_DONE);
        intentFilter.addAction(BlueToothControlService.ACTION_BACK_BLUETOOTHBIND);
        registerReceiver(bluetoothMsgReceiver, intentFilter);//注册广播接收者结果
    }
    //开始搜索按钮
    public void btn_search_onClick(View v){
//        Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 12); //3600为蓝牙设备可见时间
//        startActivityForResult(enable, 0);
        findDevList = new ArrayList<>();//搜索的设备列表
        sendBroadcast(new Intent().setAction(BlueToothControlService.ACTION_CALL_BLUETOOTHFIND));
        //初始化已配对列表
        bindDevList = new ArrayList<>();//已配对设备列表
        sendBroadcast(new Intent().setAction(BlueToothControlService.ACTION_CALL_BLUETOOTHBIND));
    }
    //停止搜索按钮
    public void btn_stopsearch_onClick(View v){
        bluetoothAdapter.cancelDiscovery();
    }

    //搜索到的设备列表单击事件
    public void tv_main_listview_detail_onClick(View v){
        TextView tv_main_listview_detail = (TextView)v;
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(tv_main_listview_detail.getText().toString().split("\n")[1]);
        if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {//已配对
            Intent intent = new Intent(this, ShowWinActivity.class);
            intent.putExtra("DATA", bluetoothDevice.getAddress());
            this.startActivity(intent);


        }else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE){//未配对
            //Toast.makeText(MainActivity.this, "未配对", Toast.LENGTH_SHORT).show();
            try {
                ClsUtils.createBond(BluetoothDevice.class, bluetoothDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(MainActivity.this, "其他", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 注册搜索到的蓝牙的设备列表的单击事件
     */
    public class Tv_main_listview_onClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tv_main_listview_detail_onClick(view.findViewById(R.id.tv_main_listview_detail));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(bluetoothMsgReceiver);//解除注册广播接收者结果
        }catch (Exception e){}
        stopService(serviceIntent);
    }


}
