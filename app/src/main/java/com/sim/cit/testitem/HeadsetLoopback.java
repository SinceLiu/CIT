package com.sim.cit.testitem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

//import com.qualcomm.factory.Utilities;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import java.util.LinkedList;
public class HeadsetLoopback extends TestActivity{

    String mAudiofilePath;
    static String TAG = "Headset";
    MediaRecorder mMediaRecorder = new MediaRecorder();
    boolean isRecording = false;
    Button recordButton = null;
    Button stopButton = null;
    AudioManager mAudioManager;
    Context mContext;
    Dialog warningDialog;
    private boolean isExit = false;
    //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 start
    MediaPlayer mMediaPlayer;
    private boolean isComplete = false;
    static boolean mIsPause = false;
    //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 end
    /*public void onCreate(Bundle savedInstanceState) {

    	layoutId=R.layout.headset;
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.headset);

        mContext = this;
        isRecording = false;

        getService();
        bindView();
        //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 start
        mMediaPlayer = new MediaPlayer();
        //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 end
        if (!mAudioManager.isWiredHeadsetOn())
            showWarningDialog(getString(R.string.insert_headset));

        setAudio();
        btnPass.setEnabled(false);

    }

    @Override
    public void finish() {
        isExit = true;
        super.finish();
        //add for click fail button crash when recoding by song 20140506 start
        if (isRecording) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
        }

        //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 start
        if (!isComplete && mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                File file = new File(mAudiofilePath);
                file.delete();
            }catch (Exception e) {
                loge(e);
            }
        }*/
        //Modify for stop the playing when exit test before compeletion by xiasiping 20140625 end


        //add for click fail button crash when recoding by song 20140506 end
   //     warningDialog.dismiss();
        /*20130111 added for change the audioMode to fix the bug 22094 by lvhongshan start*/
/*	if(!mAudioManager.isSpeakerphoneOn()) {
		mAudioManager.setSpeakerphoneOn(true);
	}*/
        /*20130111 added for change the audioMode to fix the bug 22094 by lvhongshan end*/
        /*mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    void record() throws IllegalStateException, IOException, InterruptedException {

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOutputFile(this.getCacheDir().getAbsolutePath() + "/test.aac");
        mAudiofilePath = this.getCacheDir().getAbsolutePath() + "/test.aac";
        mMediaRecorder.prepare();
        mMediaRecorder.start();
    }

    void replay() throws IllegalArgumentException, IllegalStateException, IOException {
        final TextView mTextView = (TextView) findViewById(R.id.headset_hint);
        mTextView.setText(getString(R.string.headset_playing));
        // Replaying sound right now by record();
        stopButton.setClickable(false);
        //add for pass button enalbed by songguangyu 20140506 start
        btnPass.setEnabled(true);
        //add for pass button enalbed by songguangyu 20140506 end
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
                
                final TextView mTextView = (TextView) findViewById(R.id.headset_hint);
                mTextView.setText(getString(R.string.headset_replay_end));
              //  showConfirmDialog();
                if (!isExit) {
              //      showWarningDialog(getString(R.string.record_finish));
                }
                //modify for pass button enalbed by songguangyu 20140506 start
                //btnPass.setEnabled(true);
                //modify for pass button enalbed by songguangyu 20140506 end
            }
        });

    }*/

    void showWarningDialog(String title) {

//     warningDialog = new AlertDialog.Builder(mContext).setTitle(title).setPositiveButton(getString(R.string.ok),
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {

//                    }
//                }).create();
//      warningDialog.show();
        new AlertDialog.Builder(mContext).setTitle(title).setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (!mAudioManager.isWiredHeadsetOn()){
                            showWarningDialog(getString(R.string.insert_headset));
                        }else{
                            //Modify for 501 headset not sound by lizhaobo 20151104 start
                            init();
                            //Modify for 501 headset not sound by lizhaobo 20151104 end
                            setAudio();
                            startThread();
                        }
                    }
                }).setCancelable(false).show();
    }

