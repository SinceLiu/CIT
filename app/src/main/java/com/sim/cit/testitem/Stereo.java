package com.sim.cit.testitem;

import java.io.File;

import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
//import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.sim.cit.CITTestHelper;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class Stereo extends TestActivity {
private static final String TAG = "CIT_StereoTest";

	private Button btnPass;
        private Button btnLeft;
        private Button btnRight;
	private AudioManager am;
	private MediaPlayer mp = null;
	private int nCurrentMusicVolume;
	private int dw=0;
	private boolean isTest=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutId = R.layout.stereo;
		super.onCreate(savedInstanceState);
		
		btnLeft = (Button)findViewById(R.id.leftChannel);
		btnRight = (Button)findViewById(R.id.rightChannel);
		
		btnLeft.setOnClickListener(new OnClickListener() {

                   public void onClick(View v) {
            	      switch (dw) {
        		case 0:
        			btnLeft.setText(R.string.stereo_stop);
        			btnRight.setEnabled(false);
                                btnPass.setEnabled(false);
        			dw=1;
        			startSpeaker(0);
        			break;
        		case 1:
        			btnLeft.setText(R.string.stereo_left);
        			btnRight.setEnabled(true);
                                btnPass.setEnabled(true);
        			dw=0;
                                if (mp != null) {
			            mp.stop();
			            mp.release();
		                }
		                if (am != null) {
			            am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume, 0);
		                }
        			break;
            	     }
                 }
           });
		
		btnRight.setOnClickListener(new OnClickListener() {

                   public void onClick(View v) {

            	      switch (dw) {
        		case 0:
        			btnRight.setText(R.string.stereo_stop);
        			btnLeft.setEnabled(false);
                                btnPass.setEnabled(false);
        			dw=1;
        			startSpeaker(1);
        			break;
        		case 1:
        			btnRight.setText(R.string.stereo_right);
        			btnLeft.setEnabled(true);
                                btnPass.setEnabled(true);
        			dw=0;
                                if (mp != null) {
			            mp.stop();
			            mp.release();
		                }
		                if (am != null) {
			            am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume, 0);
		                }
        			break;
            	     }
                  }
            });
		btnPass = super.btnPass;
	}
	
	private void startSpeaker(int flag){
		Log.i(TAG, "speaker start");
		am = (AudioManager) getSystemService(AUDIO_SERVICE);
		//am.setMode(AudioManager.ROUTE_SPEAKER);
		/*20130222 modify for fix bug 22887 by lvhongshan start*/
	//	if(!am.isSpeakerphoneOn()) {
	//		am.setSpeakerphoneOn(true);
	//	}
                /*20130222 modify for fix bug 22887 by lvhongshan end*/
		int nMaxMusicVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		nCurrentMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		am.setStreamVolume(AudioManager.STREAM_MUSIC, nMaxMusicVolume, 0);
                if(flag==0){
        	    mp = MediaPlayer.create(getApplicationContext(), R.raw.left);
                }
                else if(flag==1){
        	    mp = MediaPlayer.create(getApplicationContext(), R.raw.right);
                }
		if (mp != null) {
			mp.setLooping(true);
			mp.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		isTest=false;
/*		if (mp != null) {
			mp.stop();
			mp.release();
		}
		if (am != null) {
			am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume, 0);
		}*/
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
}
