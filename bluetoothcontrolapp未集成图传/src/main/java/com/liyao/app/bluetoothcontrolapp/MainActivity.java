package com.liyao.app.bluetoothcontrolapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolUtil;
import com.liyao.app.bluetoothcontrolapp.protocol.entity.sendprotocol.RunProtocol;
import com.liyao.app.bluetoothcontrolapp.protocol.operation.ProtocolManager;
import com.liyao.app.bluetoothcontrolapp.services.BlueToothControlService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    Intent serviceIntent = null;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    List<String> findDevList = null;//搜索的设备列表
    List<String> bindDevList = null;//已配对设备列表
    BroadcastReceiver bluetoothMsgReceiver = new BroadcastReceiver() {//系统蓝牙广播接收
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String bindState = "";
                if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                    bindState = "已配对";
                else if (device.getBondState() == BluetoothDevice.BOND_NONE)
                    bindState = "未配对";
                findDevList.add(device.getName() + "    " + bindState + "\n" + device.getAddress());
                ListView lv_devlist = (ListView)findViewById(R.id.lv_devlist);
                lv_devlist.setAdapter( new ArrayAdapter<>(MainActivity.this, R.layout.main_listview_item, R.id.tv_main_listview_detail,findDevList));
                //Toast.makeText(MainActivity.this, "搜索到设备", Toast.LENGTH_SHORT).show();
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(MainActivity.this, "搜索完毕:"+ findDevList.size(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceIntent = new Intent(this, BlueToothControlService.class);
        startService(serviceIntent);//开启服务
        //增加搜索到的蓝牙列表的单击事件 ps：xml配置文件中的无效
        ((ListView)this.findViewById(R.id.lv_devlist)).setOnItemClickListener(new Tv_main_listview_onClick());
        ((ListView)this.findViewById(R.id.lv_binddevlist)).setOnItemClickListener(new Tv_main_listview_bind_onClick());
        //增加蓝牙监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothMsgReceiver, intentFilter);//注册广播接收者结果

        buildBindBluetooth();//初始化已绑定设备列表

    }

    public void buildBindBluetooth(){
//        if(true)
//        return;
        //初始化已配对列表
        bindDevList = new ArrayList<>();//已配对设备列表
        for(BluetoothDevice bd : bluetoothAdapter.getBondedDevices()) {
            bindDevList.add(bd.getName()+" 已配对" + "\n" + bd.getAddress());
        }
        ListView lv_binddevlist = (ListView)findViewById(R.id.lv_binddevlist);
        lv_binddevlist.setAdapter( new ArrayAdapter<>(MainActivity.this, R.layout.main_listview_item, R.id.tv_main_listview_detail,bindDevList));
    }
    //开始搜索按钮
    public void btn_search_onClick(View v){
//        Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 12); //3600为蓝牙设备可见时间
//        startActivityForResult(enable, 0);
        findDevList = new ArrayList<>();//搜索的设备列表
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        bluetoothAdapter.startDiscovery();
        buildBindBluetooth();
    }
    //停止搜索按钮
    public void btn_stopsearch_onClick(View v){
        bluetoothAdapter.cancelDiscovery();
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
     * 注册已绑定的蓝牙的设备列表的单击事件
     */
    public class Tv_main_listview_bind_onClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tv_main_listview_bind_onClick(view.findViewById(R.id.tv_main_listview_detail));
        }
    }

    //已绑定的设备列表单击事件
    public void tv_main_listview_bind_onClick(View v){
        TextView tv_main_listview_detail = (TextView)v;
        String deviceAddr = tv_main_listview_detail.getText().toString().split("\n")[1];
        Intent intent = new Intent(this,ControlActivity.class);
        intent.putExtra("DATA", deviceAddr);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(bluetoothMsgReceiver);//解除注册广播接收者结果
        }catch (Exception e){}
        stopService(serviceIntent);
    }

    public void btn_test_onClick(View v){
//        byte[] res = ProtocolManager.fetchProtocol(new byte[]{(byte)0xfd,(byte)0x01,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0x1,(byte)0xfe,(byte)0x7d,(byte)0xfe,(byte)0x78,(byte)0xfe,(byte)0x7e,(byte)0xfe,(byte)0x78,(byte)0xf8},new ProtocolUtil());
//        RunProtocol rp = new RunProtocol();
//        rp.left_dir = 0x01;
//        rp.left_speed_h = 0x01;
//        rp.left_speed_l = (byte)0xFD;
//        rp.integrityChecking();
//        List<Byte> list = rp.getByteList((byte)0xFD);
    }


}
