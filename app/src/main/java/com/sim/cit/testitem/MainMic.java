package com.sim.cit.testitem;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.SystemClock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.readboy.util.Recorder;
import com.readboy.util.RemainingTimeCalculator;
import com.sim.cit.TestActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sim.cit.R;



public class MainMic extends TestActivity {

    String mAudiofilePath;
    static String TAG = "MainMic";
    //MediaRecorder mMediaRecorder = new MediaRecorder();
    boolean isRecording = false;
    Button recordButton = null;
    Button stopButton = null;
    AudioManager mAudioManager;
    Context mContext;
    private boolean isExit = false;
    //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 start
    MediaPlayer mMediaPlayer;
    private boolean isComplete = false;
    //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 end
    private int alarmVolume;    //add for recover volume by lxx 20180808;
    private int musicVolume;
    private int callVolume;
    private int dtmfVolume;
    private int notificationVolume;
    private int ringVolume;
    private int systemVolume;
    private HomeKeyBroadCastReceiver mReceiver;

    private Recorder mRecorder;
    private RemainingTimeCalculator mRemainingTimeCalculator;
    int mAudioSourceType = MediaRecorder.AudioSource.MIC;
    static final int BITRATE_AMR =  12800; // bits/sec
    static final int SAMPLERATE_8000 = 8000;

    public void onCreate(Bundle savedInstanceState) {

        layoutId=R.layout.transmitter_receiver;
        super.onCreate(savedInstanceState);
        mContext = this;
        btnPass.setEnabled(false);
    }

    @Override
    public void onResume(){
        super.onResume();
        isRecording = false;
        init();
        getService();
        getVolumeBefore();
        bindView();
        mReceiver = new HomeKeyBroadCastReceiver();   //监听Home键
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        if (mAudioManager.isWiredHeadsetOn())
            showWarningDialog(getString(R.string.remove_headset));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (isRecording && mMediaRecorder!=null) {
//            mMediaRecorder.stop();
//            mMediaRecorder.release();
//            mMediaRecorder = null;
//        }
        if (!isComplete && mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                File file = new File(mAudiofilePath);
                file.delete();
            }catch (Exception e) {
                loge(e);
            }
        }
        //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 end
        //add for click fail button crash when recoding by song 20140506 end
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        //Add for adding second-mic test by lvhongshan 20140521 start
        mAudioManager.setParameters("second-mic=false");
        //Add for adding second-mic test by lvhongshan 20140521 end
        mRecorder.delete();
        recoverAudio();
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecorder.delete();
    }


    private void init(){
        mRemainingTimeCalculator = new RemainingTimeCalculator(this);
        mRecorder = new Recorder();
    }

    private void startRecord(String mStoragePath){
        mRemainingTimeCalculator.reset();
        mRemainingTimeCalculator.setStoragePath(0);
        mRecorder.setStoragePath(mStoragePath);

        mRemainingTimeCalculator.setBitRate(BITRATE_AMR);
        mRecorder.setChannels(1);
        mRecorder.setSamplingRate(SAMPLERATE_8000);
        mRecorder.startRecording(MediaRecorder.OutputFormat.RAW_AMR, ".amr", this,
                mAudioSourceType, MediaRecorder.AudioEncoder.AMR_NB);
    }
    private void stopRecord(){
        mRecorder.stop();
    }
    private void play(){
        mRecorder.startPlayback();
    }
    @Override
    public void finish() {
        isExit = true;
        super.finish();
//        if (isRecording && mMediaRecorder!=null) {
//            mMediaRecorder.stop();
//            mMediaRecorder.release();
//            mMediaRecorder = null;
//        }
        if (!isComplete && mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                File file = new File(mAudiofilePath);
                file.delete();
            }catch (Exception e) {
                loge(e);
            }
        }
        //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 end
        //add for click fail button crash when recoding by song 20140506 end
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        //Add for adding second-mic test by lvhongshan 20140521 start
        mAudioManager.setParameters("second-mic=false");
        //Add for adding second-mic test by lvhongshan 20140521 end
        mRecorder.delete();
        recoverAudio();
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

//    void record() throws IllegalStateException, IOException, InterruptedException {
//
//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        mMediaRecorder.setOutputFile(this.getCacheDir().getAbsolutePath() + "/test.aac");
//        mAudiofilePath = this.getCacheDir().getAbsolutePath() + "/test.aac";
//        mMediaRecorder.prepare();
//        mMediaRecorder.start();
//    }

