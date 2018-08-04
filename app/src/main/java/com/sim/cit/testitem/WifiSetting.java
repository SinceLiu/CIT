package com.sim.cit.testitem;

import android.content.Intent;
import android.os.Bundle;

import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class WifiSetting extends TestActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		layoutId = R.layout.wifiseting_page;
		super.onCreate(savedInstanceState);

		Intent i = new Intent();
		i.setClassName("com.android.settings",
				"com.android.settings.wifi.WifiSettings");
		startActivity(i);

	}

}
