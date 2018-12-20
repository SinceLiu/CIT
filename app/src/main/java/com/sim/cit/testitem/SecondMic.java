package com.sim.cit.testitem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

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
import com.sim.cit.TestActivity;

public class SecondMic extends TestActivity {
    String mAudiofilePath;
    File file;
    static String TAG = "SecondMic";
    MediaRecorder mMediaRecorder;
    boolean isRecording = false;
    Button recordButton = null;
    Button stopButton = null;
    TextView mTextView;
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

    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate()");
        layoutId = R.layout.transmitter_receiver;
        super.onCreate(savedInstanceState);
        mContext = this;
        btnPass.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
        isRecording = false;
        getService();
        getVolumeBefore();
        bindView();
        if (mAudioManager.isWiredHeadsetOn())
            showWarningDialog(getString(R.string.remove_headset));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");
        if (isRecording && mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 start
        if (!isComplete && mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                deleteRecordResource();
            } catch (Exception e) {
                loge(e);
            }
        }
        //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 end
        //add for click fail button crash when recoding by song 20140506 end
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        //Add for adding second-mic test by lvhongshan 20140521 start
        mAudioManager.setParameters("second-mic=false");
        //Add for adding second-mic test by lvhongshan 20140521 end
        recoverAudio();
    }

    @Override
    public void finish() {
        isExit = true;
        super.finish();
        //add for click fail button crash when recoding by song 20140506 start
        if (isRecording && mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 start
        if (!isComplete && mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                deleteRecordResource();
            } catch (Exception e) {
                loge(e);
            }
        }
        //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 end
        //add for click fail button crash when recoding by song 20140506 end
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        //Add for adding second-mic test by lvhongshan 20140521 start
        mAudioManager.setParameters("second-mic=false");
        //Add for adding second-mic test by lvhongshan 20140521 end
    }

    void record() throws IllegalStateException, IOException, InterruptedException {
        mAudiofilePath = this.getCacheDir().getAbsolutePath() + "/test.amr";
        file = new File(mAudiofilePath);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        Log.e(TAG, "_________________prepare()");
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            return;
        }
        mMediaRecorder.start();
    }



    void replay() throws IllegalArgumentException, IllegalStateException, IOException {

        mTextView.setText(getString(R.string.transmitter_receiver_playing));
        // Replaying sound right now by record();
        stopButton.setEnabled(false);
        //add for pass button enalbed by songguangyu 20140505 start
        btnPass.setEnabled(true);
        //add for pass button enalbed by songguangyu 20140505 end
        FileInputStream mFileInputStream = new FileInputStream(file);

        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(mFileInputStream.getFD());
        mMediaPlayer.prepare();
        mMediaPlayer.start();
        mFileInputStream.close();
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            public void onCompletion(MediaPlayer mPlayer) {
                //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 start
                isComplete = true;
                //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 end
                mPlayer.stop();
                mPlayer.release();
                deleteRecordResource();
                recordButton.setClickable(true);

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
        mAudioManager.setParameters("second-mic=true");
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
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolume, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolume, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, callVolume, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF, dtmfVolume, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notificationVolume, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, ringVolume, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, systemVolume, 0);
    }

    void bindView() {

        recordButton = (Button) findViewById(R.id.transmitter_receiver_start);
        stopButton = (Button) findViewById(R.id.transmitter_receiver_stop);
        mTextView = (TextView) findViewById(R.id.transmitter_receiver_hint);
        mTextView.setText(getString(R.string.transmitter_receiver_to_record));
        stopButton.setEnabled(false);
        recordButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                mMediaRecorder = new MediaRecorder();
                if (!mAudioManager.isWiredHeadsetOn()) {

                    mTextView.setText(getString(R.string.transmitter_receiver_recording));
                    try {
                        setAudio();
                        recordButton.setClickable(false);
                        record();
                        isRecording = true;
                        stopButton.setEnabled(true);
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
                    Log.e(TAG, "音量为：" + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                    //add for click fail button crash when recoding by song 20140506 start
                    isRecording = false;
                    //add for click fail button crash when recoding by song 20140506 end
                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
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

    private void deleteRecordResource() {
        if (file == null) {
            return;
        }
        if (file.exists()) {
            file.delete();
        }
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
}
