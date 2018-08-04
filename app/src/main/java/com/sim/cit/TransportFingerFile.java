package com.sim.cit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
In this file, "/data/data/nvm" is diff with cit's, it's created by fingerlockscr.
The "/data/data/nvm" and "/persist/nvm" will be del once user download new ver.			
*/
public class TransportFingerFile extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{	
		int len = 0;
		byte[] buffer = new byte[1024];
		File srcFile;
		File destFile;
		FileInputStream fis;
		FileOutputStream fos;
		try
		{
			srcFile = new File("/data/data/nvm");
			destFile = new File("/persist/nvm");
			fis = new FileInputStream(srcFile); 
			fos = new FileOutputStream(destFile);

			if (srcFile.exists())
			{
				if (!destFile.exists())	
				{
					destFile.createNewFile();
					while ((len = fis.read(buffer)) != -1)
					{	
						fos.write(buffer, 0, len);
					}
				}				
				//should not write file here, diff with cit
				fos.flush();			
			}
			fos.close();
			fis.close();
		}
		catch(Exception e)
		{
			Log.e("TransportFingerFile", "Exception: "+e.toString());
		}

	}
}
