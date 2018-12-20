package com.readboy.util;

import java.io.File;

import com.sim.cit.CITTestHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.PowerManager;
import android.util.Log;

public class TFBroadCastReceiver extends BroadcastReceiver {
    static final String LOCALDATA = "localdata";
    static final String BOOTCOMPELETED = "bootcompeleted";
    static final String TAG = "TFBroadCastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        SharedPreferences sp = context.getSharedPreferences(LOCALDATA, Context.MODE_PRIVATE);
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            try {
                Log.v(TAG, "TFBroadCastReceiver1__action = " + action);
                String tfPath = DiskManager.getSdStoragePath(context);
                String citPath = tfPath + "/readboycittest.cit";
                File citFile = new File(citPath);

                if (citFile.exists()) {
                    wakeUp(context);
                    CITTestHelper citTestHelper = (CITTestHelper) context.getApplicationContext();
                    Log.e(TAG, "application: " + citTestHelper);
                    if (!citTestHelper.isCitRunning()) {
                        Log.v(TAG, "Start Cit by sd card");
                        Intent citIntent = new Intent();
                        citIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        citIntent.setPackage("com.sim.cit");
                        citIntent.setClassName(context, "com.sim.cit.MainList");
                        citIntent.putExtra("isStartedBySdCard", true);
                        context.startActivity(citIntent);
                    }
                }
//				Log.v(TAG, "TFBroadCastReceiver__action = " + action + "__mBootCompeletedeTime = " + CITTestHelper.mBootCompeletedeTime + "__citFile.exists() = " + citFile.exists());
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
//			CITTestHelper.mBootCompeletedeTime = System.currentTimeMillis();
            Editor editor = sp.edit();
            editor.putBoolean(BOOTCOMPELETED, true);
            editor.apply();
        } else if (action.equals(Intent.ACTION_SHUTDOWN)) {
            Editor editor = sp.edit();
            editor.putBoolean(BOOTCOMPELETED, false);
            editor.apply();
        }

//		Log.v(TAG, "TFBroadCastReceiver__action = " + action + "__mBootCompeletedeTime = " + CITTestHelper.mBootCompeletedeTime);
    }

    public void wakeUp(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wakeLock.acquire(1000);  //点亮屏幕
        }
    }
}
