package com.sim.cit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import android.os.SystemProperties;

/*
In this file, "/data/data/nvm" is diff with cit's, it's created by fingerlockscr.
The "/data/data/nvm" and "/persist/nvm" will be del once user download new ver.			
*/
public class AddSystemAttribute extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{	
	    try
	    {
                SystemProperties.set("persist.sys.version.software", getInternal_version() + getROMSize());
	    }
	    catch(Exception e)
	    {
		Log.e("AddSystemAttribute", "Exception: "+e.toString());
	    }

	}

    private String getInternal_version() {
        return getSystemproString("ro.product.internaledition");
    }

    private String getROMSize() {
        String procCurrentStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sys/class/mmc_host/mmc0/mmc0:0001/block/mmcblk0/size"), 256);
            try {
                procCurrentStr = reader.readLine();
            } finally {
                reader.close();
            }
            Log.e("Version_getROMSize", "getROMSize()  = [" + procCurrentStr + "]");

            int size_ROM = Integer.parseInt(procCurrentStr);

            if (size_ROM >= 60000000) {
                return "-32G";
            } else if (size_ROM <= 31000000) {
                return "-16G";
            }
            return "unknown";
        } catch (Exception e) {
            Log.e("Version_getROMSize", "getROMSize() has Exception   = [" + e.toString() + "]");
            return "unknown";
        }
    }

    private static String getSystemproString(String property) {
		return SystemProperties.get(property, "unknown");
    }

}
