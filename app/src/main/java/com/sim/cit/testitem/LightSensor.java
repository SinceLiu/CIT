package com.sim.cit.testitem;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class LightSensor extends TestActivity {
        //Add SensorCheck for LightSensor by xiasiping 20140626 start
        private long SensorchangeTimes = 0L;
        private float mValue;
        //Add SensorCheck for LightSensor by xiasiping 20140626 end
	private TextView tvData;
	private TextView tvTips;
	private SensorManager mSensorManager;
	private Sensor mLightSensor;
	private int oldLight;
	private int[] luxes = new int[] { 
			0, 50, 100, 150, 200, 400, 600, 800, 1000,
			2000, 5000, 10000, 20000, 30000 
	};
	private int[] brights = new int[]{
			25, 45, 45, 63, 63, 82, 82, 100, 100,
			118, 135, 183, 198, 221
	};
	private PowerManager powerManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutId = R.layout.two_txt;
		super.onCreate(savedInstanceState);
                //Add SensorCheck for LightSensor by xiasiping 20140626 start
                btnPass.setEnabled(false);
                //Add SensorCheck for LightSensor by xiasiping 20140626 end
		
                tvData = (TextView)findViewById(R.id.txt_two);
                tvTips = (TextView)findViewById(R.id.txt_one);
                //Modify for remove the useless words on the screen by lizhaobo 20151110 start
                //tvTips.setText(getString(R.string.light_sensor_tips));
                //Modify for remove the useless words on the screen by lizhaobo 20151110 end

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//2012-12-06 add for test by lvhongshan--start
		if(null==mLightSensor){
			Log.i("lvhongshan_LightSensor", "LightSensor is "+"null");
		}
		else{
			Log.i("lvhongshan_LightSensor", "LightSensor is "+"not null");
		}	
//2012-12-06 add for test by lvhongshan--start
		powerManager=(PowerManager) getSystemService(Context.POWER_SERVICE);
		//2012-12-06 add for test by lvhongshan--start
				if(null==powerManager){
					Log.i("lvhongshan_powerManager", "powerManager is "+"null");
				}
				else{
					Log.i("lvhongshan_powerManager", "powerManager is "+"not null");
				}	
		//2012-12-06 add for test by lvhongshan--start		
		try {
			oldLight = Settings.System.getInt(LightSensor.this.getContentResolver(), 
			        Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

		tvData.setText("Light Sensor");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		CommonDrive.backlightControl(oldLight);
		powerManager.setBacklightBrightness(oldLight);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mLightSensorListener);
	}

    @Override
    protected void onResume() {
        super.onResume();

        if(mLightSensor == null) {
            Toast.makeText(this, "LightSensor is null: ", 2000).show();
        }
        boolean bSucceed = mSensorManager.registerListener(mLightSensorListener,mLightSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
        if(!bSucceed) {
            Toast.makeText(this, "registerListener is faild: ", 2000).show();
        }
    }
	
	private final SensorEventListener mLightSensorListener = new SensorEventListener(){

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}

		public void onSensorChanged(SensorEvent event) {
			/*int degree = 0;
			try {
				degree = Integer.parseInt(CommonDrive.lightDegree().trim());
				Log.i("AmbientTest", "original data = " + degree);
			} catch (NumberFormatException e) {
				Log.i("AmbientTest", "original data is not number.");
				return;
			}*/
			
			float lux = event.values[0];
			int blDegree = 0;
			for (int i = 0; i < brights.length; i++) {
				if (i + 1 < brights.length) {
					if (lux >= luxes[i] && lux < luxes[i + 1]) {
						blDegree = brights[i];
						Log.i("AmbientTest", "blDegree = " + blDegree);
					}
				}else{
					if (lux >= luxes[brights.length - 1]) {
						blDegree = brights[brights.length - 1];
						Log.i("AmbientTest", "blDegree = " + blDegree);
					}
				}
			}
                        //Add SensorCheck for MotionSensor by xiasiping 20140626 start
                        if (SensorchangeTimes == 0L) {
                            mValue = lux;
                        } else {
                            float abs_mChange = Math.abs(lux - mValue);
                            if (lux > 30 && abs_mChange > 10) {
                                btnPass.setEnabled(true);
                            } else if (lux <= 30 && lux >= 10 && abs_mChange > 5) {
                                btnPass.setEnabled(true);
                            } else if (lux < 10 && abs_mChange > 2) {
                                btnPass.setEnabled(true);
                            }
                        }
                        SensorchangeTimes++;
                        //Add SensorCheck for MotionSensor by xiasiping 20140626 end

			powerManager.setBacklightBrightness(blDegree);
//			CommonDrive.backlightControl(blDegree);
			tvData.setText(getString(R.string.lux_data) + lux + "\n"
//					+ getString(R.string.original_data) + degree + "\n"
					+ getString(R.string.backlight_data) + blDegree);
		}
   };

}

