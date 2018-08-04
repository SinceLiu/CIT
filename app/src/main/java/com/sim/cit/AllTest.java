package com.sim.cit;

import java.util.ArrayList;


import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AllTest extends ListActivity {
	private static final int COLLIGATETEST=0;
	private static final int BACKLIGHT_SLEEP = COLLIGATETEST+1;
	private static final int FRONT_CAMMERA_TEST = BACKLIGHT_SLEEP + 1;
	private static final int Back_CAMMERA_TEST = FRONT_CAMMERA_TEST + 1;
	private static final int KEYPAD = Back_CAMMERA_TEST + 1;
	private static final int BATTERY = KEYPAD + 1;
	private static final int CHARGE = BATTERY + 1;
	private static final int UPDATE_GAEGEIC = CHARGE + 1;
	private static final int RECEIVER_HEADSET_LOOPBACK = UPDATE_GAEGEIC + 1;
	private static final int LCD = RECEIVER_HEADSET_LOOPBACK + 1;
	private static final int TOUCH = LCD + 1;
	private static final int MOTION_SENSOR_CALIBRATION = TOUCH+1 ;
	private static final int MOTION_SENSOR = MOTION_SENSOR_CALIBRATION + 1;
	private static final int PROXIMITY_CALIBRATION = MOTION_SENSOR + 1;
	private static final int PROXIMITY_SENSOR = PROXIMITY_CALIBRATION + 1;
	private static final int GPS = PROXIMITY_SENSOR+1;
	private static final int WIFISETING = GPS + 1;
	private static final int BLUETOOTH = WIFISETING + 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<String> list = new ArrayList<String>();
		String[] items = getResources().getStringArray(R.array.all_test);
		for (int i = 0; i < items.length; i++) {
			list.add(items[i]);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		getListView().setAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent i = null;
		
//		switch (position) {
//		case COLLIGATETEST:
//			i = new Intent(AllTest.this, Colligate.class);
//			startActivity(i);
//			break;
//		case BACKLIGHT_SLEEP:
//			i = new Intent(AllTest.this, BackLight.class);
//			startActivity(i);
//			break;
//		case FRONT_CAMMERA_TEST:
//			i = new Intent(AllTest.this, FrontCamera.class);
//			startActivity(i);
//			break;
//		case Back_CAMMERA_TEST:
//			i = new Intent(AllTest.this, BackCamera.class);
//			startActivity(i);
//			break;
//		case RECEIVER_HEADSET_LOOPBACK:
//			i = new Intent(AllTest.this, Loopback.class);
//			startActivity(i);
//			break;
//		case KEYPAD:
//			i = new Intent(AllTest.this, Keypad.class);
//			startActivity(i);
//			break;
//		case BATTERY:
//			i = new Intent(AllTest.this, BatteryInfo.class);
//			startActivity(i);
//			break;
//		case CHARGE:
//			i = new Intent(AllTest.this, ChargeState.class);
//			startActivity(i);
//			break;
//		case UPDATE_GAEGEIC:
//			i = new Intent(AllTest.this, UpdateGaugeIC.class);
//			startActivity(i);
//			break;
//		case LCD:
//			i = new Intent(AllTest.this, Lcd.class);
//			startActivity(i);
//			break;
//		case TOUCH:
//			i = new Intent(AllTest.this, TouchPanel.class);
//			startActivity(i);
//			break;
//		case MOTION_SENSOR_CALIBRATION:
//			i = new Intent(AllTest.this, MotionSensorCalibration.class);
//			startActivity(i);
//			break;
//		case MOTION_SENSOR:
//			i = new Intent(AllTest.this, MotionSensor.class);
//			startActivity(i);
//			break;
//		case PROXIMITY_CALIBRATION:
//			i = new Intent(AllTest.this, ProximitySensorCalibration.class);
//			startActivity(i);
//			break;
//		case PROXIMITY_SENSOR:
//			i = new Intent(AllTest.this, ProximitySensor.class);
//			startActivity(i);
//			break;
//		case GPS:
//			i = new Intent(AllTest.this, Gps.class);
//			i.putExtra("cit3", true);
//			startActivity(i);
//			break;
//		case WIFISETING:
//			i = new Intent(AllTest.this,Wlan.class);
//			startActivity(i);
//			break;
//		case BLUETOOTH:
//			try {
//				i = new Intent(AllTest.this,Bluetooth.class);
//				startActivity(i);
//			} catch (ActivityNotFoundException e) {
//				e.printStackTrace();
//			}
//			break;
//		default:
//			break;
//		}
	}
}

