package com.sim.cit;

import android.content.BroadcastReceiver;
import java.io.DataOutputStream;
import java.io.IOException;
import android.content.Intent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import android.util.Log;
import com.sim.cit.CommonDrive;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;


//import com.nrs.utils.tools.CrashHandler;

public class BootCompletedReceiver extends BroadcastReceiver {	

    @Override
    public void onReceive(final Context context, Intent intent) {

        // TODO Auto-generated method stub
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            File f1 = new File("/persist", "config.dat");
            File f2 = new File("/data/local/tmp", "config.dat");
            if (f1.exists()) {
                Log.i("BootCompletedReceiver", "f1="+f1.getAbsolutePath()+"\n"+"f2="+f2.getAbsolutePath());
                CommonDrive.copyFile(f1.getAbsolutePath(), f2.getAbsolutePath());
            }
			
			if(createSomeDirs())
	    		scanFloder(context);
			
        }
 
    }
	
	private String[] dirs = {
			"SmallSinger/Accompany",
			"SmallSinger/Lrc",
			"SmallSinger/Song",
			
			"单词记忆",
			"海淀名师",
			"黄冈视频",
			"黄冈特训",
			
			"决胜中高考/高考精讲",
			"决胜中高考/高考试题",
			"决胜中高考/高考视频",
			"决胜中高考/中考精讲",
			"决胜中高考/中考试题",
			"决胜中高考/中考视频",
			
			"课本点读/语文",
			"课本点读/数学",
			"课本点读/英语",
			
			"口语练习",
			"口语评测",
			"启东名师",
			"五三全解",
			"五三全练",
			"音乐欣赏"
			
	};
	
	public boolean createSomeDirs() {
		boolean shouldScan = false;
		Log.e("BootCompletedReceiver", "--------state ="+Environment.getExternalStorageState());
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			return shouldScan;
		
		String prePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		Log.e("BootCompletedReceiver", "prePath ="+prePath);
		List<String> dirsList = Arrays.asList(dirs);
		for (String string : dirsList) {
			File file = new File(prePath+"/"+string);
			if(!file.exists())
			{
				file.mkdirs();
				shouldScan = true;//一旦新创建了就需要扫描
			}
		}
		return shouldScan;
	}
	
	public static void scanFloder(Context context)
	{
		Bundle args = new Bundle();
        args.putString("volume", "external");
        Intent startScan = new Intent();
        startScan.putExtras(args);
        startScan.setComponent(new ComponentName("com.android.providers.media",
                "com.android.providers.media.MediaScannerService"));
        context.startService(startScan);
	}

}
