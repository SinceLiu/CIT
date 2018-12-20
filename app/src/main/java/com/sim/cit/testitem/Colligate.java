package com.sim.cit.testitem;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import android.os.storage.StorageManager;
import android.os.Environment;
import android.os.StatFs;
import android.os.Vibrator;
//import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sim.cit.CITTestHelper;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
//add by liyunlong start
import android.os.IBinder;
import com.android.internal.app.IMediaContainerService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import java.io.BufferedReader;
//add by liyunlong end
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
//Modify for use vibrator api by lizhaobo 20151111 start
import android.os.Vibrator;
//Modify for use vibrator api by lizhaobo 20151111 end

public class Colligate extends TestActivity implements OnClickListener{
private static final String TAG = "CIT_ColligateTest";

    private TextView tvSim;
    private TextView tvSD;
    private Button btnPass;
    private Vibrator vibrator;
    private AudioManager am;
    private MediaPlayer mp = null;
    private int nCurrentMusicVolume;
    int nMaxMusicVolume;
    private boolean simIsPass;
    private boolean sim2IsPass;
    private boolean sdIsPass;
    private boolean isTest=true;
    final int SUB_ID=1;
    private StorageManager mStorageManager;
    private static String myStatus;
    private FileOutputStream write_vibrator = null;
    private static final String  VIBRATOR_NODE = "/sys/class/timed_output/vibrator/enable";
    final byte[] VIBRATOR_1S = {'1', '0', '0', '0'};
    private Timer mTimer = new Timer();
    private int vibrator_times = 0;
    //Modify for use vibrator api by lizhaobo 20151111 start
    private boolean hasVibrator = true;
    private Vibrator v;
    //Modify for use vibrator api by lizhaobo 20151111 end
    private Button noDizuo;  //隐藏无底座测试
    private CITTestHelper application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.two_txt;
        super.onCreate(savedInstanceState);
        mStorageManager = StorageManager.from(this);
        tvSim = (TextView)findViewById(R.id.txt_one);
        tvSD = (TextView)findViewById(R.id.txt_two);


        View labaView = findViewById(R.id.laba);
        labaView.setVisibility(View.VISIBLE);
        btnPass = super.btnPass;
        Intent service=new Intent().setComponent(DEFAULT_CONTAINER_COMPONENT);
    	bindService(service, mDefContainerConn, Context.BIND_AUTO_CREATE);
        try {
            //Modify for use vibrator api by lizhaobo 20151111 start
            if (hasVibrator) {
                v = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                v.vibrate(new long[]{100,1000,100,1000},0);
            } else {
                write_vibrator = new FileOutputStream(VIBRATOR_NODE);
            }
            //Modify for use vibrator api by lizhaobo 20151111 end
        } catch (IOException ioe) {
            android.util.Log.e("xsp_vibrator_test", "open Vibrator File Failed : " + ioe.toString());
        }
        //Modify for use vibrator api by lizhaobo 20151111 start
        //startVibration();
        //Modify for use vibrator api by lizhaobo 20151111 end
    }

    private void startSpeaker(int switchValues){
        Log.i(TAG, "speaker start");
		am = (AudioManager) getSystemService(AUDIO_SERVICE);
        nMaxMusicVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int state = switchValues == 0 ? switchValues : 1;
        am.setParameters("spk_out_status="+state);
  	    am.setSpeakerphoneOn(state == 1);
        nCurrentMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "speaker nCurrentMusicVolume " + nCurrentMusicVolume + " nMaxMusicVolume " + nMaxMusicVolume);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume, 0);
        Uri uri = Uri.parse(CITTestHelper.COLLIGATE_SOUND_PATH);
        /*20130122 modify for change sdcard path by lvhongshan start*/
        /*  String SDCardPath=null;
        if(SystemProperties.getInt("persist.sys.emmcsdcard.enabled", 0) == 0){   //extra storage
            SDCardPath = "/storage/sdcard0";
            //SDCardPath = Environment.getExternalStorageDirectory(); //sd path
            //String EmmcPath = Environment.getInternalStorageDirectory();   //emmc path
        }else{//internal storage
            SDCardPath = "/storage/sdcard1";
            //SDCardPath = Environment.getInternalStorageDirectory(); //内部存储时的sd卡路径
            //String EmmcPath = Environment.getExternalStorageDirectory();  //内部存储时的emmc路径
        }
        String sound_path = SDCardPath + "/test.mp3";
        Log.i(TAG,"SDCardPath is "+SDCardPath+" and sound_path is "+sound_path);
        //Uri uri = Uri.parse(CITTestHelper.COLLIGATE_SOUND_PATH);
        Uri uri = Uri.parse(sound_path);*/
        /*20130122 modify for change sdcard path by lvhongshan end*/
//        if (sdIsPass) {
//            mp = MediaPlayer.create(getApplicationContext(), uri);
//        } else {
        if(mp != null)
        {
        	mp.stop();
        	mp.release();
        	mp = null;
        }
            mp = MediaPlayer.create(getApplicationContext(), R.raw.test);
