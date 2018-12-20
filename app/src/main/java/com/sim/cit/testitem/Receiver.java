package com.sim.cit.testitem;

import java.io.File;
import android.app.Activity;
import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sim.cit.CITTestHelper;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class Receiver extends TestActivity {
    private static final String TAG = "CIT_Receiver";
    private static final int MSG_SHOW_TIME = 0;
    private TextView tvTime;
    private TextView tvHint;
    private AudioManager am;
    private MediaPlayer mp = null;
    private int oldBrightValue;
    private int nCurrentMusicVolume;
    private boolean isTest=true;
    private int mSeconds = 0;
    private int mMinutes = 0;
    private int mHours = 0;
    private Timer mCalculateTimeTimer = new Timer();
    private String mSpentTime = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.receivertest;
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.receivertest);
        tvHint = (TextView)findViewById(R.id.txt_hint);
        tvTime = (TextView)findViewById(R.id.txt_time);
        tvHint.setText(getString(R.string.str_receiver_hint));
        tvTime.setText(getString(R.string.str_receiver_time));
    }

    private void startReceiver(){
        Log.i(TAG, "speaker start");
        mCalculateTimeTimer.schedule(new CalculateTimeTask(), 1, 1000);
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_CALL);
        int nMaxMusicVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        nCurrentMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, nMaxMusicVolume, 0);
        am.setParameters("cit_receiver=true");
        Uri uri = Uri.parse(CITTestHelper.COLLIGATE_SOUND_PATH);
        //mp = MediaPlayer.create(getApplicationContext(), uri);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.receiver);
        if(mp != null) {
            mp.setLooping(true);
            mp.start();
        }
    }

    @Override
    protected void onPause() {
        am.setParameters("cit_receiver=false");
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume, 0);
            am.setMode(AudioManager.MODE_NORMAL);
        }
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        startReceiver();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isTest=false;

        mCalculateTimeTimer.cancel();
    }
    class CalculateTimeTask extends TimerTask {
        public void run() {
            mSeconds++;
            mSeconds = mSeconds % 60;
            if (mSeconds == 0) {
                mMinutes++;
                mMinutes = mMinutes % 60;
                if (mMinutes == 0) {
                    mHours++;
                }
            }
            Log.i(TAG, "mMinutes = " + mMinutes + ", mHours = " + mHours);
            mSpentTime = convertTimeToString(mHours, mMinutes, mSeconds);
            mHandler.sendEmptyMessage(MSG_SHOW_TIME);
        }
    }
    Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
            tvTime.setText(getString(R.string.str_receiver_time)+" " + mSpentTime);
    	};
    };
    private String convertIntegerToString(int n) {
        if (n >= 10) {
            return String.valueOf(n);
        } else {
            return "0" + String.valueOf(n);
        }
    }

    private String convertTimeToString(int h, int m, int s) {
        return convertIntegerToString(h) + ":" +
            convertIntegerToString(m) + ":" +
            convertIntegerToString(s);
    }
}
