package com.sim.cit.testitem;

import java.io.File;

import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.os.StatFs;
import android.os.Vibrator;
//import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sim.cit.CITTestHelper;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.os.storage.StorageManager;


public class SpeakerPinknoiseTest extends TestActivity {
    private static final String TAG = "CIT_SpeakerPinknoiseTest";
    private TextView tvSpeaker;
    private AudioManager am;
    private MediaPlayer mp = null;
    private int oldBrightValue;
    private boolean isTest=true;
    private int nCurrentMusicVolume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.receivertest;
        super.onCreate(savedInstanceState);
        tvSpeaker = (TextView)findViewById(R.id.txt_hint);
        tvSpeaker.setText(getString(R.string.str_speaker_speaker));
    }

    private void startSpeaker(){
        Log.i(TAG, "SpeakerPinknoiseTest start");
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMaxMusicVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        nCurrentMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, nMaxMusicVolume, 0);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.pinknoise);
        if (mp != null) {
        mp.setLooping(true);
        mp.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isTest=false;
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume, 0);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        startSpeaker();
    }
}
