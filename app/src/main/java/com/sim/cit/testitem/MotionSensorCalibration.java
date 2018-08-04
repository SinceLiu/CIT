package com.sim.cit.testitem;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
// Add for fix bug 1532 when click calibration pop up "device busy" by lvhongshan 20140115 start
import android.provider.Settings;
// Add for fix bug 1532 when click calibration pop up "device busy" by lvhongshan 20140115 end
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import com.sim.cit.SensorCalibration;

// add for gsensor test by liuzhihao 20131129 start
import android.hardware.Sensor;
import android.hardware.SensorManager;
import com.qualcomm.sensors.sensortest.SensorID;
import com.qualcomm.sensors.sensortest.SensorID.SensorType;
import com.qualcomm.sensors.sensortest.SensorTest;
import com.qualcomm.sensors.sensortest.SensorTest.DataType;
// add for gsensor test by liuzhihao 20131129 end

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.text.TextUtils;
//import com.qualcomm.qcnvitems.QcNvItems;
import android.os.Handler;
import android.os.Message;

import java.io.File;

public class MotionSensorCalibration extends TestActivity {
    private TextView txtMsg;
    private Button btnCalibration;
    private Button btnPass;
    private Context context;
    // add for gsensor test by liuzhihao 20131129 start
    private static final String TAG = "MotionSensorCalibration";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    // add for gsensor test by liuzhihao 20131129 end
    private static final int SET_G_SENSOR = 211;
    public int rotationValue = 0;
    String strSuccess = "";
    private int val_x = 0;
    private int val_y = 0;
    private int val_z = 0;
//    private static QcNvItems mNv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt_one_btn;
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        // Add for fix bug 1532 when click calibration pop up "device busy" by lvhongshan 20140115 start
        rotationValue = Settings.System.getInt(context.getContentResolver(),
                   Settings.System.ACCELEROMETER_ROTATION, 0);
        Log.i(TAG,"rotationValue is " + rotationValue);
        Settings.System.putInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION, 0);
        // Add for fix bug 1532 when click calibration pop up "device busy" by lvhongshan 20140115 end
        // add for gsensor test by liuzhihao 20131129 start
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mSensor == null) {
            Log.e("loda", "mSensor is null!");
        }
        // add for gsensor test by liuzhihao 20131129 end

        txtMsg = (TextView)findViewById(R.id.txt_one);
        txtMsg.setText(R.string.motion_sensor_calibration_illustration);
        btnCalibration = (Button) findViewById(R.id.btn_one);
        btnCalibration.setText(R.string.str_calibration);
        btnCalibration.setOnClickListener(mCalibration);
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
//        mNv = new QcNvItems(getApplicationContext());
    }

    private OnClickListener mCalibration = new OnClickListener() {
        public void onClick(View arg0) {
            //int returnValueArray[] = new int[5];
            //returnValueArray = CommonDrive.excemotcalH();

            // add for gsensor test by liuzhihao 20131129 start
            int rv;
            /*SensorType type = SensorType.getSensorType(mSensor);

            rv = SensorTest.runSensorTest(new SensorID(type, 0),
                   getDataType(mSensor), SensorTest.TestType.values()[Integer.parseInt("5")],
                   true, true);*/
            int result = 0;
            result = SensorCalibration.doCalibration("accl");
            Log.d("G-sensorCalibration","Accel sensor calibrattion result: " + result);
            /*String vals = "pass";
            //CommonDrive.GsensorCalibration();
            Log.e("G-sensorCalibration", "qqqqqqqqqqqqqqqqq   vals = " + vals);
            if ("fail".equals(vals)) {
                strSuccess = getString(R.string.str_calibration_failed)
                           +"\n"+getString(R.string.str_calibration_failed_0);
                btnPass.setEnabled(false);
            } else if (!TextUtils.isEmpty(vals)) {
                String[] strs = vals.split(",");
                if (!",,".equals(vals)) {
                    val_x = Integer.parseInt(strs[0].trim());
                    val_y = Integer.parseInt(strs[1].trim());
                    val_z = Integer.parseInt(strs[2].trim());
                    Log.e("G-sensorCalibration", "qqqqqqqqqqqqqqqqq   val_x = " + val_x);
                    Log.e("G-sensorCalibration", "qqqqqqqqqqqqqqqqq   val_y = " + val_y);
                    Log.e("G-sensorCalibration", "qqqqqqqqqqqqqqqqq   val_z = " + val_z);
                    mmHandler.removeMessages(SET_G_SENSOR);
                    mmHandler.sendMessageDelayed(mmHandler.obtainMessage(SET_G_SENSOR), 1000);
                    // add a judggment of GsensorCalibration value by xsp 20150827 start
                    /*String mpu_node = "/sys/class/sensors/MPU6050-accel/self_test";
                    String bmi_node = "/sys/class/sensors/bmi160-accel/self_test";
                    File mpu_file = new File(mpu_node);
                    File bmi_file = new File(bmi_node);

                    if (mpu_file.exists()) {
                        if (Math.abs(val_x) > 334 || Math.abs(val_y) > 334) {
                            strSuccess = getString(R.string.str_calibration_failed)
                                   +"\n"+getString(R.string.str_calibration_failed_0);
                            btnPass.setEnabled(false);
                            Toast.makeText(context, strSuccess, 1500).show();
                        } else {
                            mmHandler.removeMessages(SET_G_SENSOR);
                            mmHandler.sendMessageDelayed(mmHandler.obtainMessage(SET_G_SENSOR), 1000);
                        }
                    }
                    if (bmi_file.exists()) {
                        int value_x = 0;
                        int value_y = 0;
                        if (val_x <= 128) {
                            value_x = val_x;
                        } else {
                            value_x = -(255 - val_x);
                        }
                        if (val_y <= 128) {
                            value_y = val_y;
                        } else {
                            value_y = -(255 - val_y);
                        }
                        if (Math.abs(value_x) < 26 && Math.abs(value_y) < 26) {
                            mmHandler.removeMessages(SET_G_SENSOR);
                            mmHandler.sendMessageDelayed(mmHandler.obtainMessage(SET_G_SENSOR), 1000);
                        } else {
                            strSuccess = getString(R.string.str_calibration_failed)
                                       +"\n"+getString(R.string.str_calibration_failed_0);
                            btnPass.setEnabled(false);
                            Toast.makeText(context, strSuccess, 1500).show();
                        }
                    }*/
              /*  }


            } else {
                strSuccess = getString(R.string.str_calibration_failed)
                           +"\n"+getString(R.string.str_calibration_failed_0);
                btnPass.setEnabled(false);
                Toast.makeText(context, strSuccess, 1500).show();
            }  */
                    // add a judggment of GsensorCalibration value by xsp 20150827 end
            
            /*Log.d("MotionCalibration", "The nVlaue is nVlaue[0]"
                + returnValueArray[0] + returnValueArray[1]
                + returnValueArray[2] + returnValueArray[3]
                + returnValueArray[4]);*/

            //if (returnValueArray[0] == 0) {
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
            // add for gsensor test by liuzhihao 20131129 end
            Toast.makeText(context, strSuccess, 1500).show();
        }

    };
    // Add for fix bug 1532 when click calibration pop up "device busy" by lvhongshan 20140115 start
    @Override
    public void finish() {
        if (rotationValue != 0){
            Settings.System.putInt(context.getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION, rotationValue);
        }
        super.finish();
    }
    // Add for fix bug 1532 when click calibration pop up "device busy" by lvhongshan 20140115 end
   // add for gsensor test by liuzhihao 20131129 start
   /**
    * Most sensor types simply use the PRIMARY data-type.  But for those that don't,
    * we must perform the translation here.
    *
    * @param sensor The sensor we wish to translate
    * @return The data-type to request
    */
   public static SensorTest.DataType getDataType(Sensor sensor){
       switch(sensor.getType()){
           case Sensor.TYPE_PROXIMITY: return SensorTest.DataType.PRIMARY;
           case Sensor.TYPE_LIGHT: return SensorTest.DataType.SECONDARY;
           default: return SensorTest.DataType.PRIMARY;
       }
   }
   // add for gsensor test by liuzhihao 20131129 end

    protected void onResume() {
        super.onResume();
        if(mSensor == null) {
            Toast.makeText(this, "MotionSensor is null: ", 2000).show();
        }
        boolean bSucceed = mSensorManager.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if(!bSucceed) {
            Toast.makeText(this, "registerListener is faild: ", 2000).show();
        }
        SensorCalibration.init_native();
    }
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(myListener);
        super.onPause();
        SensorCalibration.finalize_native();
    }

    private SensorEventListener myListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent event) {

        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private final Handler mmHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
            case SET_G_SENSOR:
                try {
                 //   mNv.set_g_sensor(val_x, val_y, val_z);
                    strSuccess = getString(R.string.str_calibration_success);
                    btnPass.setEnabled(true);
                    Toast.makeText(context, strSuccess, 1500).show();
                } catch (Exception e) {
                    Log.i("GsensorCalibration", "QcNvItems error");
                    Toast.makeText(context, "Failed", 1500).show();
                    e.printStackTrace();
                }
                break;
            }
        }
    };

}

