package com.sim.cit;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sim.cit.testitem.BackLight;
import com.sim.cit.testitem.EcompassComplete;
import com.sim.cit.testitem.Gps;
import com.sim.cit.testitem.LightSensor;
import com.sim.cit.testitem.MotionSensor;
//import com.sim.cit.testitem.ProximitySensor;
import com.sim.cit.testitem.ProximitySensorCalibration;
import com.sim.cit.testitem.TouchPanel;
//import com.sim.cit.testitem.TouchScreenUpgrade;


public class TempListActivity extends ListActivity {
	private static final int GSENSOR = 0;
	private static final int MSENSOR = GSENSOR + 1;
	private static final int PSENSOR = MSENSOR + 1;
	private static final int LSENSOR = PSENSOR + 1;
	private static final int GPS = LSENSOR + 1;
	private static final int TP = GPS + 1;
	private static final int TP_CALIBRATION = TP + 1;
	private static final int PS_CALIBRATION = TP_CALIBRATION + 1;
	private static final int BACKLIGHT = PS_CALIBRATION + 1;
	
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		
		ArrayList<String> mainList = new ArrayList<String>();
		mainList.add("MotionSensor");
		mainList.add("Ecompass");
		mainList.add("ProximitySensor");
		mainList.add("LightSensor");
		mainList.add("Gps");
		mainList.add("TouchPanel");
		mainList.add("TPcalibration");
		mainList.add("PScalibration");
		mainList.add("Backlight");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mainList);
		getListView().setAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = null;
		switch (position) {
		case GSENSOR:
			intent = new Intent(mContext, MotionSensor.class);
			break;
		case MSENSOR:
			intent = new Intent(mContext, EcompassComplete.class);
			break;
//		case PSENSOR:
//			intent = new Intent(mContext, ProximitySensor.class);
//			break;
//		case LSENSOR:
//			intent = new Intent(mContext, LightSensor.class);
//			break;
//		case GPS:
//			intent = new Intent(mContext, Gps.class);
//			break;
//		case TP:
//			intent = new Intent(mContext, TouchPanel.class);
//			break;
//		case TP_CALIBRATION:
//			intent = new Intent(mContext, TouchScreenUpgrade.class);
//			break;
//		case PS_CALIBRATION:
//			intent = new Intent(mContext, ProximitySensorCalibration.class);
//			break;
//		case BACKLIGHT:
//			intent = new Intent(mContext, BackLight.class);
//			break;
//		default:
//			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}
}
