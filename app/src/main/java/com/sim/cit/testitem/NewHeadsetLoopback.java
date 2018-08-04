package com.sim.cit.testitem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class NewHeadsetLoopback extends TestActivity {
	private TextView tvMes;
	private AudioManager am;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	//	isAutoPassOrFail = false;
		layoutId = R.layout.test_loopback;
		super.onCreate(savedInstanceState);

		tvMes = (TextView)findViewById(R.id.tv_mes);

		am = (AudioManager) getSystemService(AUDIO_SERVICE);

		tvMes.setText(R.string.headset_illustration1);
		
	}
	
	@Override
	protected void onPause() {
		am.setParameters("loopback=off");
		unregisterReceiver(headsetReceiver);
		SystemClock.sleep(1000);
		super.onPause();
	}

	@Override
	protected void onResume() {
		registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		super.onResume();
	}
	
	private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
				int state = intent.getIntExtra("state", 0);
				if (state == 1) {
					tvMes.setText(R.string.headset_illustration2);
					am.setParameters("loopback=headset");
				}else{
					tvMes.setText(R.string.headset_illustration1);
					am.setParameters("loopback=off");
				}
			}
		}
	};
}
