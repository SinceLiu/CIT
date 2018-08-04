package com.sim.cit.testitem;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.TextView;

import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class EcompassPCB extends TestActivity {
	private TextView tvX;
	private TextView tvY;
	private TextView tvZ;

	private int[] mMagneticValue = new int[3];
	private boolean isTest;
	
	private SensorManager mSensorManager;
	private Sensor mSensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutId = R.layout.ecompass_pcb;
		super.onCreate(savedInstanceState);
		
		tvX = (TextView)findViewById(R.id.txt_x);
		tvY = (TextView)findViewById(R.id.txt_y);
		tvZ = (TextView)findViewById(R.id.txt_z);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		mSensorManager.registerListener(eventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		
		isTest = true;
		new Thread(){
			@Override
			public void run() {
				super.run();
				while (isTest) {
					mMagneticValue = CommonDrive.getCompassValues();
					mHandler.sendMessage(new Message());
					try {
						sleep(200);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(eventListener);
		isTest = false;
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			tvX.setText("X = " + mMagneticValue[0]);
			tvY.setText("Y = " + mMagneticValue[1]);
			tvZ.setText("Z = " + mMagneticValue[2]);
		}
	};

	private SensorEventListener eventListener = new SensorEventListener() {
		
		public void onSensorChanged(SensorEvent event) {
			
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}
	};
}
