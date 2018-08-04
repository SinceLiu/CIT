package com.sim.cit.testitem;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.sim.cit.BlueToothBean;
import com.sim.cit.CITTestHelper;
import com.sim.cit.R;

public class BlueToothService extends Service {
    private BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice device = null;
    IntentFilter intentFilter;
    CITTestHelper application;

    public BlueToothService(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("BlueToothService", "Service onCreate()");
        application = (CITTestHelper)getApplication();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(blueToothReceiver, intentFilter);
        if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {

            } else {
                mBluetoothAdapter.enable();
            }
        } else if (!mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            mBluetoothAdapter.disable();
        }
        unregisterReceiver(blueToothReceiver);
    }

    private BroadcastReceiver blueToothReceiver=new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    Log.i("progress", "BluetoothAdapter.getState()==true");
                    if (!mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.startDiscovery();
                    }
                }
                /*if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    mBluetoothAdapter.enable();
                }*/
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device= intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBluetoothAdapter.cancelDiscovery();
                mHandler.sendMessage(mHandler.obtainMessage());
                Log.i("progress", "BluetoothDevice.ACTION_FOUND device="+device.getAddress());
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mBluetoothAdapter.cancelDiscovery();
                mHandler.sendMessage(mHandler.obtainMessage());
                Log.i("progress", "BluetoothDevice.ACTION_DISCOVERY_FINISHED");
            }
        }
    };
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(device!=null){
                BlueToothBean blueTooth = new BlueToothBean(device.getAddress(),device.getName());
                application.setBlueTooth(blueTooth);
            }
            /*Intent i = new Intent("android.settings.BLUETOOTH_SETTINGS");
            startActivity(i);*/
        }
    };

}
