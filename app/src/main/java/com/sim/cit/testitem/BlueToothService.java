package com.sim.cit.testitem;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import com.sim.cit.BlueToothBean;
import com.sim.cit.CITTestHelper;
import com.sim.cit.R;

import java.util.ArrayList;
import java.util.List;

public class BlueToothService extends Service {
    private static final String TAG = "CIT_BluetoothService";
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice device = null;
    private IntentFilter intentFilter;
    private CITTestHelper application;
    private boolean mReceiverTag = false;  //标记receiver是否注册
    private String bluetoothList = "";
    private List<String> devices;   //用于去重

    public BlueToothService(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service onCreate()");
        application = (CITTestHelper)getApplication();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devices = new ArrayList<String>();
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(blueToothReceiver, intentFilter);
        mReceiverTag = true;
        if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {

            } else {
                Log.e(TAG,"开启蓝牙");
                mBluetoothAdapter.enable();
            }
        } else if (!mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG,"onDestroy()");
        application.setBluetoothList(bluetoothList);
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON && !application.isBlueToothOpened()) {
            mBluetoothAdapter.disable();
            Log.e(TAG,"关闭蓝牙");
        }
        if(mReceiverTag){
            mReceiverTag = false;
            unregisterReceiver(blueToothReceiver);
        }
    }

    private BroadcastReceiver blueToothReceiver=new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG,"Action: "+action);

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    Log.i(TAG, "BluetoothAdapter.getState()==true");
                    if (!mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.startDiscovery();
                    }
                }
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                bluetoothList = "";
            }

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG, device.getName() + "---" + device.getAddress());
                if (device.getBondState() != BluetoothDevice.BOND_BONDED &&
                        !devices.contains(device.getAddress())) {   //去重
                    bluetoothList += device.getName() + "———" + getString(R.string.bluetooth_mac_address) + device.getAddress()
                            + "\n";
                }
                devices.add(device.getAddress());
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                application.setBluetoothList(bluetoothList);
                devices.clear();
                mBluetoothAdapter.cancelDiscovery();
                if(mReceiverTag){
                    mReceiverTag = false;
                    unregisterReceiver(blueToothReceiver);
                }
                stopSelf();
            }
        }
    };

}
