package com.sim.cit.testitem;

import java.io.File;
import java.util.ArrayList;

import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
//modify for add reTest btn in UDisk test by xiasiping 20141103 start
import android.view.View;
import android.view.View.OnClickListener;
//modify for add reTest btn in UDisk test by xiasiping 20141103 end
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.readboy.util.DiskManager;
import com.sim.cit.CITTestHelper;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import com.sim.udisktest.UDiskTest;

import android.os.storage.StorageManager;
import android.content.Intent;


public class UDisk extends TestActivity {
    ///udisk   or  /storage/udisk
    private static final String TAG = "CIT_UDisk";

    private TextView tvUDisk;
    private Button btnPass;
    //modify for add reTest btn in UDisk test by xiasiping 20141103 start
    private Button reTest;
    //modify for add reTest btn in UDisk test by xiasiping 20141103 end
    private AudioManager am;
    private MediaPlayer mp = null;
    private int nCurrentMusicVolume;
    private boolean sdIsPass;
    private boolean isTest=true;
    private StorageManager mStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt_one_btn;
        super.onCreate(savedInstanceState);
        mStorageManager = StorageManager.from(this);
        tvUDisk = (TextView)findViewById(R.id.txt_one);
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
        //modify for add reTest btn in UDisk test by xiasiping 20141103 start
        reTest = (Button)findViewById(R.id.btn_one);
        reTest.setText("Test USB");
        reTest.setOnClickListener(new OnClickListener(){
            public void onClick (View v) {
                reTest.setEnabled(false);
                if (v.getId() == R.id.btn_one) {
                    startReadFile();
                }
            }
        });
        reTest.setEnabled(false);
        
        //modify for add reTest btn in UDisk test by xiasiping 20141103 end
        //tvUDisk.setText(getString(R.string.udisk_hint));
        //startSpeaker();
        startReadFile();
    }

    private void startReadFile(){
//    	ArrayList<String> allPathList = DiskManager.getAllStoragePath(this);
//		int size = allPathList.size();
//		if(size < 3){
//			Toast.makeText(getApplicationContext(), R.string.insertSD, Toast.LENGTH_LONG).show();
//			reTest.setEnabled(true);
//		}else {
//			ArrayList<String> list = DiskManager.initDevicePath(this);
//	    	for (int i = 0; i < list.size(); i++) {
//				Log.i(TAG, "-------------list = "+list.get(i));
//			}
	        Intent intent = new Intent();
	        intent.setClass(getApplicationContext(), UDiskTest.class);
	        startActivityForResult(intent,1);
//		}
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        Log.d("UDiskTest","onActivityResult : " + resultCode);
        if(resultCode == RESULT_OK) {
            String strResult = (String)data.getExtra("readfile");
            tvUDisk.setText(strResult);
            btnPass.setEnabled(true);
            Toast.makeText(this, "UDisk Test Successfully ", 2000).show();
        } else {
            //Modify for add prompt of check USB by xiasiping 20141124 start
            tvUDisk.setText(R.string.udisk_failed);
            //Modify for add prompt of check USB by xiasiping 20141124 end
            btnPass.setEnabled(false);
            try {
                String strResult = (String)data.getExtra("readfile");
                Toast.makeText(this, strResult, 2000).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //modify for add reTest btn in UDisk test by xiasiping 20141103 start
        reTest.setEnabled(true);
        //modify for add reTest btn in UDisk test by xiasiping 20141103 end
    }

    private void startSpeaker(){
        Log.i(TAG, "speaker start");
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMaxMusicVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        nCurrentMusicVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        am.setStreamVolume(AudioManager.STREAM_MUSIC, nMaxMusicVolume, 0);
        String uDiskPath = "/storage/usbotg";

        String sound_path = uDiskPath + "/test.mp3";
        Log.i(TAG,"UDiskPath is "+uDiskPath+" and sound_path is "+sound_path);
        File f = new File(sound_path);
        //Uri uri = Uri.parse(CITTestHelper.COLLIGATE_SOUND_PATH);
        Uri uri = Uri.parse(sound_path);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType( Uri.fromFile(f), "audio/x-mpeg");
        startActivity(intent);

//        if (sdIsPass) {
 //           mp = new MediaPlayer();
//            try{
 //               mp.setDataSource("/storage/udisk/test.mp3");
//                mp.prepare();
 //           }catch (IOException e) {
//                        ;
//            }
//            mp = MediaPlayer.create(getApplicationContext(), uri);
//            } else {
//                  mp = MediaPlayer.create(getApplicationContext(), R.raw.test);
//            }
/*            if (mp != null) {
                  Log.i(TAG,"mp != null");
                  mp.setLooping(true);
                  mp.start();
            }*/
      }

    private void getUDiskStatus(){
/*        final String UDiskPath = "/storage/udisk";
        String status = mStorageManager.getVolumeState(UDiskPath);
            Log.i(TAG, "UDisk status: " + status);
            if (Environment.MEDIA_MOUNTED.equals(status)) {
            StatFs stat = new StatFs(UDiskPath);
                Log.i(TAG, "UDiskPath "+UDiskPath);
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                sdIsPass = true;
                tvUDisk.setText(getString(R.string.sd_card_space) + Formatter.formatFileSize(this, (totalBlocks * blockSize)));
            }else{
                sdIsPass = false;
                tvUDisk.setText("UDisk:\tFail");
                Toast.makeText(this, "UDisk status: " + status, 2000).show();
            }*/
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
        //getUDiskStatus();
        //startSpeaker();
        Log.i(TAG,"onResume");
       //    sdIsPass = true;
      // btnPass.setEnabled(sdIsPass);
    }

}
