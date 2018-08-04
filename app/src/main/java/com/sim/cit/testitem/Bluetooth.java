package com.sim.cit.testitem;

import com.sim.cit.BlueToothBean;
import com.sim.cit.CITTestHelper;
import com.sim.cit.R;
import com.sim.cit.TestActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Bluetooth extends TestActivity implements OnClickListener{
    private static final String TAG = "CIT_Bluetooth";
    //Add for avoiding the duplicate clicking in short time by lvhongshan 20140128 start
    private static long lastClickTime = 0;
    //Add for avoiding the duplicate clicking in short time by lvhongshan 20140128 end
    private ProgressDialog mProgressDialog;
    private BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice device=null;
    private Button passButton;
    private Button discovery_device_button;
    private TextView text_bluetooth_info;
    IntentFilter intentFilter;
    BlueToothBean blueTooth;
    CITTestHelper applicaiton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.bluetooth_setingpage;
        super.onCreate(savedInstanceState);
        applicaiton = (CITTestHelper)getApplication();
        passButton=(Button)findViewById(R.id.btn_pass);
        discovery_device_button=(Button)findViewById(R.id.btn_discovery_device);
        text_bluetooth_info=(TextView)findViewById(R.id.tv_bluetooth_info);
        discovery_device_button.setText("Search Devices");
        discovery_device_button.setOnClickListener(this);
        passButton.setEnabled(false);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        //mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.wait));

        //add for show blueTooth quickly by lxx 20180727
        if(applicaiton.getBlueTooth()!=null){
            passButton.setEnabled(true);
            blueTooth = applicaiton.getBlueTooth();
            text_bluetooth_info.setText(getString(R.string.bluetooth_setingpage)+"\n"+"Address="+blueTooth.getAddress()+"\n"+
                    "name ="+blueTooth.getName());
        }else {
            mProgressDialog.show();
        }
        intentFilter=new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(blueToothReceiver,intentFilter);
        if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
            if (mBluetoothAdapter.getState()==BluetoothAdapter.STATE_TURNING_OFF) {
                mProgressDialog.dismiss();
                discovery_device_button.setText("OPEN BT");
            }else {
                mBluetoothAdapter.enable();
            }

        }else if (!mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.startDiscovery();
        }
    }
    @Override
    protected void onPause() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        super.onPause();
    }
    @Override
    protected void onStop() {
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            mBluetoothAdapter.disable();
        }
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
		 unregisterReceiver(blueToothReceiver);
    }
    private BroadcastReceiver blueToothReceiver=new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    discovery_device_button.setText("Search Devices");
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
                text_bluetooth_info.setText(getString(R.string.bluetooth_setingpage)+"\n"+"Address="+device.getAddress()+"\n"+
                                "name ="+device.getName());
                mBluetoothAdapter.cancelDiscovery();
                Toast.makeText(Bluetooth.this, "find divece name="+device.getAddress(), Toast.LENGTH_SHORT).show();
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
            if (device!=null) {
                passButton.setEnabled(true);
            }
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            /*Intent i = new Intent("android.settings.BLUETOOTH_SETTINGS");
            startActivity(i);*/
        }
    };
    @Override
    public void onClick(View v) {
    //Modify for avoiding the duplicate clicking in short time by lvhongshan 20140128 start
        if (isFastDoubleClick()) {
            return;
        }else{
            mProgressDialog.show();
            if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
                mBluetoothAdapter.enable();
            }else if (!mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.startDiscovery();
            }
        }
    //Modify for avoiding the duplicate clicking in short time by lvhongshan 20140128 end
    }
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode== KeyEvent.KEYCODE_BACK) {
//            if (mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
//
//            this.finish();
//            return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    //Add for avoiding the duplicate clicking in short time by lvhongshan 20140128 start
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    //Add for avoiding the duplicate clicking in short time by lvhongshan 20140128 end
}