    void replay() throws IllegalArgumentException, IllegalStateException, IOException {
        final TextView mTextView = (TextView) findViewById(R.id.transmitter_receiver_hint);
        mTextView.setText(getString(R.string.transmitter_receiver_playing));
        // Replaying sound right now by record();
        stopButton.setEnabled(false);
        //add for pass button enalbed by songguangyu 20140505 start
        btnPass.setEnabled(true);
        //add for pass button enalbed by songguangyu 20140505 end
        File file = new File(mAudiofilePath);
        FileInputStream mFileInputStream = new FileInputStream(file);

        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(mFileInputStream.getFD());
        mMediaPlayer.prepare();
        mMediaPlayer.start();

        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            public void onCompletion(MediaPlayer mPlayer) {
                //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 start
                isComplete = true;
                //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 end
                mPlayer.stop();
                mPlayer.release();
                File file = new File(mAudiofilePath);
                file.delete();
                recordButton.setClickable(true);
                final TextView mTextView = (TextView) findViewById(R.id.transmitter_receiver_hint);
                mTextView.setText(getString(R.string.transmitter_receiver_replay_end));
                // showConfirmDialog();
                if (!isExit) {
                    //       showWarningDialog(getString(R.string.record_finish));
                }
                //modify for pass button enalbed by songguangyu 20140505 start
                //btnPass.setEnabled(true);
                //modify for pass button enalbed by songguangyu 20140505 end
            }
        });

    }

    void showWarningDialog(String title) {

        new AlertDialog.Builder(mContext).setTitle(title).setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    void showConfirmDialog() {

        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.transmitter_receiver_confirm)).setPositiveButton(
                getString(R.string.yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //    pass();
                    }
                }).setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        //   fail(null);
                    }
                }).show();
    }

    public void setAudio() {

//        mAudioManager.setMode(AudioManager.MODE_IN_CALL);

        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF, mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_DTMF), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_RING), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0);
        //Add for adding second-mic test by lvhongshan 20140521 start
//        mAudioManager.setParameters("second-mic=true");
        //Add for adding second-mic test by lvhongshan 20140521 end
    }

    public void getVolumeBefore() {
        alarmVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        musicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        callVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        dtmfVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_DTMF);
        notificationVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        ringVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        systemVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    public void recoverAudio() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,alarmVolume,0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,musicVolume,0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,callVolume,0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,dtmfVolume,0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,notificationVolume,0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING,ringVolume,0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,systemVolume,0);
    }

    void bindView() {

        recordButton = (Button) findViewById(R.id.transmitter_receiver_start);
        stopButton = (Button) findViewById(R.id.transmitter_receiver_stop);
        final TextView mTextView = (TextView) findViewById(R.id.transmitter_receiver_hint);
        mTextView.setText(getString(R.string.transmitter_receiver_to_record));
        stopButton.setEnabled(false);
        recordButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (!mAudioManager.isWiredHeadsetOn()) {

                    mTextView.setText(getString(R.string.transmitter_receiver_recording));
                    try {
                        setAudio();
                        recordButton.setClickable(false);
                        startRecord(Environment.getExternalStorageDirectory().toString()+"/smt_detection");
                        stopButton.setEnabled(true);
                        isRecording = true;

                    } catch (Exception e) {
                        loge(e);
                    }
                } else
                    showWarningDialog(getString(R.string.remove_headset));

            }
        });

        stopButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                mMediaPlayer = new MediaPlayer();
                if (isRecording) {
                    //add for click fail button crash when recoding by song 20140506 start
                    isRecording = false;
                    //add for click fail button crash when recoding by song 20140506 end
                    stopRecord();
                    play();
                    try {
                        replay();
                    } catch (Exception e) {
                        loge(e);
                    }
                } else
                    showWarningDialog(getString(R.string.transmitter_receiver_record_first));
            }
        });
    }

    void getService() {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    void fail(Object msg) {

        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
        finish();
    }

    void pass() {

        setResult(RESULT_OK);
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

/*	private TextView tvMes;
	private AudioManager am;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	//	isAutoPassOrFail = false;
		layoutId = R.layout.test_loopback;
		super.onCreate(savedInstanceState);
		tvMes = (TextView)findViewById(R.id.tv_mes);
		am = (AudioManager) getSystemService(AUDIO_SERVICE);
		tvMes.setText(R.string.SpeakerLoopback_illustration);
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
	}*/

    public class HomeKeyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 在这里处理HomeKey事件
            recoverAudio();
        }
    }
}
