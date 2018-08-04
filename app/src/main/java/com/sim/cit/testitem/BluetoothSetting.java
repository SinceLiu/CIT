package com.sim.cit.testitem;

import android.content.Intent;
import android.os.Bundle;

import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class BluetoothSetting extends TestActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		layoutId = R.layout.bluetooth_setingpage;
		super.onCreate(savedInstanceState);
		Intent i = new Intent("android.settings.BLUETOOTH_SETTINGS");
		startActivity(i);
	}

}
