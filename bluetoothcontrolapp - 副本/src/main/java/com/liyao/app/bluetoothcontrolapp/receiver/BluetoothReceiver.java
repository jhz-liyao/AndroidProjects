package com.liyao.app.bluetoothcontrolapp.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.liyao.app.bluetoothcontrolapp.MainActivity;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by liyao on 2016/5/18.
 */
public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String bluetoothDeviceInfo = null;

        if(BluetoothDevice.ACTION_FOUND.equals(action)){//设备被发现
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String bindState = "";
            if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                bindState = "已配对";
            else if (device.getBondState() == BluetoothDevice.BOND_NONE)
                bindState = "未配对";
            // 将设备名称和地址放入array adapter，以便在ListView中显示
            bluetoothDeviceInfo = device.getName() + "    " + bindState + "\n" + device.getAddress();
            Intent intent1 = new Intent(MainActivity.ACTION_SEARCH_INFO);
            intent1.putExtra("DATA", bluetoothDeviceInfo);
            context.sendBroadcast(intent1);
            Log.v("搜索到设备",bluetoothDeviceInfo);
        }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){//搜索完毕
            Intent intent1 = new Intent(MainActivity.ACTION_SEARCH_DONE);
            intent1.putExtra("DATA", bluetoothDeviceInfo);
            context.sendBroadcast(intent1);
        }else{
            //Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
        }
    }
}
