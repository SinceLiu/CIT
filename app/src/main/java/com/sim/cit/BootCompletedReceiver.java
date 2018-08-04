package com.sim.cit;

import android.content.BroadcastReceiver;
import java.io.DataOutputStream;
import java.io.IOException;
import android.content.Intent;
import java.io.File;
import android.util.Log;
import com.sim.cit.CommonDrive;
import android.content.Context;


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
        }
 
    }

}