//        }
        if (mp != null) {
            mp.setLooping(true);
            mp.start();
            Log.i(TAG, "speaker start mp is not null end");
        }
    }

    private void startVibration(){
        mTimer.schedule(new TimerStartVibrator(), 1, 1000);
    }

    private void getSimStatus(){
        String strStatus = "";
        String strStatus2 = "";
        int nSimStatus;
        int nSim2Status;

//        MSimTelephonyManager mMSimTelephonyManager = (MSimTelephonyManager) getSystemService(Service.MSIM_TELEPHONY_SERVICE);
//        nSimStatus = mMSimTelephonyManager.getSimState(0);
//        nSim2Status = mMSimTelephonyManager.getSimState(1);


        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        nSimStatus = tm.getSimState(0);
        nSim2Status = tm.getSimState(1);

        switch (nSimStatus) {
        case TelephonyManager.SIM_STATE_UNKNOWN:
            strStatus = getString(R.string.sim_status_0);
            simIsPass = false;
            break;
        case TelephonyManager.SIM_STATE_ABSENT:
            strStatus = getString(R.string.sim_status_1);
            simIsPass = false;
            break;
        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
            strStatus = getString(R.string.sim_status_2);
            simIsPass = false;
            break;
        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
            strStatus = getString(R.string.sim_status_3);
            simIsPass = false;
            break;
        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            strStatus = getString(R.string.sim_status_4);
            simIsPass = false;
            break;
        case TelephonyManager.SIM_STATE_READY:
            strStatus = getString(R.string.sim_status_5);
            simIsPass = true;
            break;
        default:
            strStatus = getString(R.string.sim_status_0);
            simIsPass = false;
            break;
        }
        switch (nSim2Status) {
        case TelephonyManager.SIM_STATE_UNKNOWN:
            strStatus2 = getString(R.string.sim_status_0);
            sim2IsPass = false;
            break;
        case TelephonyManager.SIM_STATE_ABSENT:
            strStatus2 = getString(R.string.sim_status_1);
            sim2IsPass = false;
            break;
        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
            strStatus2 = getString(R.string.sim_status_2);
            sim2IsPass = false;
            break;
        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
            strStatus2 = getString(R.string.sim_status_3);
            sim2IsPass = false;
            break;
        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            strStatus2 = getString(R.string.sim_status_4);
            sim2IsPass = false;
            break;
        case TelephonyManager.SIM_STATE_READY:
            strStatus2 = getString(R.string.sim_status_5);
            sim2IsPass = true;
            break;
        default:
            strStatus2 = getString(R.string.sim_status_0);
            sim2IsPass = false;
            break;
        }
        Log.i(TAG, "SIM card status: " + strStatus);
        if (simIsPass && sim2IsPass) {
            simIsPass = true;
            tvSim.setText(getString(R.string.sim_status_6));
        }else{
            simIsPass = false;
            tvSim.setText("SIM1 :"+strStatus+"\n"+"SIM2 :"+strStatus2);
            Toast.makeText(this, "SIM card status: " + strStatus, 2000).show();
            Toast.makeText(this, "SIM card2 status: " + strStatus2, 2000).show();
        }
        /*if (simIsPass) {
            tvSim.setText("SIM card1 and SIM card2:\tPass");
        }else{
            simIsPass = false;
            tvSim.setText("SIM1 status:"+strStatus);
            Toast.makeText(this, "SIM card status: " + strStatus, 2000).show();
        }*/
    }
/*
    private void getSDStatus(){
        final String ExternalStoragePath = "/storage/sdcard1";
        String status = Environment.getExternalStorageState();
        // String status = Environment.getInternalStorageState();
        // String status = mStorageManager.getVolumeState(ExternalStoragePath);
        Log.i(TAG, "SD card status: " + status);
        if (Environment.MEDIA_MOUNTED.equals(status)) {
    //        File path = Environment.getExternalStorageDirectory();
    //        File path = "/storage/sdcard1";
     //       Log.i(TAG, "sdpath is " + path.getPath());
    //        StatFs stat = new StatFs(path.getPath());
            StatFs stat = new StatFs(ExternalStoragePath);
            Log.i("lvhongshan", "sdpath "+"/storage/sdcard1");
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            sdIsPass = true;
            tvSD.setText(getString(R.string.sd_card_space) + Formatter.formatFileSize(this, (totalBlocks * blockSize)));
        }else{
            sdIsPass = false;
            tvSD.setText("SD card:\tFail");
            Toast.makeText(this, "SD card status: " + status, 2000).show();
        }
    }
*/
    private void startLed(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 3; i++) {
                    if (!isTest) {
                        break;
                    }
                    CommonDrive.lightControl(i);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(i == 2){
                        i = -1;
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvSD.setText(getString(R.string.colligate_illustration));

//        getSimStatus();
        // getSDStatus();
//        startSpeaker(0);

//        if (CITTestHelper.HAS_LED) {
//            startLed();
//    }
        //Log.i(TAG, "sdIsPass : " + sdIsPass +"simIsPass"+simIsPass);
//        btnPass.setEnabled(simIsPass);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isTest=false;
//        CommonDrive.lightControl(-1);
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        if (am != null) {
            nCurrentMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC); //获取最新音量（滑动调节？)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, nCurrentMusicVolume, 0);
        }
        if(am.isSpeakerphoneOn()) {    //Q9
		 	am.setParameters("spk_out_status="+ 0);
            am.setSpeakerphoneOn(false);
        }
    }

