package com.sim.cit.testitem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.view.View;
import android.os.SystemProperties; // add for show software version by lxx 20180727
//add read electricity by songguangyu 20140124 start
import java.io.FileInputStream;
import org.apache.http.util.EncodingUtils;
//add read electricity by songguangyu 20140124 end
//Modify for CIT optimization by xiasiping 20140730 start
import java.lang.ref.WeakReference;
import java.util.List;
import com.sim.cit.MyXml;
import com.sim.cit.XNode;
import com.sim.cit.XProperty;
import com.sim.cit.XMethod;
import com.sim.cit.MyXmlUtils;
//Modify for CIT optimization by xiasiping 20140730 end

public class BatteryChargeState extends TestActivity {
    private final static String TAG = "BatteryChargeState";
    private TextView tvVoltage;
    private TextView tvTemperature;

    private CommonDrive cmd;
    private String fVoltage;
    private boolean isTest = true;
    private boolean isChargingPass = false;
    private boolean isStatePass = false;
    private TextView tvMes;
    private Button btnPass;
    private String batteryStatus;
    private String quality;
    //add read electricity by songguangyu 20140124 start
    private TextView tvEle;
    private String fElectricity;
    private FileInputStream Fis_electricity;
    //add read electricity by songguangyu 20140124 end
    //add for update temperature of battery by lvhongshan 20140305 start
    private String temperature = " ";
    //add for update temperature of battery by lvhongshan 20140305 end
    //Modify for CIT optimization by xiasiping 20140730 start
    private static MyXml mxml;
    private List<XProperty> xProperties;
    private List<XMethod> xMethods;
    private List<XNode> xNodes;
    private String xVoltage = null;
    private String volpermission = null;
    private String xElectricity = null;
    private String elepermission = null;
    private String xTemperature = null;
    private String tempermission = null;
    //Modify for CIT optimization by xiasiping 20140730 end
    private MyHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Modify for CIT optimization by xiasiping 20140730 start
        try {
            mxml = new MyXmlUtils().getMxml();
            xProperties = mxml.getXProperties();
            xMethods = mxml.getXMethods();
            xNodes = mxml.getXNodes();
        }catch (Exception e) {
            Log.e(TAG,"xsp_May be config file has error");
        }
        if (xNodes != null) {
            for (XNode xn : xNodes) {
                String name = xn.getName();
                if ("voltage".equals(name)) {
                    xVoltage = xn.getValue();
                    volpermission = xn.getPermission();
                }
                if ("electricity".equals(name)) {
                    xElectricity = xn.getValue();
                    elepermission = xn.getPermission();
                }
                if ("temperature".equals(name)) {
                    xTemperature = xn.getValue();
                    tempermission = xn.getPermission();
                }
            }
        }
        //Modify for CIT optimization by xiasiping 20140730 end

        //isAutoPassOrFail = false;   //20121208 modify by lvhongshan
        layoutId = R.layout.battery_charge;
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler(this);
        cmd = new CommonDrive();
        tvVoltage = (TextView) findViewById(R.id.tv_voltage);
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);
        //add read electricity by songguangyu 20140124 start
        tvEle = (TextView) findViewById(R.id.tv_electricity);
        tvEle.setVisibility(View.INVISIBLE);
        //add read electricity by songguangyu 20140124 end

