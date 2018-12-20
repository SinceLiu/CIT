package com.sim.cit.testitem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sim.cit.CommonDrive;
//import com.qualcomm.qcnvitems.QcNvItems;
import com.sim.cit.R;
import com.sim.cit.TestActivity;

import android.os.Message;
import android.os.Handler;
import java.util.Timer;
import java.util.TimerTask;
//Modify for CIT optimization by xiasiping 20140730 start
import java.util.List;
import com.sim.cit.MyXml;
import com.sim.cit.XNode;
import com.sim.cit.XProperty;
import com.sim.cit.XMethod;
import com.sim.cit.MyXmlUtils;
import java.lang.reflect.Method;
//Modify for CIT optimization by xiasiping 20140730 end
import java.io.File;
import java.io.FileWriter;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import java.io.BufferedReader;
import java.io.FileReader;

import com.sim.cit.SensorCalibration;

public class ProximitySensorCalibration extends TestActivity implements
               OnClickListener {
    private static final String TAG = "PSensorCalibration";

    private static final String LEVEL_NODE = "/sys/bus/i2c/devices/0-0039/ps_offset_level";
    private static final String CAL_NODE = "/sys/bus/i2c/devices/0-0039/ps_calibrate_prox";
    private static final String PERSIST_NODE = "/persist/pcal_offset";
    private static final int GET_PCAL_OFFSET = 523;
    private boolean result_of_cal = false;
    private String result_get = null;
    private File persist_file = null;

    private SensorManager mSensorManager;
    private Sensor mProimitySensor;
    public TextView tvMsg;
    public Button btn6;
    public Button btnCalibration;
    String strSuccess = "";
    public TextView tvResult;
    public Button btnPass;
    public boolean is2Pass=false;
    public boolean is6Pass=false;
    public boolean is2Click = false;
    public boolean is6Click = false;
    //modify for support set Psensor value by songguangyu 20131204 start
    public int value6 = 0;
    public int value2 = 0;
    //modify for support set Psensor value by songguangyu 20131204 end
    public int time6 = 5;
    public int time2 = 5;
    int type = 0;
    //modify for prevent repeat set Psensor value by songguangyu 20131211 start
//    private static QcNvItems mNv;
    private static final int SET_P_SENSOR = 122;
    private static int[] value;
    private static boolean setFinish;
    //modify for prevent repeat set Psensor value by songguangyu 20131211 end
    //Modify for reducing the time of calibration by lvhongshan 20140318 start
    private Timer timer = null;
    public int times = 0;
    public static final int TIME_MAX = 7;
    public int time_count = 0;
    public int valueDef = 0;
    public boolean isFinished = false;
    //Modify for reducing the time of calibration by lvhongshan 20140318 end
    //Modify for CIT optimization by xiasiping 20140730 start
    private static MyXml mxml;
    private static List<XProperty> xProperties;
    private static List<XMethod> xMethods;
    private static List<XNode> xNodes;
    static {
        try {
            mxml = new MyXmlUtils().getMxml();
            xProperties = mxml.getXProperties();
            xMethods = mxml.getXMethods();
            xNodes = mxml.getXNodes();
        }catch (Exception e) {
            Log.e(TAG,"xsp_May be config file has error");
        }
    }
    //Modify for CIT optimization by xiasiping 20140730 end
    private static String far = null;
    private static String near = null;

    private Button btn_infinity;
    public int value_infinity = 0;
    public boolean infinity_pass = false;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt_one_btn;
        super.onCreate(savedInstanceState);
        tvMsg = (TextView)findViewById(R.id.txt_one);
        tvMsg.setText(R.string.proximity_calibration_illustration);
        btnCalibration = (Button) findViewById(R.id.btn_one);
        btnCalibration.setText(R.string.str_calibration);
        btnCalibration.setOnClickListener(this);
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
        context = getApplicationContext();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //modify for prevent repeat set Psensor value by songguangyu 20131211 start
//        mNv = new QcNvItems(getApplicationContext());
        setFinish = false;
        //modify for prevent repeat set Psensor value by songguangyu 20131211 end

        writeNode("0", LEVEL_NODE);
        if(mSensorManager == null)
            Log.i(TAG,"mSensorManager is null");
        else{
            mProimitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
    }

    private final SensorEventListener mProximityListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public void onSensorChanged(SensorEvent event) {

        }
    };

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mProximityListener);
        SensorCalibration.finalize_native();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Modify for P-sensor change by xiasiping 20140919 start
        boolean bSucceed = mSensorManager.registerListener(mProximityListener,
                               mProimitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "bSucceed is " + bSucceed);
        //Modify for P-sensor change by xiasiping 20140808 end
        SensorCalibration.init_native();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Modify for P-sensor change by xiasiping 20140808 start
        //mSensorManager.unregisterListener(mProximityListener);
        //Modify for P-sensor change by xiasiping 20140808 end
    }

    public void onClick(View v) {
        int rv;
        int result = 0;
        Log.i("proximity", "calibration --- start");
        result = SensorCalibration.doCalibration("proximity");
        Log.i("proximity", "result ----- " + result);
        Log.d(TAG,"proximity sensor calibrattion result: " + result);

        if (result == 0) {
            strSuccess = getString(R.string.str_calibration_success);
            btnPass.setEnabled(true);
        } else {
            strSuccess = getString(R.string.str_calibration_failed);
            btnPass.setEnabled(false);
        }
        switch (result) {
        case 0:
            strSuccess = getString(R.string.str_calibration_success);
            btnPass.setEnabled(true);
            break;
        case -1:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_0);
            btnPass.setEnabled(false);
            break;
        case -2:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_1);
            btnPass.setEnabled(false);
            break;
        case -3:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_2);
            btnPass.setEnabled(false);
            break;
        case -12:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_3);
            btnPass.setEnabled(false);
            break;
        case -13:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_4);
            btnPass.setEnabled(false);
            break;
        case -14:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_5);
            btnPass.setEnabled(false);
            break;
        case -15:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_6);
            btnPass.setEnabled(false);
            break;
        case -16:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_7);
            btnPass.setEnabled(false);
            break;
        case -21:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_8);
            btnPass.setEnabled(false);
            break;
        case -22:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_9);
            btnPass.setEnabled(false);
            break;
        default:
            strSuccess = getString(R.string.str_calibration_failed)
                       +"\n"+getString(R.string.str_calibration_failed_10);
            btnPass.setEnabled(false);
            break;
        }
        Toast.makeText(context, strSuccess, 1500).show();
    }

    /*public void onClick(View v) {
        //QcNvItems mQcNvItems = new QcNvItems(this);

        switch (v.getId()) {
        case R.id.btn_three:
            is2Click = true;
            is6Click = false;
            time2 = 0;
            times = 0;
            value2 = 0;
            time_count = 0;
            isFinished = false;
            //Modify for P-sensor change by xiasiping 20140808 start
            value2 = CommonDrive.getXPsensor_new();
            tvMsg.setText(getString(R.string.proximity_calibration_illustration) + "\n2cm value: " + value2);
            if (value2 > -1) {
                is2Pass = true;
                btn6.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.GONE);
            } else {
                is2Pass = false;
                Toast.makeText(getApplicationContext(), "The device have problem!",1000).show();
            }

            /*if(timer != null){
                timer.cancel();
            }
            timer = new Timer();
            Log.i(TAG,"R.id.btn_three is clicked ");
            timer.scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    ProximitySensorCalibration.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            time_count++;
                            if(time_count==TIME_MAX){
                                if((!isFinished) && time2 < 5){
                                    isFinished = true;
                                    if (time2 == 0){
                                        value2 = valueDef;
                                        Log.i(TAG,"time2 = 0 && isFinished value2 is " + value2);
                                    }else {
                                        value2 /= time2;
                                        Log.i(TAG,"time2 != 0 && isFinished value2 is " + value2);
                                    }
                                    tvMsg.setText(getString(R.string.proximity_calibration_illustration)
                                                   + "\n2cm value: " + value2);
                                    if (value2 > -1) {
                                        is2Pass = true;
                                        btn6.setVisibility(View.VISIBLE);
                                        btn2.setVisibility(View.GONE);
                                    } else {
                                        is2Pass = false;
                                        Toast.makeText(getApplicationContext(), "The device have problem!",1000).show();
                                    }
                                }
                            }
                        }
                    });
                }
            }, 0, 500*1);
            //Modify for P-sensor change by xiasiping 20140808 end
            value0 = CommonDrive.proximityCalibration(0);
                        tvMsg.setText(getString(R.string.proximity_calibration_illustration)
                                        + "\n0mm value: " + value0);
                        if (value0 > -1) {
                                is0Pass = true;
                                btn30.setVisibility(View.VISIBLE);
                                btn0.setVisibility(View.GONE);
                        } else {
                                is0Pass = false;
                                Toast.makeText(getApplicationContext(), "The device have problem!",
                                                1000).show();
                        }
            break;
        case R.id.btn_two:
            is6Click = true;
            is2Click = false;
            time6 = 0;
            value6 = 0;
            time_count = 0;
            isFinished = false;
            //Modify for P-sensor change by xiasiping 20140808 start
            value6 = CommonDrive.getXPsensor_new();
            tvMsg.setText(getString(R.string.proximity_calibration_illustration) + "\n3.5cm value: " + value6);
            if (value6 > -1) {
                is6Pass = true;
                btn_infinity.setVisibility(View.VISIBLE);
                btn6.setVisibility(View.GONE);
            } else {
                is6Pass = false;
                btn2.setVisibility(View.VISIBLE);
                btn6.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "The device have problem!",1000).show();
            }
            if (is2Pass  && is6Pass) {
                if ((value2 - value6 < 30 || value6 > 900)) {
                    Toast.makeText(getApplicationContext(), R.string.fail,1000).show();
                    is2Pass = false;
                    is6Pass = false;
                    btn6.setVisibility(View.GONE);
                    btn_infinity.setVisibility(View.GONE);
                    btn2.setVisibility(View.VISIBLE);
                    return;
                }
            }
            break;
        case R.id.btn_infinity:
            value_infinity = 0;
            //Modify for P-sensor change by xiasiping 20140808 start
            value_infinity = CommonDrive.getXPsensor_new();
            tvMsg.setText(getString(R.string.proximity_calibration_illustration) + "\n Infinity value: " + value_infinity);
            if (value_infinity > -1) {
                infinity_pass = true;
            } else {
                infinity_pass = false;
                Toast.makeText(getApplicationContext(), "The device have problem!",1000).show();
            }
            if (is2Pass  && is6Pass && infinity_pass) {
                if ((value2 - value6 < 130) || (value6 - value_infinity < 100)) {
                    Toast.makeText(getApplicationContext(), R.string.fail,1000).show();
                    is2Pass = false;
                    is6Pass = false;
                    btn6.setVisibility(View.GONE);
                    btn_infinity.setVisibility(View.GONE);
                    btn2.setVisibility(View.VISIBLE);
                    result_of_cal = false;
                    return;
                }
                tvResult.setText(R.string.calibrating);
                tvResult.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.GONE);
                btn6.setVisibility(View.GONE);
                btn_infinity.setVisibility(View.GONE);
                //btnPass.setEnabled(true);
                result_of_cal = true;
                //write the valuo to nv
                if (!setFinish) {
                setFinish = true;
                far = Integer.toString(value6 + 1);
                near = Integer.toString(value6 + 2);
                value = new int[]{(value6 + 1),(value6 + 2)};
                Log.d("ProximityCalibration", "value2:" + value2);
                Log.d("ProximityCalibration", "value6:" + value6);
                //Add for send broadcast to display by lvhongshan 20140401 start
                Intent intent = new Intent();
                intent.setAction("com.sonim.action.PSENSOR_CALIBRATION");
                intent.putExtra("min",value6 + 1);
                intent.putExtra("max",value2 + 1);
                ProximitySensorCalibration.this.sendBroadcast(intent);
                //Add for send broadcast to display by lvhongshan 20140401 end
                mmHandler.removeMessages(SET_P_SENSOR);
                mmHandler.sendMessageDelayed(mmHandler.obtainMessage(SET_P_SENSOR), 1000);
                }
            }
            writeNode("1", CAL_NODE);
            mHandler.sendMessage(mHandler.obtainMessage(GET_PCAL_OFFSET));
            break;
        }
    }*/

            /*if(timer != null){
                timer.cancel();
            }
            timer = new Timer();
            Log.i(TAG,"R.id.btn_two is clicked ");
            timer.scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    ProximitySensorCalibration.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            time_count++;
                            if(time_count==TIME_MAX){
                                if((!isFinished) && time6 < 5){
                                    isFinished = true;
                                    if (time6 == 0){
                                        value6 = valueDef;
                                        Log.i(TAG,"time6 = 0 && isFinished value6 is " + value6);
                                    }else {
                                        value6 /= time6;
                                        Log.i(TAG,"time6 != 0 && isFinished value6 is " + value6);
                                    }
                                    tvMsg.setText(getString(R.string.proximity_calibration_illustration)
                                                   + "\n5cm value: " + value6);
                                    if (value6 > -1) {
                                        is6Pass = true;
                                    } else {
                                        is6Pass = false;
                                        Toast.makeText(getApplicationContext(), "The device have problem!",1000).show();
                                    }

                                    if (is2Pass  && is6Pass) {
                                        if ((value2 - value6 < 3)||(value2<value6)) {
                                            Toast.makeText(getApplicationContext(), R.string.fail,1000).show();
                                            is2Pass = false;
                                            is6Pass = false;
                                            btn6.setVisibility(View.GONE);
                                            btn2.setVisibility(View.VISIBLE);
                                            return;
                                        }
                                        tvResult.setText(R.string.pass);
                                        tvResult.setVisibility(View.VISIBLE);
                                        btn2.setVisibility(View.GONE);
                                        btn6.setVisibility(View.GONE);
                                        btnPass.setEnabled(true);
                                        //write the valuo to nv
                                        if (!setFinish) {
                                            setFinish = true;
                                            value = new byte[]{(byte)(value6 + 1),(byte)(value2 + 1)};
                                            Log.d("ProximityCalibration", "value2:" + (byte)value2);
                                            Log.d("ProximityCalibration", "value6:" + (byte)value6);
                                            //Add for send broadcast to display by lvhongshan 20140401 start
                                            Intent intent = new Intent();
                                            intent.setAction("com.sonim.action.PSENSOR_CALIBRATION");
                                            intent.putExtra("min",value6 + 1);
                                            intent.putExtra("max",value2 + 1);
                                            ProximitySensorCalibration.this.sendBroadcast(intent);
                                            //Add for send broadcast to display by lvhongshan 20140401 end
                                            mmHandler.removeMessages(SET_P_SENSOR);
                                            mmHandler.sendMessageDelayed(mmHandler.obtainMessage(SET_P_SENSOR), 1000);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }, 0, 500*1);*/
            //Modify for P-sensor change by xiasiping 20140808 end
                /*      value30 = CommonDrive.proximityCalibration(35);
                        tvMsg.setText(getString(R.string.proximity_calibration_illustration)
                                        + "\n0mm value: " + value0
                                        + "\n35mm value: " + value30);
                        Log.i("ProximityCalibration", "value35 = " + value30);
                        if (value30 > -1) {
                                is30Pass = true;
                        } else {
                                is30Pass = false;
                                Toast.makeText(getApplicationContext(), "The device have problem!",
                                                1000).show();
                        }*/
            //break;
        //}
                 //if (is0Pass  && is30Pass) {
                        /*try {
                                mQcNvItems.set_nv_ps_calibration_30mm((byte)value35);
                        } catch (IOException e) {
                                Log.i("ProximityCalibration", "QcNvItems error");
                                e.printStackTrace();
                        }*/
                /*      if (value30 - value0 < 5) {
                                Toast.makeText(getApplicationContext(), R.string.fail,
                                                                 1000).show();
                                is0Pass = false;
                                is30Pass = false;
                                btn30.setVisibility(View.GONE);
                                btn0.setVisibility(View.VISIBLE);
                                return;
                        }
                        tvResult.setText(R.string.pass);
                        tvResult.setVisibility(View.VISIBLE);
                        btn0.setVisibility(View.GONE);
                        btn30.setVisibility(View.GONE);
                        btnPass.setEnabled(true);*/

                      /*  try {
                        //      mQcNvItems.set_nv_ps_calibration_0mm((byte)value0);
                       //       mQcNvItems.set_nv_ps_calibration_30mm((byte)value30);
                        } catch (IOException e) {
                                Log.i("ProximityCalibration", "QcNvItems error");
                                e.printStackTrace();
                        }*/
                /*      String str0 = ""+value0;
                        String str30 = ""+value30;
                        File f0, f30;
                        File dir = new File("/persist/simcom/stk_ps");
                        FileOutputStream f0fout, f30fout;
                        try
                        {
                                if (!dir.exists() && !dir.mkdirs())
                                {
                                        Log.v("---wuzz---", "create dir fail");
                                        finish();
                                }

                                f0 = new File("/persist/simcom/stk_ps/ps_0_nv.file");
                                f30 = new File("/persist/simcom/stk_ps/ps_30_nv.file");
                                if (!f0.exists())
                                {
                                        f0.createNewFile();
                                }
                                if (!f30.exists())
                                {
                                        f30.createNewFile();
                                }
                                f0fout = new FileOutputStream(f0);
                                f30fout = new FileOutputStream(f30);
                                f0fout.write(str0.getBytes(), 0, str0.length());
                                f30fout.write(str30.getBytes(), 0, str30.length());
Log.v("---wuzz---", "in ProximitySensor value0="+value0+", value30="+value30+", str0="+str0+", str30="+str30);
                                f0fout.close();
                                f30fout.close();

                        }
                        catch(Exception e)
                        {
                                Log.e("ProximitySensor", "Exception: "+e.toString());
                        }*/
                        /*CommonDrive.proximitySetCali(0, value0);
                        CommonDrive.proximitySetCali(30, value30);
            }*/
    //Modify for P-sensor change by xiasiping 20140808 start
    /*private final SensorEventListener mProximityListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        public void onSensorChanged(SensorEvent event) {
            valueDef = (int)event.values[0];
            if ((!isFinished) && is2Click && time2 < 5) {
                time2 ++;
                value2 += event.values[0];
                Log.i(TAG,"is2Click && time2 < 5 value2 is " + value2);
                if(time2 == 5){
                    isFinished = true;
                    value2 /= 5;
                    Log.i(TAG,"time2 == 5 value2 is " + value2);
                    tvMsg.setText(getString(R.string.proximity_calibration_illustration)
                                               + "\n2cm value: " + value2);
                    if (value2 > -1) {
                        is2Pass = true;
                        btn6.setVisibility(View.VISIBLE);
                        btn2.setVisibility(View.GONE);
                    } else {
                        is2Pass = false;
                        Toast.makeText(getApplicationContext(), "The device have problem!",1000).show();
                    }
                }
            }else if ((!isFinished) && is6Click && time6 < 5) {
                time6 ++;
                value6 += event.values[0];
                Log.i(TAG,"is6Click && time6 < 5 value6 is " + value6);
                if(time6 == 5){
                    isFinished = true;
                    value6 /= 5;
                    Log.i(TAG,"time6 == 5 value6 is " + value6);
                    tvMsg.setText(getString(R.string.proximity_calibration_illustration)
                                               + "\n5cm value: " + value6);
                    if (value6 > -1) {
                        is6Pass = true;
                    } else {
                        is6Pass = false;
                        Toast.makeText(getApplicationContext(), "The device have problem!",1000).show();
                    }
                }
            }
            if (is2Pass  && is6Pass) {
                if ((value2 - value6 < 3)||(value2<value6)) {
                    Toast.makeText(getApplicationContext(), R.string.fail,1000).show();
                    is2Pass = false;
                    is6Pass = false;
                    btn6.setVisibility(View.GONE);
                    btn2.setVisibility(View.VISIBLE);
                    return;
                }
                tvResult.setText(R.string.pass);
                tvResult.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.GONE);
                btn6.setVisibility(View.GONE);
                btnPass.setEnabled(true);
                //write the valuo to nv
                //modify for prevent repeat set Psensor value by songguangyu 20131211 start
                if (!setFinish) {
                    setFinish = true;
                    value = new byte[]{(byte)(value6 + 1),(byte)(value2 + 1)};
                    Log.d("ProximityCalibration", "value2:" + (byte)value2);
                    Log.d("ProximityCalibration", "value6:" + (byte)value6);
                    //Add for send broadcast to display by lvhongshan 20140401 start
                    Intent intent = new Intent();
                    intent.setAction("com.sonim.action.PSENSOR_CALIBRATION");
                    intent.putExtra("min",value6 + 1);
                    intent.putExtra("max",value2 + 1);
                    ProximitySensorCalibration.this.sendBroadcast(intent);
                    //Add for send broadcast to display by lvhongshan 20140401 end
                    mmHandler.removeMessages(SET_P_SENSOR);
                    mmHandler.sendMessageDelayed(mmHandler.obtainMessage(SET_P_SENSOR), 1000);
                }
                //modify for prevent repeat set Psensor value by songguangyu 20131211 end
            }
        }
    };*/
    //Modify for P-sensor change by xiasiping 20140808 end
    //add to support set Psensor value by songguangyu 20131201 start

    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            String result_now = readNode(LEVEL_NODE).trim();
            /*persist_file = new File(PERSIST_NODE);
            if (persist_file.exists()) {
                Log.e("xsp_PCal", "persist_file was exists!!!!");
            } else {
                Log.e("xsp_PCal", "persist_file doesn't exists!!");
                try {
                    persist_file.createNewFile();
                } catch (IOException e) {

                }
            }*/


            if (result_get == null) {
                result_get = result_now;
            }
            if (result_get.equals(result_now)) {
                Log.e("xsp_PCal", "result doesn't changed!~ or firstresult getresult = [" + result_get + "]");
            } else {
                result_get = result_now;
                Log.e("xsp_PCal", "get new result = [" + result_get + "]");
            }
            if ("127".equals(result_get) || "255".equals(result_get)) {
                Log.e("xsp_PCal", "result_get == 255(or127)!!~~~~");
                btnPass.setEnabled(false);
                tvResult.setText(R.string.fail);
            } else {
                Log.e("xsp_PCal", "result_get != 255(or127)!!~~~~");
                writeNode(result_get, PERSIST_NODE);
                if (result_of_cal) {
                    btnPass.setEnabled(true);
                    tvResult.setText(R.string.pass);
                }
            }
        }
    };

    private String readNode(String path) {
        String procCurrentStr;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path), 256);
            try {
                procCurrentStr = reader.readLine();
            } finally {
                reader.close();
            }
            return procCurrentStr;
        } catch (IOException e) {
            Log.e("xsp_PCal", "readNode has Exception = [" + e.toString() + "], and path =[" + path +"]");
            return "unknow";
        }
    }

    private boolean writeNode(String str, String path) {
        try {
            File mFile = new File(path);
            FileWriter fileWriter = new FileWriter(mFile, false);
            fileWriter.write(str);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            Log.e("xsp_PCal", "writeNode has Exception = [" + e.toString() + "], and path =[" + path +"]");
            return false;
        }
    }

    private static final Handler mmHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
            case SET_P_SENSOR:
                //Modify for CIT optimization by xiasiping 20140916 start
                String method_set_psensor = null;
                if (xMethods != null) {
                    for (XMethod xm : xMethods) {
                        String name = xm.getName();
                        if ("qcnvitems_write_p_sensor_calibration".equals(name)) {
                            method_set_psensor = xm.getValue();
                        }
                    }
                }
                /*try{
                    if (mNv.get_p_sensor_first() == 0) {
                        mNv.set_p_sensor_first(value[1]);
                    }
                    Log.i("xsp_get_p_sensor_first", "get_p_sensor_frist() = [" + mNv.get_p_sensor_first() + "]" );
                } catch (Exception e1) {
                    Log.i("save psensor_frist", "save psensor_frist has Exception : " + e1.toString());
                }*/
                try {
                    /*if (method_set_psensor != null) {
                        Log.e(TAG,"xsp_method_set_psensor = " + method_set_psensor);
                        Class[] parameterTypes = new Class[1];
                        parameterTypes[0] = byte[].class;
                        Object[] args = new Object[1];
                        args[0] = value;
                        Method mmw = mNv.getClass().getMethod(method_set_psensor, parameterTypes);
                        mmw.invoke(mNv, args);
                        Log.e(TAG, "set psensor finish!");
                    } else {
                        Log.e(TAG,"xsp_method_set_psensor is null ");
                        mNv.set_p_sensor(value);
                    }*/
                //Modify for CIT optimization by xiasiping 20140916 end
                    //mNv.set_p_sensor(value[1]);

                    int setNear = CommonDrive.setPsensorNear(near);
                    int setFar = CommonDrive.setPsensorFar(far);
                    Log.e(TAG, "setNear is " + setNear + ", setFar is " + setFar);
                    Log.e(TAG, "near = " + near + ", far = " + far);
                //Modify for CIT optimization by xiasiping 20140730 end
                } catch (Exception e) {
                    Log.i("ProximityCalibration", "QcNvItems error");
                    //mNv.set_p_sensor(value);
                    e.printStackTrace();
                }
                break;
            }
        }
    };
    //add to support set Psensor value by songguangyu 20131201 end
}
