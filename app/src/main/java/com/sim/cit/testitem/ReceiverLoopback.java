package com.sim.cit.testitem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.sim.cit.R;
import com.sim.cit.TestActivity;
//import com.qualcomm.factory.Utilities;

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
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import java.util.LinkedList;

public class ReceiverLoopback extends TestActivity {
	String mAudiofilePath;
	static String TAG = "TransmitterReceiver";
	MediaRecorder mMediaRecorder = new MediaRecorder();
	boolean isRecording = false;
	Button recordButton = null;
	Button stopButton = null;
	AudioManager mAudioManager;
	Context mContext;
	private boolean isExit = false;
	// Modify for stop the playing when exit test before compeletion by
	// xiasiping 20140625 start
	MediaPlayer mMediaPlayer;
	private boolean isComplete = false;
	// Modify for stop the playing when exit test before compeletion by
	// xiasiping 20140625 end

	/*
	 * public void onCreate(Bundle savedInstanceState) {
	 * 
	 * layoutId=R.layout.transmitter_receiver;
	 * super.onCreate(savedInstanceState); //
	 * setContentView(R.layout.transmitter_receiver);
	 * 
	 * mContext = this; isRecording = false;
	 * 
	 * getService(); bindView(); //Modify for stop the playing when exit test
	 * before compeletion by xiasiping 20140625 start mMediaPlayer = new
	 * MediaPlayer(); //Modify for stop the playing when exit test before
	 * compeletion by xiasiping 20140625 end
	 * 
	 * if (mAudioManager.isWiredHeadsetOn())
	 * showWarningDialog(getString(R.string.remove_headset));
	 * 
	 * setAudio(); btnPass.setEnabled(false);
	 * 
	 * }
	 * 
	 * @Override public void finish() { isExit = true; super.finish(); //add for
	 * click fail button crash when recoding by song 20140506 start if
	 * (isRecording) { mMediaRecorder.stop(); mMediaRecorder.release(); } //add
	 * for click fail button crash when recoding by song 20140506 end //Modify
	 * for stop the playing when exit test before compeletion by xiasiping
	 * 20140625 start if (!isComplete && mMediaPlayer != null) { try {
	 * mMediaPlayer.stop(); mMediaPlayer.release(); File file = new
	 * File(mAudiofilePath); file.delete(); }catch (Exception e) { loge(e); } }
	 */
	// Modify for stop the playing when exit test before compeletion by
	// xiasiping 20140625 end
	/*
	 * 20130111 added for change the audioMode to fix the bug 22094 by
	 * lvhongshan start
	 */
	/*
	 * if(!mAudioManager.isSpeakerphoneOn()) {
	 * mAudioManager.setSpeakerphoneOn(true); }
	 */
	/*
	 * 20130111 added for change the audioMode to fix the bug 22094 by
	 * lvhongshan end
	 */
	/*
	 * mAudioManager.setMode(AudioManager.MODE_NORMAL); }
	 */

	/*
	 * void record() throws IllegalStateException, IOException,
	 * InterruptedException {
	 * 
	 * mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
	 * //Modify recorder to single channel by xiasiping 20150204 start
	 * mMediaRecorder.setAudioChannels(1); //Modify recorder to single channel
	 * by xiasiping 20150204 end
	 * mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	 * mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
	 * mMediaRecorder.setOutputFile(this.getCacheDir().getAbsolutePath() +
	 * "/test.aac"); mAudiofilePath = this.getCacheDir().getAbsolutePath() +
	 * "/test.aac"; mMediaRecorder.prepare(); mMediaRecorder.start(); }
	 * 
	 * void replay() throws IllegalArgumentException, IllegalStateException,
	 * IOException { final TextView mTextView = (TextView)
	 * findViewById(R.id.transmitter_receiver_hint);
	 * mTextView.setText(getString(R.string.transmitter_receiver_playing)); //
	 * Replaying sound right now by record(); stopButton.setClickable(false);
	 * //add for pass button enalbed by songguangyu 20140505 start
	 * btnPass.setEnabled(true); //add for pass button enalbed by songguangyu
	 * 20140505 end File file = new File(mAudiofilePath); FileInputStream
	 * mFileInputStream = new FileInputStream(file);
	 * 
	 * mMediaPlayer.reset();
	 * mMediaPlayer.setDataSource(mFileInputStream.getFD());
	 * mMediaPlayer.prepare(); mMediaPlayer.start();
	 * 
	 * mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
	 * 
	 * public void onCompletion(MediaPlayer mPlayer) { //Modify for stop the
	 * playing when exit test before compeletion by xiasiping 20140625 start
	 * isComplete = true; //Modify for stop the playing when exit test before
	 * compeletion by xiasiping 20140625 end mPlayer.stop(); mPlayer.release();
	 * mAudioManager.setMode(AudioManager.MODE_NORMAL); File file = new
	 * File(mAudiofilePath); file.delete();
	 * 
	 * final TextView mTextView = (TextView)
	 * findViewById(R.id.transmitter_receiver_hint);
	 * mTextView.setText(getString(R.string.transmitter_receiver_replay_end));
	 * // showConfirmDialog(); if (!isExit) { //
	 * showWarningDialog(getString(R.string.record_finish)); } //modify for pass
	 * button enalbed by songguangyu 20140505 start //btnPass.setEnabled(true);
	 * //modify for pass button enalbed by songguangyu 20140505 end } });
	 * 
	 * }
	 */