//add by liyunlong start
    private void getSDStatus(){

        myStatus = mStorageManager.getVolumeState("/storage/sdcard1");
    	Log.i(TAG, "SD card status : "+myStatus);
        if(Environment.MEDIA_MOUNTED.equals(myStatus)){
            sdIsPass=true;
            Log.i(TAG, "sdIsPass : " + sdIsPass);
            //tvSD.setText(getString(R.string.sd_card_space)+":" +Formatter.formatFileSize(this, mTotalSize)+"\n"+getString(R.string.sd_card_space1)+":"+Formatter.formatFileSize(this, mAvailSize));
            //Toast.makeText(this, "SD card status : "+myStatus, 2000).show();
        }else{
            sdIsPass=false;
            //tvSD.setText("SD card:\tFail");
            //Toast.makeText(this, "SD card status : "+myStatus, 2000).show();
        }
    }
 
    public static final ComponentName DEFAULT_CONTAINER_COMPONENT = new ComponentName(
             "com.android.defcontainer", "com.android.defcontainer.DefaultContainerService");

    private long mTotalSize;
    private long mAvailSize;

    private void measureSDStorage(IMediaContainerService imcs){
        //final File file=Environment.getExternalStorageDirectory();
        String path="/storage/sdcard1";
        try {
             final long[] stats = imcs.getFileSystemStats(path);
             mTotalSize = stats[0];
             mAvailSize = stats[1];
             Log.d(TAG, "TotalSize------>"+mTotalSize);
             Log.d(TAG, "AvailSize------>"+mAvailSize);
         } catch (Exception e) {
             Log.w(TAG, "Problem in container service", e);
         }
    }

    private final ServiceConnection mDefContainerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final IMediaContainerService imcs = IMediaContainerService.Stub.asInterface(
                    service);
            measureSDStorage(imcs);
            getSDStatus();
            //btnPass.setEnabled(sdIsPass && simIsPass);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //do nothing
        }
    };
    //add by liyunlong end

    class TimerStartVibrator  extends TimerTask {
        public void run() {
            if ((vibrator_times % 2) == 0 && write_vibrator != null) {
                //Modify for use vibrator api by lizhaobo 20151111 start
                /*try {
                    write_vibrator.write(VIBRATOR_1S);
                } catch (IOException ioe_w) {
                    android.util.Log.e("xsp_vibrator_test",
                         "IOException when write vibrator_file : " + ioe_w.toString());
                }*/
                //Modify for use vibrator api by lizhaobo 20151111 end
            }
            vibrator_times++;
        }
    }

    @Override
    protected void onDestroy() {
        mTimer.cancel();
        try {
            //Modify for use vibrator api by lizhaobo 20151111 start
            if (hasVibrator) {
                v.cancel();
            }
            //Modify for use vibrator api by lizhaobo 20151111 end
            if (write_vibrator != null)
                write_vibrator.close();
        } catch (IOException ioe_f) {
            android.util.Log.e("xsp_vibrator_test", "IOException when close vibrator_file : " + ioe_f.toString());
        }
        super.onDestroy();
        unbindService(mDefContainerConn);
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
	
	 @Override
    	public void onClick(View v) {
    		// TODO Auto-generated method stub
    		switch (v.getId()) {
				case R.id.jishen:
                    if(mp != null)
                    {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
					startSpeaker(0);
					break;
	
				case R.id.dizuo:
                    if(mp != null)
                    {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
					if(get_ext_speaker_type() == 1){
						startSpeaker(1);
					}else{
						//toast no
						Toast.makeText(Colligate.this, "请先插入底座", Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.nodizuo:
                    if(mp != null)
                    {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
					startSpeaker(1);
					break;
			}
    	}
    
    /**
     * 0机身，1底座
     * @return
     */
    private int get_ext_speaker_type()
	{
		int gpio_value = -1;
		String str1 = "/proc/readboy/speaker_type";
		String str2 = null;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 300);
			str2 = localBufferedReader.readLine();
			if(null != str2) {
				gpio_value = Integer.parseInt(str2);
			}
			localBufferedReader.close();
			localFileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
		Log.d(TAG, "read = " + str2 + ",gpio_value = " + gpio_value);
		
		return gpio_value;
	}




}
