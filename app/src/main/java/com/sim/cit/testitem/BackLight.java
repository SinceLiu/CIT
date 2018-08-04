package com.sim.cit.testitem;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.TextView;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
//Modify for 501 can not see the word on screen by lizhaobo 20151104 start
import android.graphics.Color;
//Modify for 501 can not see the word on screen by lizhaobo 20151104 end

public class BackLight extends TestActivity {
	private static final String TAG = BackLight.class.getSimpleName();
	private TextView txtMsg;
	private int oldLight;
	PowerManager powerManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "-------------------onCreate");
		layoutId = R.layout.one_txt;
		super.onCreate(savedInstanceState);
		//Add sleepcheck for Sleep/wake Test by xiasiping 20140626 start
                btnPass.setEnabled(false);
		//Add sleepcheck for Sleep/wake Test by xiasiping 20140626 end
		
		getWindow().setBackgroundDrawableResource(R.color.white);
		
		txtMsg = (TextView)findViewById(R.id.txt_one);
                //Modify for 501 can not see the word on screen by lizhaobo 20151104 start
                txtMsg.setTextColor(Color.BLACK);
                //Modify for 501 can not see the word on screen by lizhaobo 20151104 end
		txtMsg.setText(R.string.backlight_illustration);
		
		try {
			oldLight = Settings.System.getInt(getContentResolver(), 
			        Settings.System.SCREEN_BRIGHTNESS);
			Log.i("progress", "backlight oldlight="+oldLight);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		powerManager=(PowerManager) getSystemService(Context.POWER_SERVICE);
		
		new Thread(){
			@Override
			public void run() {
				super.run();
				try {
					sleep(500);
				} catch (InterruptedException e1) {
				}
				for (int i = oldLight; i >= 25; i-=2) {
					try {
						Log.i(TAG, "------------------------sdfsdfsdf");
						powerManager.setBacklightBrightness(i);
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
	}

	
	//add by lqn at 2017.3.7
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i(TAG, "-------------------newConfig = "+newConfig.orientation);
		btnPass.setEnabled(true);
	}

	

		//Add sleepcheck for Sleep/wake Test by xiasiping 20140626 start
        @Override
	protected void onRestart() {
                btnPass.setEnabled(true);
                Log.i("BackLight", "xsp_onRestart!~ ");
		super.onRestart();
	}
	//Add sleepcheck for Sleep/wake Test by xiasiping 20140626 end

	@Override
	protected void onDestroy() {
		super.onDestroy();
                Log.i("BackLight", "xsp_onDestroy!~ ");
                try {
                	powerManager.setBacklightBrightness(oldLight);
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
}
