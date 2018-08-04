package com.readboy.util;

import java.io.File;

import com.sim.cit.CITTestHelper;
import com.sim.cit.testitem.TFCard;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class TFBroadCastReceiver extends BroadcastReceiver{
	final String LOCALDATA = "localdata";
	final String BOOTCOMPELETED = "bootcompeleted";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		SharedPreferences sp = context.getSharedPreferences(LOCALDATA, Context.MODE_PRIVATE);
		if(action.equals(Intent.ACTION_MEDIA_MOUNTED))
		{
			try {
				Log.v("TFBroadCastReceiver", "TFBroadCastReceiver1__action = " + action );
				String tfPath = DiskManager.getSdStoragePath(context);
				String citPath = tfPath + "/readboycittest.cit";
				File citFile = new File(citPath);
				
				if(citFile.exists())
				{
					Log.v("TFBroadCastReceiver", "boot complete.");
					CITTestHelper citTestHelper = (CITTestHelper)context.getApplicationContext();
					citTestHelper.setStartedBySdCard(true);
					Intent citIntent = new Intent();
					citIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
					citIntent.setPackage("com.sim.cit");
				    citIntent.setClassName(context,"com.sim.cit.MainList");
				    context.startActivity(citIntent);
				}
//				Log.v("TFBroadCastReceiver", "TFBroadCastReceiver__action = " + action + "__mBootCompeletedeTime = " + CITTestHelper.mBootCompeletedeTime + "__citFile.exists() = " + citFile.exists());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}
		else if(action.equals(Intent.ACTION_BOOT_COMPLETED))
		{
//			CITTestHelper.mBootCompeletedeTime = System.currentTimeMillis();
			Editor editor = sp.edit();
			editor.putBoolean(BOOTCOMPELETED, true);
			editor.commit();
		}
		else if (action.equals(Intent.ACTION_SHUTDOWN)) 
		{
			Editor editor = sp.edit();
			editor.putBoolean(BOOTCOMPELETED, false);
			editor.commit();
		}
		
//		Log.v("TFBroadCastReceiver", "TFBroadCastReceiver__action = " + action + "__mBootCompeletedeTime = " + CITTestHelper.mBootCompeletedeTime);
	}

}
