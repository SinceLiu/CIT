package com.readboy.util;

import java.io.File;

import android.os.Environment;
import android.util.Log;

public class ResultWriter {
	private static final String DIR_NAME =Environment.getExternalStorageDirectory().toString()
			+ File.separatorChar + "smt_detection" ;
	private static final String FILE_NAME =DIR_NAME + File.separator + "flags.dat";
	
	public static void write(String content){
			Log.i("adsd","make dir = "+FileOperation.createDir(new File(DIR_NAME)));
			Log.i("adsd","create = "+FileOperation.createFile(new File(FILE_NAME)));
			try {
				FileOperation.writeTxtFile(content, new File(FILE_NAME));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
