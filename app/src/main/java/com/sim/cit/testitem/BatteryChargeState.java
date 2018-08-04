package com.sim.cit.testitem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.view.View;
import android.os.SystemProperties; // add for show software version by lxx 20180727
//add read electricity by songguangyu 20140124 start
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import org.apache.http.util.EncodingUtils;
//add read electricity by songguangyu 20140124 end
//Modify for CIT optimization by xiasiping 20140730 start
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import com.sim.cit.MyXml;
import com.sim.cit.XNode;
import com.sim.cit.XProperty;
import com.sim.cit.XMethod;
import com.sim.cit.MyXmlUtils;
import android.widget.Toast;
//Modify for CIT optimization by xiasiping 20140730 end

public class BatteryChargeState extends TestActivity {
    private final String TAG = "BatteryChargeState";
    private TextView tvSoftwareVersion;
    private TextView tvVoltage;
    private TextView tvTemperature;
    private TextView TPManufacturerInfo;
    private TextView LCDManufacturerInfo;
    private TextView FrontCameraManufacturerInfo;
    private TextView BackCameraManufacturerInfo;
    private TextView ROMManufacturerInfo;
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
        cmd = new CommonDrive();
        tvSoftwareVersion = (TextView) findViewById(R.id.tv_software_version);
        tvVoltage = (TextView) findViewById(R.id.tv_voltage);
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);
        //add read electricity by songguangyu 20140124 start
        tvEle = (TextView) findViewById(R.id.tv_electricity);
        tvEle.setVisibility(View.INVISIBLE);
        //add read electricity by songguangyu 20140124 end
        TPManufacturerInfo = (TextView) findViewById(R.id.tp_manufacturer_info);
        LCDManufacturerInfo = (TextView) findViewById(R.id.lcd_manufacturer_info);
        FrontCameraManufacturerInfo = (TextView) findViewById(R.id.front_camera_manufacturer_info);
        BackCameraManufacturerInfo = (TextView) findViewById(R.id.back_camera_manufacturer_info);
        ROMManufacturerInfo = (TextView) findViewById(R.id.rom_manufacturer_info);

//        cmd = new CommonDrive();
        tvMes = (TextView)findViewById(R.id.tv_mes);
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
        registerReceiver(mChargeInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
        //add for show software version by lxx 20180727
        String innerVersion = getInternal_version() + getSECVersion();
        if (TextUtils.isEmpty(innerVersion) || innerVersion.contains("unknown"))
            innerVersion = getSoftwareVersion2();
        tvSoftwareVersion.setText(innerVersion);
        TPManufacturerInfo.setText(getString(R.string.tp_type) + ":" + getTPManufacturer());
        LCDManufacturerInfo.setText(getString(R.string.lcd_type) + ":" + getLCDManufacturer());
        FrontCameraManufacturerInfo.setText(getString(R.string.front_camera_type) + ":" + getFrontCameraManufacturer());
        BackCameraManufacturerInfo.setText(getString(R.string.camera_type) + ":" + getCameraManufacturer());
        ROMManufacturerInfo.setText(getString(R.string.rom_type) + ":" + getROMSupplier() + "_" + getROMSize());

        //registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //Modify for fix the problem of battery infos displaying by xiasiping 20140819 start
        isTest = true;
        new Thread(){
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
        }.start();
        //Modify for fix the problem of battery infos displaying by xiasiping 20140819 end
    }
    @Override
    protected void onPause() {
        super.onPause();
        isTest = false;
        //unregisterReceiver(mBatteryInfoReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        isTest = false;
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
                Log.i("lvhongshan_BatteryChargeState", "status="+status);
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
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //modify for cit crash when charge test bug23462 by songguangyu 20140505 start
            switch(msg.what){
            case 1:
                tvVoltage.setText(fVoltage);
                //add read electricity by songguangyu 20140124 start
                //tvEle.setText(fElectricity);
                //add read electricity by songguangyu 20140124 end
                //add for update temperature of battery by lvhongshan 20140305 start
                tvTemperature.setText(String.valueOf(temperature));
                //add for update temperature of battery by lvhongshan 20140305 end
                break;
            case 2:
                if (isChargingPass && isStatePass){
                    btnPass.setEnabled(true);
                } else {
                    btnPass.setEnabled(false);
                }
                break;
            }
            //modify for cit crash when charge test bug23462 by songguangyu 20140505 end
        }
    };
