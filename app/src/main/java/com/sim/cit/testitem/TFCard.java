package com.sim.cit.testitem;

import java.io.File;

import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import android.os.storage.StorageManager;
import android.os.Environment;
import android.os.StatFs;
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
import android.os.IBinder;
import android.os.Handler;

import com.android.internal.app.IMediaContainerService;
import com.readboy.util.DiskManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TFCard extends TestActivity {
    private static final String TAG = "CIT_TFCardTest";

    private TextView tvSD;
    private Button btnPass;
    private boolean sdIsPass;
    private boolean isTest = true;
    final int SUB_ID = 1;
    private StorageManager mStorageManager;
    private static String myStatus;
    BroadcastReceiver mReceiver;

    private String tfPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.two_txt;
        super.onCreate(savedInstanceState);
        mStorageManager = StorageManager.from(this);
        tvSD = (TextView) findViewById(R.id.txt_two);
        btnPass = super.btnPass;
        Intent service = new Intent().setComponent(DEFAULT_CONTAINER_COMPONENT);
        bindService(service, mDefContainerConn, Context.BIND_AUTO_CREATE);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    System.out.println("TFCard__Intent.ACTION_MEDIA_MOUNTED");
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    tfPath = "/storage/0865-180D";    //原来是："/storage/sdcard1"
                    tfPath = DiskManager.getSdStoragePath(TFCard.this);    //获取tf卡路径
                    Log.i(TAG, "----------------------tfPath = " + tfPath);
                    if (tfPath == null) {
                        tfPath = "";
                    }

                    File rootFile = new File(tfPath);
                    mTotalSize = rootFile.getTotalSpace();
                    mAvailSize = rootFile.getFreeSpace();
                    getSDStatus();
                    btnPass.setEnabled(sdIsPass);
                }
                if(intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)){
                    getSDStatus();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme(ContentResolver.SCHEME_FILE);
        registerReceiver(mReceiver, filter);

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
    @Override
    protected void onPause() {
        super.onPause();
        isTest = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // getSDStatus();

        btnPass.setEnabled(sdIsPass);
    }


    private void getSDStatus() {

        myStatus = mStorageManager.getVolumeState(tfPath);
        Log.i(TAG, "SD card status : " + myStatus);
        if (Environment.MEDIA_MOUNTED.equals(myStatus)) {
            sdIsPass = true;
            Log.i(TAG, "sdIsPass : " + sdIsPass);
            tvSD.setText(getString(R.string.sd_card_space) + ":" + Formatter.formatFileSize(this, mTotalSize) + "\n" + getString(R.string.sd_card_space1) + ":" + Formatter.formatFileSize(this, mAvailSize));
            Toast.makeText(this, "SD card status : " + myStatus, 2000).show();
        } else {
            sdIsPass = false;
            tvSD.setText("SD card:\tFail");
            Toast.makeText(this, "SD card status : " + myStatus, 2000).show();
        }
    }

    public static final ComponentName DEFAULT_CONTAINER_COMPONENT = new ComponentName(
            "com.android.defcontainer", "com.android.defcontainer.DefaultContainerService");

    private long mTotalSize;
    private long mAvailSize;

    private void measureSDStorage(IMediaContainerService imcs) {
        final File file = Environment.getExternalStorageDirectory();
//		Log.i(TAG, "---------------path = "+file.getAbsolutePath());	//手机内部存储

//		01-13 14:18:53.357: I/CIT_TFCardTest(3457): ---------------path = /storage/emulated/0
//				01-13 14:18:53.362: D/CIT_TFCardTest(3457): TotalSize------>3014639616
//				01-13 14:18:53.362: D/CIT_TFCardTest(3457): AvailSize------>2008444928
//				01-13 14:18:53.373: I/CIT_TFCardTest(3457): SD card status : unknown


        String path = tfPath;
        try {
            final long[] stats = imcs.getFileSystemStats(path);
            mTotalSize = stats[0];
            mAvailSize = stats[1];
            Log.d(TAG, "TotalSize------>" + mTotalSize);
            Log.d(TAG, "AvailSize------>" + mAvailSize);
        } catch (Exception e) {
            Log.w(TAG, "Problem in container service", e);
        }
    }

    private final ServiceConnection mDefContainerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final IMediaContainerService imcs = IMediaContainerService.Stub.asInterface(
                    service);
            tfPath = "/storage/0865-180D";    //原来是："/storage/sdcard1"
            tfPath = DiskManager.getSdStoragePath(TFCard.this);    //获取tf卡路径
            Log.i(TAG, "----------------------tfPath = " + tfPath);
            if (tfPath == null) {
                tfPath = "";
            }

            measureSDStorage(imcs);
            getSDStatus();
            btnPass.setEnabled(sdIsPass);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //do nothing
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mDefContainerConn);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

}



