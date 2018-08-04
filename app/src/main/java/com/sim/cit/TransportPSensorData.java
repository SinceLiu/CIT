package com.sim.cit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TransportPSensorData extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{	
		//Intent sayHelloIntent=new Intent(context,SayHello.class);
		//sayHelloIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//context.startActivity(sayHelloIntent);

		int res;
		File f0, f30;
		String tmp;
		BufferedReader f0br, f30br;
		try
		{
			f0 = new File("/persist/simcom/stk_ps/ps_0_nv.file");
			f30 = new File("/persist/simcom/stk_ps/ps_30_nv.file");
			if (!f0.exists() || !f30.exists())
			{
				Log.e("TransportPSensorData", "TransportPSensorData the file is not exist");
				return;
			}
			else
			{
				Log.e("TransportPSensorData", "TransportPSensorData file exist");
				f0br = new BufferedReader(new InputStreamReader(new FileInputStream(f0)));
				f30br = new BufferedReader(new InputStreamReader(new FileInputStream(f30)));
				tmp = f0br.readLine();

				if (tmp != null)
				{
					CommonDrive.proximitySetCali(0, Integer.parseInt(tmp));
				}
				
				tmp = f30br.readLine();
				if (tmp != null)
				{
					CommonDrive.proximitySetCali(30, Integer.parseInt(tmp));
				}

				f0br.close();
				f30br.close();
			}
		}
		catch(Exception e)
		{
			Log.e("TransportPSensorData", "Exception: "+e.toString());
		}

	}

}
