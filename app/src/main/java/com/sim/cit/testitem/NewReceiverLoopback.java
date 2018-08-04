package com.sim.cit.testitem;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class NewReceiverLoopback extends TestActivity {
	private TextView tvMes;
	private AudioManager am;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	//	isAutoPassOrFail = false;
		layoutId = R.layout.test_loopback;
		super.onCreate(savedInstanceState);
		
		tvMes = (TextView)findViewById(R.id.tv_mes);
		
		am = (AudioManager) getSystemService(AUDIO_SERVICE);

		tvMes.setText(R.string.receiver_illustration);
		
	}

	@Override
	protected void onPause() {
		am.setParameters("loopback=off");
		SystemClock.sleep(1000);
		super.onPause();
	}

	@Override
	protected void onResume() {
		am.setParameters("loopback=receiver");
		super.onResume();
	}
}
