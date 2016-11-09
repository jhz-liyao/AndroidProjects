package com.liyao.app.bluetoothcontrolapp.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.liyao.app.bluetoothcontrolapp.BluetoothClient;
import com.liyao.app.bluetoothcontrolapp.bluetoothinterface.BluetoothCommunication;
import com.liyao.app.bluetoothcontrolapp.vo.TransmitDataVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyao on 2016/5/20.
 */
public class BlueToothControlService extends Service {
    public static final String TAG = "BlueToothControlService";
//    public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
//    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
//    public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
//    public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";
//    private static final UUID HC_05_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    public static final String ACTION_CALL_BLUETOOTHFIND    = "bluetoothcontrolapp.bluetoothcontrolservice.call.bluetoothfind";
//    public static final String ACTION_CALL_BLUETOOTHBIND = "bluetoothcontrolapp.bluetoothcontrolservice.call.bluetoothbind";
//    public static final String ACTION_BACK_BLUETOOTHBIND = "bluetoothcontrolapp.bluetoothcontrolservice.back.bluetoothbind";
//    ServiceReceiver serviceReceiver = new ServiceReceiver();
//    BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
    static Context context = null;
    BluetoothClient bluetoothClient = null;
    private BluetoothAdapter bluetoothAdapter = null;
    BluetoothDevice bluetoothDevice = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.w(TAG,"onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        BluetoothClientManager.closeBluetoothClient(bluetoothDevice);
        super.onUnbind(intent);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.w(TAG,"onDestroy");
    }

    public class ServiceBinder extends Binder {
        public void setBluetoothCommunication(String bluetoothAddr,BluetoothCommunication bluetoothCommunication){//注册回调函数
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(bluetoothAddr);
            bluetoothClient = BluetoothClientManager.openBluetoothClient(bluetoothDevice);
            bluetoothClient.bluetoothCommunication = bluetoothCommunication;
        }

        public void sendBluetoothData(TransmitDataVO data){
            bluetoothClient.sendBluetoothData(data);
        }
    }

    /**
     * BluetoothClient连接池 提供设备socket的初始化打开和关闭
     */
    public static class BluetoothClientManager{
        static List<BluetoothClient> bluetoothClients = new ArrayList<>();
        public static BluetoothClient openBluetoothClient(BluetoothDevice bluetoothDevice){
            for(BluetoothClient tmpBC : bluetoothClients ){
                if(bluetoothDevice.getAddress().equals(tmpBC.bluetoothDevice.getAddress()))
                    tmpBC.start();
                    return tmpBC;
            }
            BluetoothClient bc = new BluetoothClient(context,bluetoothDevice);
            bluetoothClients.add(bc);
            bc.start();
            return bc;
        }
        public static void closeBluetoothClient(BluetoothDevice bluetoothDevice){
            for(int i = 0; i < bluetoothClients.size(); i++){
                BluetoothDevice tmpBD = bluetoothClients.get(i).bluetoothDevice;
                if(bluetoothDevice.getAddress().equals(tmpBD.getAddress())){
                    bluetoothClients.get(i).stop();
                    bluetoothClients.remove(i);
                    return;
                }
            }
        }
    }
}
