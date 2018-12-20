package com.sim.cit.testitem;

import com.sim.cit.CITTestHelper;
import com.sim.cit.R;
import com.sim.cit.TestActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Bluetooth extends TestActivity implements OnClickListener {
    private static final String TAG = "CIT_Bluetooth";
    //Add for avoiding the duplicate clicking in short time by lvhongshan 20140128 start
    private static long lastClickTime = 0;
    //Add for avoiding the duplicate clicking in short time by lvhongshan 20140128 end
    private BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice device = null;
    private Button passButton;
    private Button discovery_device_button;
    private TextView text_bluetooth_info;
    private TextView text_bluetooth_scanning;
    private TextView text_bluetooth_finished;
    IntentFilter intentFilter;
    CITTestHelper applicaiton;
    private boolean mReceiverTag = false;  //标记register是否注册
    private String bluetoothList = "";
    private List<String> devices;   //用于去重

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.bluetooth_setingpage;
        super.onCreate(savedInstanceState);
        applicaiton = (CITTestHelper) getApplication();
        passButton = (Button) findViewById(R.id.btn_pass);
        discovery_device_button = (Button) findViewById(R.id.btn_discovery_device);
        text_bluetooth_info = (TextView) findViewById(R.id.tv_bluetooth_info);
        text_bluetooth_scanning = (TextView) findViewById(R.id.tv_bluetooth_scanning);
        text_bluetooth_finished = (TextView) findViewById(R.id.tv_bluetooth_finished);

        discovery_device_button.setText("Search Devices");
        discovery_device_button.setOnClickListener(this);
        passButton.setEnabled(false);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devices = new ArrayList<String>();

        Intent stopBlueToothService = new Intent(Bluetooth.this, BlueToothService.class);
        stopService(stopBlueToothService);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                intentFilter = new IntentFilter();
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
                registerReceiver(blueToothReceiver, intentFilter);
                mReceiverTag = true;
                //add for show blueTooth quickly by lxx 20180727
                if (applicaiton.getBluetoothList() != null && !applicaiton.getBluetoothList().equals("")) {
                    passButton.setEnabled(true);
                    bluetoothList = applicaiton.getBluetoothList();
                    text_bluetooth_info.setText(bluetoothList);
                } else if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
                    if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                        discovery_device_button.setText("OPEN BT");
                    } else {
                        mBluetoothAdapter.enable();
                    }
                } else if (!mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.startDiscovery();
                }
            }
        }, 15);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            applicaiton.setBluetoothList(bluetoothList);
            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON && !applicaiton.isBlueToothOpened()) {
                mBluetoothAdapter.disable();
                Log.e(TAG,"关闭蓝牙");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if (mReceiverTag) {
            mReceiverTag = false;
            unregisterReceiver(blueToothReceiver);
        }
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON && !applicaiton.isBlueToothOpened()) {
            mBluetoothAdapter.disable();
            Log.e(TAG,"关闭蓝牙");
        }
        mBluetoothAdapter = null;
    }

    private BroadcastReceiver blueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG,"Action: "+action);

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    discovery_device_button.setText("Search Devices");
                    Log.e(TAG, "BluetoothAdapter.getState()==true");
                    if (!mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.startDiscovery();
                    }
                }
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                bluetoothList = "";
                text_bluetooth_info.setText(bluetoothList);
                text_bluetooth_scanning.setVisibility(View.VISIBLE);
                text_bluetooth_finished.setVisibility(View.INVISIBLE);
                discovery_device_button.setEnabled(false);
            }

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                passButton.setEnabled(true);
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG, device.getName() + "---" + device.getAddress());
                if (device.getBondState() != BluetoothDevice.BOND_BONDED &&
                        !devices.contains(device.getAddress())) {   //去重
                    bluetoothList += device.getName() + "———" + getString(R.string.bluetooth_mac_address) + device.getAddress()
                            + "\n";
                    text_bluetooth_info.setText(bluetoothList);
                }
                devices.add(device.getAddress());
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                applicaiton.setBluetoothList(bluetoothList);
                devices.clear();
                discovery_device_button.setEnabled(true);
                text_bluetooth_scanning.setVisibility(View.INVISIBLE);
                text_bluetooth_finished.setVisibility(View.VISIBLE);
                mBluetoothAdapter.cancelDiscovery();
            }
        }
    };

    @Override
    public void onClick(View v) {
        //Modify for avoiding the duplicate clicking in short time by lvhongshan 20140128 start
        if (isFastDoubleClick()) {
            return;
        } else {
            if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
                mBluetoothAdapter.enable();
            } else if (!mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.startDiscovery();
            }
        }
        //Modify for avoiding the duplicate clicking in short time by lvhongshan 20140128 end
    }


    //Add for avoiding the duplicate clicking in short time by lvhongshan 20140128 start
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    //Add for avoiding the duplicate clicking in short time by lvhongshan 20140128 end
}