//20121209 modified for autotest and do not display the Dialog by lvhongshan
    void showConfirmDialog() {

        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.headset_confirm)).setPositiveButton(
                        getString(R.string.yes), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                              //  pass();
                            }
                        }).setNegativeButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                             //   fail(null);
                            }
                        }).show();
    }

    public void setAudio() {
    	
    	/*20121214 added for change the audioMode by lvhongshan start*/
	/*	if(mAudioManager.isSpeakerphoneOn()) {
			mAudioManager.setSpeakerphoneOn(false);
		}*/
	/*20121214 added for change the audioMode by lvhongshan end*/
        /*20130122 modify for change the audioMode and audioVolume by lvhongshan start*/
/*        mAudioManager.setMode(AudioManager.MODE_IN_CALL);

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
                .getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0);*/
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        float ratio = 0.8f;

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
        /*20130122 modify for change the audioMode and audioVolume by lvhongshan end*/
    }

    /*void bindView() {

        recordButton = (Button) findViewById(R.id.headset_record);
        stopButton = (Button) findViewById(R.id.headset_stop);
        final TextView mTextView = (TextView) findViewById(R.id.headset_hint);
        mTextView.setText(getString(R.string.headset_to_record));

        recordButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (mAudioManager.isWiredHeadsetOn()) {

                    mTextView.setText(getString(R.string.headset_recording));
                    try {
                        recordButton.setClickable(false);
                        record();
                        isRecording = true;

                    } catch (Exception e) {
                        loge(e);
                    }
                } else
                    showWarningDialog(getString(R.string.insert_headset));

            }
        });

        stopButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (isRecording) {
                    //add for click fail button crash when recoding by song 20140506 start
                    isRecording = false;
                    //add for click fail button crash when recoding by song 20140506 end
                    mMediaRecorder.stop();
                    mMediaRecorder.release();

                    try {
                        replay();
                    } catch (Exception e) {
                        loge(e);
                    }
                } else
                    showWarningDialog(getString(R.string.headset_record_first));
            }
        });
    }*/

    void getService() {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    void fail(Object msg) {

        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
   //     Utilities.writeCurMessage(this, TAG,"Failed");
        finish();
    }

    void pass() {

        setResult(RESULT_OK);
   //     Utilities.writeCurMessage(this, TAG,"Pass");
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
        //Modified playing while recording requirement by liuyue 20150427 start
        protected int mInputBufferSize;
        private AudioRecord mAudioRecord;
        private byte[] mInputBufferBytes;
        private LinkedList<byte[]>  mInputBytesList;
        //AudioTrack
        private int mOutputBufferSize;
        private AudioTrack mAudioTrack;
        private byte[] mOutputBufferBytes;
        private Thread mRecord;
        private Thread mPlay;
        private boolean flag = true;//让线程停止的标志
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                layoutId=R.layout.transmitter_receiver;
                super.onCreate(savedInstanceState);
                mContext = this;
                getService();
                //Modify for 501 headset not sound by lizhaobo 20151104 start
                if(mAudioManager != null){
                    Log.i(TAG,"onCreate:: mAudioManager.getMode() "+mAudioManager.getMode());
                    if (!mAudioManager.isWiredHeadsetOn()){
                        showWarningDialog(getString(R.string.insert_headset));
                    }else{
                        Log.i(TAG,"onCreate::start thread ");
                        init();
                        setAudio();
                        startThread();
                    }
                //Modify for 501 headset not sound by lizhaobo 20151104 end
                }
        }
        private void startThread(){
            try {
                    mRecord =  new Thread(new recordSound());
                    mPlay =  new Thread(new playRecord());
                    mRecord.start();
                    mPlay.start();
            } catch (Exception e) {
                    Log.e(TAG,"start thread catch exception");
                    loge(e);
            }
        }
        private void init(){
            recordButton = (Button) findViewById(R.id.transmitter_receiver_start);
            stopButton = (Button) findViewById(R.id.transmitter_receiver_stop);
            stopButton.setVisibility(View.GONE);
            recordButton.setVisibility(View.GONE);
            final TextView mTextView = (TextView) findViewById(R.id.transmitter_receiver_hint);
            mTextView.setText(getString(R.string.transmitter_receiver_recording3));
            /*recordButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (mAudioManager.isWiredHeadsetOn()) {
                    mTextView.setText(getString(R.string.transmitter_receiver_recording));
                    btnPass.setEnabled(true);
                    try {
                        mRecord =  new Thread(new recordSound());
                        mPlay =  new Thread(new playRecord());
                        mRecord.start();
                        mPlay.start();
                    } catch (Exception e) {
                        loge(e);
                    }
                } else
                    showWarningDialog(getString(R.string.insert_headset));
                }
            });*/
            //Init AudioRecord
            mInputBufferSize =AudioRecord.getMinBufferSize(8000,
            AudioFormat.CHANNEL_CONFIGURATION_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                           8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                           AudioFormat.ENCODING_PCM_16BIT,mInputBufferSize);
            mInputBufferBytes = new byte[mInputBufferSize] ;
            mInputBytesList=new LinkedList<byte[]>();
            //Init AudioTrack
            mOutputBufferSize = AudioTrack.getMinBufferSize(8000,
            AudioFormat.CHANNEL_CONFIGURATION_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                           AudioFormat.CHANNEL_CONFIGURATION_MONO,
                           AudioFormat.ENCODING_PCM_16BIT,
                           mOutputBufferSize,
                           AudioTrack.MODE_STREAM);
            mOutputBufferBytes=new byte[mOutputBufferSize];
        }
        class recordSound implements Runnable{
                @Override
                public void run() {
                     byte [] bytes_pkg;
                     try{
                         Log.e(TAG,"recordSound start");
                         mAudioRecord.startRecording();
                         while(flag){
//                                 Log.e(TAG,"recordSound read() mIsPause = " + mIsPause);
                                 if(mAudioManager!= null && mAudioManager.isWiredHeadsetOn() && !mIsPause)
                                 {
	                                 mAudioRecord.read(mInputBufferBytes, 0, mInputBufferSize) ;
	                                 bytes_pkg = mInputBufferBytes.clone() ;
	                                 if(mInputBytesList.size() > 1){
	                                    mInputBytesList.removeFirst();
	                                 }
	                                 mInputBytesList.add(bytes_pkg) ;
                                 }
                         }
                     }catch(Exception e){
                         Log.e(TAG,"recordSound catch exception");
                         loge(e);
                     }
                }
         }
        class playRecord implements Runnable{
                @Override
                public void run() {
                    byte [] bytes_pkg = null ;
                    mAudioTrack.play() ;
                    while(flag) {
                         try{
                        	 if(mAudioManager!= null && mAudioManager.isWiredHeadsetOn() && !mIsPause)
                        	 {
//	                             Log.e(TAG,"playrecord write mIsPause = " + mIsPause);
	                             mOutputBufferBytes = mInputBytesList.getFirst();
	                             bytes_pkg = mOutputBufferBytes.clone() ;
	                             mAudioTrack.write(bytes_pkg, 0, bytes_pkg.length);
                        	 }
                         }catch(Exception e){
                             Log.e(TAG,"playrecord catch exception");
                             loge(e);
                         }
                    }
                }
        }
       @Override
       public void finish() {
          flag = false;
          super.finish();
          mAudioManager.setMode(AudioManager.MODE_NORMAL);
          Log.i(TAG,"finsh()");
          //Set normal mic
          mAudioManager.setParameters("second-mic=false");
          if(mAudioRecord != null){
              mAudioRecord.stop();
              mAudioRecord.release();
              mAudioRecord = null;
          }
          if(mAudioTrack != null){
              mAudioTrack.stop();
              mAudioTrack.release();
              mAudioTrack = null;
          }
      }
       
       @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	mIsPause = false;
    }
       
       @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	mIsPause = true;
    }
      //Modified playing while recording requirement by liuyue 20150427 end
}