//        cmd = new CommonDrive();
        tvMes = (TextView)findViewById(R.id.tv_mes);
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
        registerReceiver(mChargeInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        isTest = true;
        new MyThread(this).start();
        /*new Thread(){
            @Override
            public void run() {
                super.run();
                while(isTest){
                    //Modify for CIT optimization by xiasiping 20140730 start
                    if (xVoltage != null && "O_RDONLY".equals(volpermission)) {
                        Log.e(TAG,"xsp_xVoltage = " + xVoltage);
                        fVoltage = getString(R.string.voltage)+cmd.getVoltage_d(xVoltage)+getString(R.string.test_volage_volt);
                    } else {
                        Log.e(TAG,"xsp_xVoltage is null or permission is not O_RDONLY ");
                        fVoltage = getString(R.string.voltage) + cmd.getVoltage() + getString(R.string.test_volage_volt);
                    }
                    //fVoltage = getString(R.string.voltage) + cmd.getVoltage() + getString(R.string.test_volage_volt);
                    //Modify for CIT optimization by xiasiping 20140730 end
                    //add read electricity by songguangyu 20140124 start
                    fElectricity = getElectricity();
                    //add read electricity by songguangyu 20140124 end
                    //add for update temperature of battery by lvhongshan 20140305 start
                    temperature = getTemperature();
                    //add for update temperature of battery by lvhongshan 20140305
                    //modify for cit crash when charge test bug23462 by songguangyu 20140505 start
                    //mHandler.sendMessage(new Message());
                    mHandler.sendEmptyMessage(1);
                    //modify for cit crash when charge test bug23462 by songguangyu 20140505 end
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }.start();*/
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                while (isTest) {
//                    quality = " (" + cmd.getCapacity() + "%)";
//                    mHandler.sendMessage(new Message());
//                    try {
//                        sleep(500);
//                    } catch (InterruptedException e) {
//                    }
//                }
//            }
//        }.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    public static class MyThread extends Thread{
        WeakReference<BatteryChargeState> reference;
        public MyThread(BatteryChargeState activity){
            reference = new WeakReference<BatteryChargeState>(activity);
        }
        @Override
        public void run() {
            super.run();
            BatteryChargeState batteryChargeState = reference.get();
            while(batteryChargeState.isTest){
                //Modify for CIT optimization by xiasiping 20140730 start
                if (batteryChargeState.xVoltage != null && "O_RDONLY".equals(batteryChargeState.volpermission)) {
                    Log.e(TAG,"xsp_xVoltage = " + batteryChargeState.xVoltage);
                    batteryChargeState.fVoltage = batteryChargeState.getString(R.string.voltage)+batteryChargeState.cmd.getVoltage_d(batteryChargeState.xVoltage)+batteryChargeState.getString(R.string.test_volage_volt);
                } else {
                    Log.e(TAG,"xsp_xVoltage is null or permission is not O_RDONLY ");
                    batteryChargeState.fVoltage = batteryChargeState.getString(R.string.voltage) + batteryChargeState.cmd.getVoltage() + batteryChargeState.getString(R.string.test_volage_volt);
                }
                //fVoltage = getString(R.string.voltage) + cmd.getVoltage() + getString(R.string.test_volage_volt);
                //Modify for CIT optimization by xiasiping 20140730 end
                //add read electricity by songguangyu 20140124 start
                batteryChargeState.fElectricity = batteryChargeState.getElectricity();
                //add read electricity by songguangyu 20140124 end
                //add for update temperature of battery by lvhongshan 20140305 start
                batteryChargeState.temperature = batteryChargeState.getTemperature();
                //add for update temperature of battery by lvhongshan 20140305
                //modify for cit crash when charge test bug23462 by songguangyu 20140505 start
                //mHandler.sendMessage(new Message());
                batteryChargeState.mHandler.sendEmptyMessage(1);
                //modify for cit crash when charge test bug23462 by songguangyu 20140505 end
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            isTest = false;
        }
//        unregisterReceiver(mBatteryInfoReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isTest = false;
        unregisterReceiver(mChargeInfoReceiver);
    }
    //Modify for update temperature of battery by lvhongshan 20140305 start
    /*private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String nTemperature = "";
            String action = intent.getAction();

            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                nTemperature = getString(R.string.temperature) + (float)intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10 + getString(R.string.test_temperature_degree);
                isStatePass = true;
            }
            if((int)intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10<=-30){
                Toast.makeText(BatteryChargeState.this, getString(R.string.battery_not_linked), 2000).show();
                isStatePass = false;
            }else if((int)intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10 > 70){
                nTemperature = getString(R.string.temperature) + (float)intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10 + getString(R.string.test_temperature_degree) + " " + getString(R.string.battery_tooLarge);
                isStatePass = false;
            }else {
                nTemperature = getString(R.string.temperature) + (float)intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10 + getString(R.string.test_temperature_degree) + " " + getString(R.string.battery_normal);
                isStatePass = true;
            }
            tvTemperature.setText(String.valueOf(nTemperature));
            if (isChargingPass && isStatePass){
                btnPass.setEnabled(true);
            } else {
                btnPass.setEnabled(false);
            }
        }
    };*/

    private String getTemperature() {
        float temp = 0f;
        //Modify for CIT optimization by xiasiping 20140730 start
        if (xTemperature != null && "O_RDONLY".equals(tempermission)) {
            Log.e(TAG,"xsp_xTemperature = " + xTemperature);
            temp = (float)cmd.getTemp_d(xTemperature) / 10;
        } else {
            Log.e(TAG,"xsp_xTemperature is null or permission is not O_RDONLY");
            temp = (float)cmd.getTemp() / 10;
        }

        //temp = (float)cmd.getTemp() / 10;
        //Modify for CIT optimization by xiasiping 20140730 end
        String nTemperature = "";
        if(temp <= (-30)){
            nTemperature = getString(R.string.battery_not_linked);
            isStatePass = false;
        }else if(temp > 70){
            nTemperature = getString(R.string.temperature) + temp + getString(R.string.test_temperature_degree) + " " + getString(R.string.battery_tooLarge);
            isStatePass = false;
        }else {
            nTemperature = getString(R.string.temperature) + temp + getString(R.string.test_temperature_degree) + " " + getString(R.string.battery_normal);
            isStatePass = true;
        }
        //modify for cit crash when charge test bug23462 by songguangyu 20140505 start
        /*if (isChargingPass && isStatePass){
            btnPass.setEnabled(true);
        } else {
            btnPass.setEnabled(false);
        }*/
        mHandler.sendEmptyMessage(2);
        //modify for cit crash when charge test bug23462 by songguangyu 20140505 end
        return nTemperature;
    }
    //Modify for update temperature of battery by lvhongshan 20140305 end
    private BroadcastReceiver mChargeInfoReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int plugType = intent.getIntExtra("plugged", 0);
                int status = intent.getIntExtra("status",BatteryManager.BATTERY_STATUS_UNKNOWN);
                Log.i("BatteryChargeState", "status="+status);
                int mLevel = intent.getIntExtra("level", 0);
                quality = " (" + mLevel + "%)";
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    /*if (plugType > 0) {
                        batteryStatus = getString(R.string.charging);
                        isChargingPass = true;
                    }*/
                    batteryStatus = getString(R.string.charging);
                    isChargingPass = true;
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    batteryStatus = getString(R.string.please_input_charger);
                    isChargingPass = false;
                } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                    batteryStatus = getString(R.string.please_input_charger);
                    isChargingPass = false;
                } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                    batteryStatus = getString(R.string.battery_info_status_full);
                    isChargingPass = true;
                } else {
                    batteryStatus = "unknown state";
                    isChargingPass = false;
                }

                tvMes.setText(batteryStatus+quality);
                if(mLevel<=10){
                    tvMes.setTextColor(getResources().getColor(R.color.red));
                }else {
                    tvMes.setTextColor(getResources().getColor(R.color.black));
                }
                //modify for cit crash when charge test bug23462 by songguangyu 20140505 start
                /*if (isChargingPass && isStatePass){
                    btnPass.setEnabled(true);
                } else {
                    btnPass.setEnabled(false);
                }*/
                mHandler.sendEmptyMessage(2);
                //modify for cit crash when charge test bug23462 by songguangyu 20140505 end
            }
        }
    };
    //add read electricity by songguangyu 20140124 start
    public String getElectricity() {
        String ele ="";
        try {
            //Modify for CIT optimization by xiasiping 20140730 start
            if (xElectricity != null && "O_RDONLY".equals(elepermission)) {
                Log.e(TAG,"xsp_xElectricity = " + xElectricity);
                Fis_electricity = new FileInputStream(xElectricity);
            } else {
                Log.e(TAG,"xsp_xElectricity is null or permission is not O_RDONLY");
                Fis_electricity = new FileInputStream("/sys/class/power_supply/battery/current_now");
            }
            //Fis_electricity = new FileInputStream("/sys/class/power_supply/battery/current_now");
            //Modify for CIT optimization by xiasiping 20140730 end
            byte [] buf = new byte[20];
            for(int i=0;i<20;i++){
                buf[i]=' ';
            }
            Fis_electricity.read(buf);
            ele = EncodingUtils.getString(buf, "UTF-8");
            ele = ele.replaceAll(" ","");
            boolean charge = '-'==ele.charAt(0);
            if(charge) {
                ele = "charge electricity:"+ele.substring(1,ele.length()-4)+"mA";
            }else {
                ele = "discharge electricity:"+ele.substring(0,ele.length()-4)+"mA";
            }
            Log.i("electricity", "Electricity():"+ ele);
        } catch (Exception e) {
            Log.i("electricity", "getElectricity():"+ e);
        }
        return ele;
    }
    //add read electricity by songguangyu 20140124 end
    private static class MyHandler extends Handler {
        private WeakReference<BatteryChargeState> reference;

        public MyHandler(BatteryChargeState activity) {
            reference = new WeakReference<BatteryChargeState>(activity);//这里传入activity的上下文
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BatteryChargeState batteryChargeState = reference.get();
            //modify for cit crash when charge test bug23462 by songguangyu 20140505 start
            switch(msg.what){
                case 1:
                    batteryChargeState.tvVoltage.setText(batteryChargeState.fVoltage);
                    //add read electricity by songguangyu 20140124 start
                    //tvEle.setText(fElectricity);
                    //add read electricity by songguangyu 20140124 end
                    //add for update temperature of battery by lvhongshan 20140305 start
                    batteryChargeState.tvTemperature.setText(String.valueOf(batteryChargeState.temperature));
                    //add for update temperature of battery by lvhongshan 20140305 end
                    break;
                case 2:
                    if (batteryChargeState.isChargingPass && batteryChargeState.isStatePass){
                        batteryChargeState.btnPass.setEnabled(true);
                    } else {
                        batteryChargeState.btnPass.setEnabled(false);
                    }
                    break;
            }
            //modify for cit crash when charge test bug23462 by songguangyu 20140505 end
        }
    }
}
