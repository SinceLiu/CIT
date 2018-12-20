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
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sim.cit.CITTestHelper;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.os.storage.StorageManager;


public class SpeakerTest extends TestActivity {
    private static final String TAG = "CIT_Speaker";
    private TextView tvSpeaker;
    private AudioManager am;
    private MediaPlayer mp = null;
    private int oldBrightValue;
    private boolean isTest=true;
    private int nCurrentMusicVolume;
    private int nMaxMusicVolume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.receivertest;
        super.onCreate(savedInstanceState);
        tvSpeaker = (TextView)findViewById(R.id.txt_hint);
        tvSpeaker.setText(getString(R.string.str_speaker_speaker));
    }

    private void startSpeaker(){
        Log.i(TAG, "speaker start");
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        nMaxMusicVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        nCurrentMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume, 0);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.speaker);
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
            nCurrentMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //获取最新音量（滑动调节？)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume, 0);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        startSpeaker();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        nCurrentMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //获取最新音量
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (nCurrentMusicVolume < nMaxMusicVolume)
                    nCurrentMusicVolume++;
                am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (nCurrentMusicVolume > 0)
                    nCurrentMusicVolume--;
                am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode,event);
    }
}