	void showWarningDialog(String title) {

		new AlertDialog.Builder(mContext).setTitle(title)
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (mAudioManager.isWiredHeadsetOn()) {
							showWarningDialog(getString(R.string.remove_headset));
						} else {
							startThread();
						}
					}
				}).show();

	}

	void showConfirmDialog() {

		new AlertDialog.Builder(mContext).setTitle(getString(R.string.transmitter_receiver_confirm))
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						// pass();
					}
				}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						// fail(null);
					}
				}).show();
	}

	public void setAudio() {

		/* 20121214 added for change the audioMode by lvhongshan start */
		
		if (mAudioManager.isSpeakerphoneOn()) {
			mAudioManager.setSpeakerphoneOn(false);
		}
		 
		/* 20121214 added for change the audioMode by lvhongshan end */
		mAudioManager.setMode(AudioManager.MODE_IN_CALL);

		mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
		mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0);
	}

	/*
	 * void bindView() {
	 * 
	 * recordButton = (Button) findViewById(R.id.transmitter_receiver_start);
	 * stopButton = (Button) findViewById(R.id.transmitter_receiver_stop); final
	 * TextView mTextView = (TextView)
	 * findViewById(R.id.transmitter_receiver_hint);
	 * mTextView.setText(getString(R.string.transmitter_receiver_to_record));
	 * stopButton.setEnabled(false); recordButton.setOnClickListener(new
	 * OnClickListener() {
	 * 
	 * public void onClick(View v) {
	 * 
	 * if (!mAudioManager.isWiredHeadsetOn()) {
	 * 
	 * mTextView.setText(getString(R.string.transmitter_receiver_recording));
	 * try { recordButton.setClickable(false); record();
	 * stopButton.setEnabled(true); isRecording = true;
	 * 
	 * } catch (Exception e) { loge(e); } } else
	 * showWarningDialog(getString(R.string.remove_headset));
	 * 
	 * } });
	 * 
	 * stopButton.setOnClickListener(new OnClickListener() {
	 * 
	 * public void onClick(View v) {
	 * 
	 * if (isRecording) { //add for click fail button crash when recoding by
	 * song 20140506 start isRecording = false; //add for click fail button
	 * crash when recoding by song 20140506 end mMediaRecorder.stop();
	 * mMediaRecorder.release();
	 * 
	 * try { mAudioManager.setMode(AudioManager.MODE_IN_CALL); replay(); } catch
	 * (Exception e) { loge(e); } } else
	 * showWarningDialog(getString(R.string.transmitter_receiver_record_first));
	 * } }); }
	 */

	void getService() {

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	}

	void fail(Object msg) {

		loge(msg);
		toast(msg);
		setResult(RESULT_CANCELED);
		// Utilities.writeCurMessage(this, TAG,"Failed");
		finish();
	}

	void pass() {

		setResult(RESULT_OK);
		// Utilities.writeCurMessage(this, TAG,"Pass");
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

	private boolean firstRecord = false;
	// Modified playing while recording requirement by liuyue 20150427 start
	protected int mInputBufferSize;
	private AudioRecord mAudioRecord;
	private byte[] mInputBufferBytes;
	private LinkedList<byte[]> mInputBytesList;
	// AudioTrack
	private int mOutputBufferSize;
	private AudioTrack mAudioTrack;
	private byte[] mOutputBufferBytes;
	private Thread mRecord;
	private Thread mPlay;
	private boolean flag = true;// 让线程停止的标志

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutId = R.layout.transmitter_receiver;
		super.onCreate(savedInstanceState);
		mContext = this;
		getService();
		setAudio();
		firstRecord = true;
		init();
		if (mAudioManager != null) {
			Log.i(TAG, "onCreate() mAudioManager.getMode() " + mAudioManager.getMode());
			mAudioManager.setParameters("avoid-Echoes=true");
			mAudioManager.setMode(AudioManager.MODE_IN_CALL);
			if (mAudioManager.isWiredHeadsetOn()) {
				showWarningDialog(getString(R.string.remove_headset));
			} else {
				Log.i(TAG, "onCreate() audiomanager is not null startthread() mode is " + mAudioManager.getMode());
				startThread();
			}
		}
	}

	private void startThread() {
		try {
			mRecord = new Thread(new recordSound());
			mPlay = new Thread(new playRecord());
			mRecord.start();
			mPlay.start();
		} catch (Exception e) {
			Log.e(TAG, "startThread catch exception");
			loge(e);
		}
	}

	private void init() {
		recordButton = (Button) findViewById(R.id.transmitter_receiver_start);
		stopButton = (Button) findViewById(R.id.transmitter_receiver_stop);
		stopButton.setVisibility(View.GONE);
		recordButton.setVisibility(View.GONE);
		final TextView mTextView = (TextView) findViewById(R.id.transmitter_receiver_hint);
		mTextView.setText(getString(R.string.transmitter_receiver_recording2));
		/*
		 * recordButton.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { if (!mAudioManager.isWiredHeadsetOn()) {
		 * mTextView.setText(getString(R.string.transmitter_receiver_recording))
		 * ; btnPass.setEnabled(true); try { mRecord = new Thread(new
		 * recordSound()); mPlay = new Thread(new playRecord());
		 * mRecord.start(); mPlay.start(); } catch (Exception e) { loge(e); } }
		 * else showWarningDialog(getString(R.string.remove_headset)); } });
		 */
		// Init AudioRecord
		mInputBufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, mInputBufferSize);
		mInputBufferBytes = new byte[mInputBufferSize];
		mInputBytesList = new LinkedList<byte[]>();
		// Init AudioTrack
		mOutputBufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, mOutputBufferSize, AudioTrack.MODE_STREAM);
		mOutputBufferBytes = new byte[mOutputBufferSize];
	}

	class recordSound implements Runnable {
		@Override
		public void run() {
			byte[] bytes_pkg;
			try {
				Log.i(TAG, "mAudioRecord.startRecording()");
				mAudioRecord.startRecording();
				while (flag) {
					if (firstRecord) {
						// mAudioRecord.read(mInputBufferBytes,
						// mInputBufferSize, mInputBufferSize) ;
						// Do nothing when first time. add by zhangliying
						firstRecord = false;
					} else {
						Log.i(TAG, "mAudioRecord.startRecording() read");
						mAudioRecord.read(mInputBufferBytes, 0, mInputBufferSize);
					}
					bytes_pkg = mInputBufferBytes.clone();
					if (mInputBytesList.size() > 1) {
						mInputBytesList.removeFirst();
					}
					mInputBytesList.add(bytes_pkg);
				}
			} catch (Exception e) {
				Log.e(TAG, "mAudioRecord catch exception");
				loge(e);
			}
		}
	}

	class playRecord implements Runnable {
		@Override
		public void run() {
			byte[] bytes_pkg = null;
			mAudioTrack.play();
			while (flag) {
				try {
					Log.e(TAG, "playRecord write()");
					mOutputBufferBytes = mInputBytesList.getFirst();
					bytes_pkg = mOutputBufferBytes.clone();
					mAudioTrack.write(bytes_pkg, 0, bytes_pkg.length);
				} catch (Exception e) {
					Log.e(TAG, "playRecord catch exception");
					loge(e);
				}
			}
		}
	}

	@Override
	public void finish() {
		flag = false;
		Log.i(TAG, "finish()");
		if (mAudioRecord != null) {
			mAudioRecord.stop();
			mAudioRecord.release();
			mAudioRecord = null;
		}
		if (mAudioTrack != null) {
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}
		mAudioManager.setMode(AudioManager.MODE_NORMAL);
		mAudioManager.setParameters("avoid-Echoes=false");
		Log.i(TAG, "finish() mode is " + mAudioManager.getMode());
		super.finish();
	}
	// Modified playing while recording requirement by liuyue 20150427 end
}
