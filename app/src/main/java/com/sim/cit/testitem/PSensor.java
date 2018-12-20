package com.sim.cit.testitem;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
//Modify for P-sensor change by xiasiping 20140808 start
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
//Modify for P-sensor change by xiasiping 20140808 end
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PSensor extends TestActivity {

        private static final String TAG = "PSensor";
	private SensorManager mSensorManager;
	TextView mtv_pro_info;
	Sensor mProimitySensor;
	String strPromityinfo;

	ImageView imageView[] = new ImageView[2];
	private float oldValue;  
	private float newValue;
	private boolean isNear;

	private int egt = 0;
	private int fve = 0;

    //Add SensorCheck for ProximitySensor by xiasiping 20140626 start
    private long SensorchangeTimes = 0L;
    private float mValue;
    //Add SensorCheck for ProximitySensor by xiasiping 20140626 end
    //Modify for P-sensor change by xiasiping 20140808 start
    private int mValue_show;
    private Timer mTimerReadPsensor = new Timer();
    private static final int MSG_SHOW_PSENSOR = 0;
    private float mValue_show_f = 0.0f;
    //Modify for P-sensor change by xiasiping 20140808 end
    private float mValue_old = 0.0f;
    private String PS_DATA = "/sys/devices/soc.0/78b6000.i2c/i2c-0/0-0053/ps_data";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//isAutoPassOrFail = false;
		layoutId = R.layout.test_proximitysenor;
		super.onCreate(savedInstanceState);
                //Add SensorCheck for ProximitySensor by xiasiping 201405626 start
                btnPass.setEnabled(false);
                //Add SensorCheck for ProximitySensor by xiasiping 20140626 end
		initAllControl();
/*  20121210 modified for M1000 test proximitySensor by lvhongshan start    */
//		String eight = CommonDrive.getProximityPSwitch(30).trim();
//		String five = CommonDrive.getProximityPSwitch(20).trim();
//		if (!"".equals(eight) && eight != null) {
//			egt = Integer.parseInt(eight);
//		}
//		if (!"".equals(five) && five != null) {
//			fve = Integer.parseInt(five);
//		}
//		egt=30;
//		fve=20;
                egt=60;
		fve=45;

		mtv_pro_info.setText(strPromityinfo);
		mtv_pro_info.setTextColor(0xffff0000);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                if(mSensorManager == null)
                    Log.i(TAG,"mSensorManager is null");
		mProimitySensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	}

	private void initAllControl() {
		imageView[0] = (ImageView) findViewById(R.id.test_proximitysensor_gray);
		imageView[1] = (ImageView) findViewById(R.id.test_proximitysensor_green);

		strPromityinfo = getString(R.string.proximitysenor_info);
		mtv_pro_info = (TextView) findViewById(R.id.tv_proximitysenor_info);

		imageView[0].setVisibility(View.VISIBLE);
		mtv_pro_info.setText(strPromityinfo + "\n");
	}
    //Modify for P-sensor change by xiasiping 20140808 start
    class TimerReadPsensor extends TimerTask {
        public void run() {
            int ps_data = -1;
            if (!"unknown".equals(readNode(PS_DATA))) {
                ps_data = Integer.parseInt(readNode(PS_DATA).trim());
            }
            mValue_show = ps_data;
            mValue_show_f = (float)mValue_show;
            mHandler.sendEmptyMessage(MSG_SHOW_PSENSOR);
        }
    }
    private String readNode(String path){
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
            e.printStackTrace();
            return "unknown";
        }
    }

    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mtv_pro_info.setText(strPromityinfo + "\n" + mValue_show_f);
            if (mValue_old == 0.0f && mValue_show_f != 0.0f) {
                mValue_old = mValue_show_f;
            } else if (mValue_old != 0.0f && mValue_show_f != 0.0f) {
                if (mValue_old != mValue_show_f) {
                    //btnPass.setEnabled(true);
                }
            }
        }
    };
    //Modify for P-sensor change by xiasiping 20140808 end

    @Override
    protected void onResume() {
        super.onResume();
        if(mProimitySensor == null) {
            Toast.makeText(this, "ProimitySensor is null: ", 2000).show();
        }
        boolean bSucceed = mSensorManager.registerListener(mProximityListener,
			mProimitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "bSucceed is " + bSucceed);

        if(!bSucceed) {
            Toast.makeText(this, "registerListener is faild: ", 2000).show();
        }
        //Modify for P-sensor change by xiasiping 20140808 start
//        mTimerReadPsensor.schedule(new TimerReadPsensor(), 1, 100);
        //Modify for P-sensor change by xiasiping 20140808 end
    }

        //Modify for P-sensor change by xiasiping 20140808 start
        @Override
        protected void onDestroy() {
//            mTimerReadPsensor.cancel();
            super.onDestroy();
            mHandler.removeCallbacksAndMessages(null);  //把消息对象从消息队列移除
        }

        //Modify for P-sensor change by xiasiping 20140808 end
	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mProximityListener);
		super.onPause();
	}

	private final SensorEventListener mProximityListener = new SensorEventListener() {

		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		public void onSensorChanged(SensorEvent event) {
//			Log.i("hwj","------- Proximity Sensor onSensorChanged");
			if (oldValue == 0) {
				oldValue = event.values[0];
                            Log.i(TAG,"oldValue == 0 event.values[0]= "+event.values[0]);
			}
			newValue = event.values[0];
                        Log.i(TAG,"newValue = event.values[0]= "+event.values[0]);
                        
                        //Modify for P-sensor change by xiasiping 20140808 start
			if (newValue < oldValue) {
                        //Modify for P-sensor change by xiasiping 20140808 end
				isNear = true;
			} else {
				isNear = false;
			}
                        Log.i(TAG,"isNear = "+isNear);
                        //Modify for P-sensor change by xiasiping 20140808 start
			//mtv_pro_info.setText(strPromityinfo + "\n" + newValue);
                        //Modify for P-sensor change by xiasiping 20140808 end
			oldValue = newValue;
                        //Add SensorCheck for ProximitySensor by xiasiping 20140626 start
                        if (SensorchangeTimes == 0L) {
                            mValue = newValue;
                        } else {
                            //Modify for P-sensor change by xiasiping 20140808 start
                            //float abs_mChange = Math.abs(newValue - mValue);
                            //if (abs_mChange > 5) {
                            if (mValue != newValue) {
                            //Modify for P-sensor change by xiasiping 20140808 end
                                btnPass.setEnabled(true);
                            }
                        }
                        SensorchangeTimes++;
                        //Add SensorCheck for ProximitySensor by xiasiping 20140626 end

            //add  by hwj20170112
            mValue_show_f = newValue;
            mHandler.sendEmptyMessage(MSG_SHOW_PSENSOR);    
            
			if (newValue <= fve && newValue >= egt) {
				return;
			}
//			Log.i("hwj","isNear --- " + isNear);
			if (isNear) {
                                //Modify for P-sensor change by xiasiping 20140808 start
//				if (newValue == 0) {
				if (newValue < 5) {
					imageView[0].setVisibility(View.INVISIBLE);
					imageView[1].setVisibility(View.VISIBLE);
				} else if (newValue >= 5) {
					imageView[0].setVisibility(View.VISIBLE);
					imageView[1].setVisibility(View.INVISIBLE);
				}
			} else {
				if (newValue > 5) {
					imageView[0].setVisibility(View.VISIBLE);
					imageView[1].setVisibility(View.INVISIBLE);
				} 
//				else if (newValue == 0) {
				else if (newValue < 5) {
					imageView[0].setVisibility(View.INVISIBLE);
					imageView[1].setVisibility(View.VISIBLE);
				}
                                //Modify for P-sensor change by xiasiping 20140808 end
			}
		}
	};

}
