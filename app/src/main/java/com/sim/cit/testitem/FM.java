/*
 * Copyright (c) 2011-2012, QUALCOMM Incorporated.
 * All Rights Reserved.
 * QUALCOMM Proprietary and Confidential.
 * Developed by QRD Engineering team.
 */
package com.sim.cit.testitem;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class FM extends TestActivity {

    static String TAG = "FM";
//    Button searchButton, passButton, failButton;
    Button searchButton;
    TextView mTextView;
    AudioManager mAudioManager = null;
    FmManager mFmManager = null;
    Context mContext = null;
    public ProgressDialog dialog;
    public ScanThread st;
    // Add Headset detect to open FM by lishuai 20140110 start
    private BroadcastReceiver mHeadsetReceiver;
    // Add Headset detect to open FM by lishuai 20140110 end
    public boolean isReceiverBroadcast = false;
    private int def_frequ = 98500;
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                mTextView.setText(new Float(mFmManager.getFrequency() / 1000f).toString() + "MHZ");
            //    dialog.dismiss();
            }
        };
    };

    @Override
    public void finish() {
        //modify for open fm in cit reflect slow by songguangyu 20140319 -bug14182 start
        //mFmManager.test(false);
        //modify for open fm in cit reflect slow by songguangyu 20140319 -bug14182 end
        mFmManager.closeFM();

        super.finish();
      //  dialog.dismiss();
    }

    void getService() {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mFmManager = new FmManager(mContext, mHandler, def_frequ);
    }

    void bindView() {

        searchButton = (Button) findViewById(R.id.fm_search);
//        passButton = (Button) findViewById(R.id.fm_pass);
//        failButton = (Button) findViewById(R.id.fm_fail);
        mTextView = (TextView) findViewById(R.id.fm_frequency);
		  mTextView.setText("98.5" + "MHZ");//new code

        searchButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (mAudioManager.isWiredHeadsetOn()) {
//                    setButtonClickable(true);
                   // dialog.show();
                    //st.start();
                    Log.i(TAG,"dialog.show()");
                    if(isReceiverBroadcast){
                        mFmManager.searchUP();
                    }else {
                        Log.i(TAG,"isReceiverBroadcast is false");
                        mFmManager.openFM();
                        mFmManager.mReceiver.enable(mFmManager.getFmDefConfig());
                        AudioSystem.setForceUse(AudioSystem.FOR_MEDIA, AudioSystem.FORCE_NONE);
                        Intent mIntent = new Intent("android.intent.action.FM");
                        mIntent.putExtra("state", 1);
                        getApplicationContext().sendBroadcast(mIntent);
                        startFM();
                        isReceiverBroadcast = true;
                    }
                    //st.stopThread();
                   // dialog.dismiss();
                } else {
//                    setButtonClickable(false);
                    showWarningDialog(getString(R.string.insert_headset));
                }
            }
        });

/*        passButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                pass();
            }
        });

        failButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                fail(null);
            }
        });*/

    }

    public void setAudio() {

        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        float ratio = 1.0f;
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        layoutId = R.layout.fm;
        super.onCreate(savedInstanceState);
        mContext = this;

//        setContentView(R.layout.fm);
        getService();
        setAudio();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        bindView();
//        dialog = new ProgressDialog(this);
//	dialog.setCancelable(false);
//	dialog.setMessage(getString(R.string.wait));
        st = new ScanThread();

        if (!mAudioManager.isWiredHeadsetOn()) {
//            setButtonClickable(false);
            showWarningDialog(getString(R.string.insert_headset));
        // Add Headset detect to open FM by lishuai 20140110 start
        } else {
            if (!mFmManager.openFM()){
                Toast.makeText(this, getString(R.string.openFM_fail) , 3000).show();
            }
            //mFmManager.openFM();
            AudioSystem.setForceUse(AudioSystem.FOR_MEDIA, AudioSystem.FORCE_NONE);
            //modify for open fm in cit reflect slow by songguangyu 20140319 -bug14182 start
            //mFmManager.test(true);
            //modify for open fm in cit reflect slow by songguangyu 20140319 -bug14182 end
            Intent mIntent = new Intent("android.intent.action.FM");
            mIntent.putExtra("state", 1);
            getApplicationContext().sendBroadcast(mIntent);
            startFM();
            isReceiverBroadcast = true;
        }
        mHeadsetReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context,Intent intent){
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                    if (intent.getIntExtra("state", 0) == 1) {
                        mFmManager.openFM();
                        mFmManager.mReceiver.enable(mFmManager.getFmDefConfig());
                        AudioSystem.setForceUse(AudioSystem.FOR_MEDIA, AudioSystem.FORCE_NONE);
                        //modify for open fm in cit reflect slow by songguangyu 20140319 -bug14182 start
                        //mFmManager.test(true);
                        //modify for open fm in cit reflect slow by songguangyu 20140319 -bug14182 end
                        Intent mIntent = new Intent("android.intent.action.FM");
                        mIntent.putExtra("state", 1);
                        getApplicationContext().sendBroadcast(mIntent);
                        startFM();
                        isReceiverBroadcast = true;
                    } else {
                        showWarningDialog(getString(R.string.insert_headset));
                        if (mFmManager.mReceiver != null) {
                            mFmManager.mReceiver.disable();
                            mFmManager.mReceiver = null;
                        }
                    }
                }
            }
        // Add Headset detect to open FM by lishuai 20140110 end
        };
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mHeadsetReceiver, iFilter);

    }

    void showWarningDialog(String title) {

        new AlertDialog.Builder(mContext).setTitle(title)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

/*    void setButtonClickable(boolean cmd) {

        passButton.setClickable(cmd);
        passButton.setFocusable(cmd);
        failButton.setClickable(cmd);
        failButton.setFocusable(cmd);

    }*/

    @Override
    protected void onDestroy() {
        // Add Headset detect to open FM by lishuai 20140110 start
        if (mHeadsetReceiver != null) {
            unregisterReceiver(mHeadsetReceiver);
            mHeadsetReceiver = null;
        }
        // Add Headset detect to open FM by lishuai 20140110 end
        super.onDestroy();
    }

    void fail(Object msg) {

        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
//        Utilities.writeCurMessage(mContext, TAG, "Failed");
        finish();
    }

    void pass() {

        setResult(RESULT_OK);
//        Utilities.writeCurMessage(mContext, TAG, "Pass");
        finish();
    }

    public void toast(Object s) {

        if (s == null)
            return;
        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
    }

    private void loge(Object e) {

        if (e == null)
            return;
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();
        e = "[" + mMethodName + "] " + e;
        Log.e(TAG, e + "");
    }

    @SuppressWarnings("unused")
    private void logd(Object s) {

        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }

    class ScanThread extends Thread{
                private boolean flag=true;
		public void run(){
                    if(flag){

                        //Message msg =new Message();
			//handler.sendMessageDelayed(msg, 100);
                     }
                     else{
                        dialog.dismiss();
                     }
		}
                public void stopThread(){
                       flag=false;
                }
        }

    private void startFM(){
            AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_OUT_FM,
                                AudioSystem.DEVICE_STATE_AVAILABLE, "");
            AudioSystem.setForceUse(AudioSystem.FOR_MEDIA, AudioSystem.FORCE_NONE);
            mFmManager.mReceiver.setStation(98500);
    }
}