//  private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            tvMes.setText(batteryStatus+quality);
//        }
//    };

    //add for show software version by lxx 20180727 start
    private String getInternal_version() {
        // Modify for CIT optimization by xiasiping 20140730 start
        String internal_version = null;
        if (xProperties != null) {
            for (XProperty xpp : xProperties) {
                String name = xpp.getName();
                if ("internaledition".equals(name)) {
                    internal_version = xpp.getValue();
                }
            }
        }
        String ivStr = null;
        if (internal_version != null) {
            Log.e(TAG, "xsp_internal_version = " + internal_version);
            ivStr = getSystemproString(internal_version);
        } else {
            ivStr = getSystemproString("ro.product.internaledition");
            Log.e(TAG, "xsp_internal_version is null ");
        }
        // String ivStr = getSystemproString("ro.product.internaledition");
        // Modify for CIT optimization by xiasiping 20140730 end
        return ivStr;
    }

    private String getSECVersion() {
        String procCurrentStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sys/devices/soc0/secure_boot_version"), 256);
            try {
                procCurrentStr = reader.readLine();
            } finally {
                reader.close();
            }
            Log.e("Version_getSECVersion", "getSECVersion()  = [" + procCurrentStr + "]");

            int isSec = Integer.parseInt(procCurrentStr);

            if (isSec == 1) {
                return "-sec";
            } else if (isSec == 0) {
                return "";
            }
            return "";
        } catch (Exception e) {
            Log.e("Version_getSECVersion", "getSECVersion() has Exception   = [" + e.toString() + "]");
            return "";
        }
    }

    private static String getSystemproString(String property) {
        return SystemProperties.get(property, "unknown");
    }
    private String getSoftwareVersion2() {
        return android.os.Build.DISPLAY;
    }

    private String getTPManufacturer() {
        String procCurrentStr;

        try {
            Log.i(TAG, "----------------getTPManufacturer1");
            // BufferedReader reader = new BufferedReader(new
            // FileReader("/sys/class/i2c-dev/i2c-5/device/5-0038/tp_vendor"),
            // 256);
            // BufferedReader reader = new BufferedReader(new
            // FileReader("/sys/class/i2c-dev/i2c-3/device/3-005d/tp_vendor"),
            // 256);
            // BufferedReader reader = new BufferedReader(new
            // FileReader("/sys/devices/soc/78b7000.i2c/i2c-3/i2c-dev/i2c-3"),
            // 256);
            BufferedReader reader = new BufferedReader(new FileReader("/proc/tp_vendor"), 256);
            Log.i(TAG, "----------------getTPManufacturer2");
            try {
                procCurrentStr = reader.readLine();
                Log.i(TAG, "----------------getTPManufacturer procCurrentStr = " + procCurrentStr);
            } finally {
                reader.close();
            }
            return procCurrentStr;
        } catch (IOException e) {
            e.printStackTrace();
            return "unknown";
        }
    }

    private String getLCDManufacturer() {
        String procCurrentStr;

        try {
            Log.i(TAG, "----------------getLCDManufacturer1");
            BufferedReader reader = new BufferedReader(
                    new FileReader("/sys/devices/soc.0/1a98000.qcom,mdss_dsi/lcd_vendor"), 256);
            Log.i(TAG, "----------------getLCDManufacturer");
            try {
                procCurrentStr = reader.readLine();
                Log.i(TAG, "----------------getLCDManufacturer procCurrentStr = " + procCurrentStr);
            } finally {
                reader.close();
            }
            return procCurrentStr;
        } catch (IOException e) {
            e.printStackTrace();
            return "unknown";
        }
    }

    private String getCameraManufacturer() {
        String procCurrentStr;

        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader("/sys/devices/soc.0/1b0c000.qcom,cci/3.qcom,eeprom/camera_name"), 256);
            try {
                procCurrentStr = reader.readLine();
            } finally {
                reader.close();
            }
            return procCurrentStr;
        } catch (IOException e) {
            e.printStackTrace();
            return "unknown";
        }
    }

    // Modify for add front camera information by lizhaobo 20151120 start
    private String getFrontCameraManufacturer() {
        String procCurrentStr;

        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader("/sys/devices/soc.0/1b0c000.qcom,cci/1.qcom,eeprom/sub_camera_name"), 256);
            try {
                procCurrentStr = reader.readLine();
            } finally {
                reader.close();
            }
            return procCurrentStr;
        } catch (IOException e) {
            e.printStackTrace();
            return "unknown";
        }
    }
    // Modify for add front camera information by lizhaobo 20151120 end

    private String getROMSize() {
        String procCurrentStr;

        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader("/sys/class/mmc_host/mmc0/mmc0:0001/block/mmcblk0/size"), 256);
            try {
                procCurrentStr = reader.readLine();
            } finally {
                reader.close();
            }
            Log.e("Version_getROMSize", "getROMSize()  = [" + procCurrentStr + "]");

            File file = Environment.getExternalStorageDirectory();
            Log.e("Version_getROMSize", "getROMSize()  = [" + file.getTotalSpace() + "]");
            long size = file.getTotalSpace();
            float romSize = size / 1024 / 1024 / 1024;
            if(romSize > 64)
            {
                return "128G";
            }
            else if(romSize > 32)
            {
                return "64G";
            }
            else if(romSize > 16) {
                return "32G";
            }
            else if(romSize > 8) {
                return "16G";
            }
            else if(romSize > 4){
                return "8G";
            }

//			int size_ROM = Integer.parseInt(procCurrentStr);
//
//			if (size_ROM >= 60000000) {
//				return "32G";
//			} else if (size_ROM <= 31000000) {
//				return "16G";
//			}

            return "unknown";
        } catch (Exception e) {
            Log.e("Version_getROMSize", "getROMSize() has Exception   = [" + e.toString() + "]");
            return "unknown";
        }
    }
    private String getROMSupplier() {
        String procCurrentStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sys/class/mmc_host/mmc0/mmc0:0001/manfid"),
                    256);
            try {
                procCurrentStr = reader.readLine();
            } finally {
                reader.close();
            }
            Log.e("Version_getROMSupplier", "getROMSupplier()  = [" + procCurrentStr + "]");
            if ("0x000015".equals(procCurrentStr)) {
                return "Samsung";
            } else if ("0x000090".equals(procCurrentStr)) {
                return "Hynix";
            }
            return "unknown";
        } catch (IOException e) {
            Log.e("Version_getROMSupplier", "getROMSupplier() has Exception   = [" + e.toString() + "]");
            return "unknown";
        }
    }
    //add for show software version by lxx 20180727 end


}
