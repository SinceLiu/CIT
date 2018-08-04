package com.sim.cit.testitem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sim.cit.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class ClearDataActivity extends Activity implements OnClickListener{
	private final static String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	private final int MSG_CLEARDATA_FINISH = 0x1000;//清除数据完成
	ClearUserDataObserver mClearDataObserver;
	//包名
	Stack<String> mPackageNames = new Stack<String>();
	//机器中保留的文件
	Stack<String> mDefaultPaths = new Stack<String>();
	Handler mHandler;
	boolean mIsClearEnd = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_clear_data);
		
		mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
					case MSG_CLEARDATA_FINISH:
//						ComponentName name = new ComponentName("com.android.provision", "com.android.provision.DefaultActivity");
//						PackageManager tempm = getPackageManager();
//						System.out.println("tempm.getComponentEnabledSetting(name) = " + tempm.getComponentEnabledSetting(name));
//						if(!(tempm.getComponentEnabledSetting(name) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED))
//				        {
//				        	System.out.println("PackageManager.COMPONENT_ENABLED_STATE_ENABLED false");
//				        	Message message = new Message();
//							message.what = MSG_CLEARDATA_FINISH;
//							mHandler.sendMessageDelayed(message, 1000);
//							return;
//				        }
//						System.out.println("MSG_CLEARDATA_FINISH__1");
						
//						String path = "/data/createbyhqb";
//						File file = new File(path);
//						try {
//							file.createNewFile();
//						} catch (Exception e) {
//							// TODO: handle exception
//							System.out.println("create file exception");
//							e.printStackTrace();
//						}
						
						dismissProgressDialog();
//						System.out.println("MSG_CLEARDATA_FINISH__2");
						Toast.makeText(ClearDataActivity.this, "数据清除完成", Toast.LENGTH_SHORT).show();
//						System.out.println("MSG_CLEARDATA_FINISH__3");
//						System.out.println("clearData__MSG_CLEARDATA_FINISH");
						PowerManager pm = (PowerManager) ClearDataActivity.this.getSystemService(Context.POWER_SERVICE);
//				        pm.rebootSafeMode(false, false);
						pm.reboot("");
						break;
				}
			}
		};
	}
	
	public String getReadboyRecoveryPassword(){
		String currentTime = new SimpleDateFormat("MMddHHmm").format(new Date());
		currentTime += currentTime.replace("0", "");
		return currentTime;
	}
	
	ProgressDialog mProgressDialog;
	 /**
	 * 创建mProgressDialog
	 */
	 private void createProgressDialogTitle(String title)
	 {
		 if(mProgressDialog == null)
		 {
			 mProgressDialog = new ProgressDialog(this);
		 }
		 mProgressDialog.setTitle(title);
		 mProgressDialog.setIndeterminate(true);
		 mProgressDialog.setCancelable(false);
		 mProgressDialog.show();
	 }
	
	 /**
	  * 隐藏mProgressDialog
	  */
	 private void dismissProgressDialog()
	 {
		 if(isActivityValid(this) && mProgressDialog != null && mProgressDialog.isShowing())
		 {
			 mProgressDialog.dismiss();
		 }
	 }
	 
	 /**
		 * The full path name of the file to delete.
		 * 删除指定路径的文件
		 * @param path name
		 * @return
		 */
		public int deleteTarget(String path) {
			File target = new File(path);
			//文件存在并且是文件，则直接删除
			//if(target.exists() && target.isFile() && target.canWrite()) 
			if(target.exists() && target.isFile())
			{
				target.delete();
				return 0;
			}
			//文件存在且是文件夹 
			else if(target.exists() && target.isDirectory()) {
				String[] file_list = target.list();
				//文件夹存在并且是空的文件夹则直接删除
				if(file_list != null && file_list.length == 0) {
					if(target.delete())
					{
						return 1;
					}
					else
					{
						return -1;
					}
					
				}
				//文件夹存在并且文件夹不为空，则递归将文件夹内的文件一个个删除，最后再将空的文件夹删除 
				else if(file_list != null && file_list.length > 0) {
					
					//for(int i = 0; i < file_list.length && deleteflag; i++)
					for(int i = 0; i < file_list.length; i++) {
						String filePath = target.getAbsolutePath() + "/" + file_list[i];
						File temp_f = new File(filePath);

						if(temp_f.isDirectory())
						{
							deleteTarget(temp_f.getAbsolutePath());
						}
						else if(temp_f.isFile())
						{
							temp_f.delete();
						}
					}
				}
				//if(target.exists() && deleteflag)
				if(target.exists())
					if(target.delete())
						return 2;
			}	
			return -3;
		}
	
	/**
	 * 清除数据
	 */
	private void clearData()
	{
		createProgressDialogTitle("正在清除数据，请稍后");
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setAction("com.readboy.cleardata");
				intent.putExtra("close", true);
				intent.putExtra("pwd", getReadboyRecoveryPassword());
				sendBroadcast(intent);
//				System.out.println("clearData__1");
				
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
					// TODO: handle exception
				}
//				System.out.println("clearData__2");
				
				// TODO Auto-generated method stub
//				android.provider.Settings.Global.getString(getContentResolver(), "getCleanSystemProviderDatabaseByDivhee");
//				PackageManager pm = getPackageManager();
//		        ComponentName name = new ComponentName("com.android.provision", "com.android.provision.DefaultActivity");
//		        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//		                PackageManager.DONT_KILL_APP);
//				PackageManager tempm = getPackageManager();
//		        ComponentName temname = new ComponentName("com.android.provision", "com.android.provision.DefaultActivity");
//				System.out.println("firsrt tempm.getComponentEnabledSetting(name) = " + tempm.getComponentEnabledSetting(temname));
				addDefaultFilePath();
//				System.out.println("clearData__3");
				loadInstallApps();
//				System.out.println("clearData__4");
//				for(int index = 0; index < mPackageNames.size(); index ++)
				for(int index = 0; index < mPackageNames.size();)
				{
					if(mIsClearEnd)
					{
//						System.out.println("clearData__5__index = " + index);
						try {
							initiateClearUserData(mPackageNames.get(index));
						} catch (Exception e) {
							// TODO: handle exception
//							System.out.println("clearData__e = " + e.toString());
							e.printStackTrace();
						}
						index ++;
					}
					else
					{
//						System.out.println("wait clear finish index = " + index);
						try {
							Thread.sleep(50);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
//				System.out.println("clearData__6");
				File rootFile = new File(ROOT);
//				System.out.println("clearData__7");
				if(rootFile.exists())
				{
					File[] files = rootFile.listFiles();
					if(files != null)
					{
						for(int index = 0; index < files.length; index ++)
						{
							if(files[index].exists())
							{
								boolean isDelete = true;
								String temPath = files[index].getAbsolutePath();
								for(int count = 0; count < mDefaultPaths.size(); count ++)
								{
									if(mDefaultPaths.get(count).equals(temPath))
									{
										isDelete = false;
										break;
									}
								}
								
								if(isDelete)
								{
									int result = deleteTarget(temPath);
//									System.out.println("temPath = " + temPath + "__isDelete = " + isDelete + "__result = " + result);
								}
							}
						}
					}
				}
//				System.out.println("clearData__8");
//				android.provider.Settings.Global.getString(getContentResolver(), "getCleanSystemProviderDatabaseByDivhee");
				PackageManager pm = getPackageManager();
		        ComponentName name = new ComponentName("com.android.provision", "com.android.provision.DefaultActivity");
		        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		                PackageManager.DONT_KILL_APP);
//		        android.provider.Settings.Global.putInt(getContentResolver(), "clearbyhqb", 100);
//		        try {
//		        	System.out.println("COMPONENT_ENABLED_STATE_ENABLED__2000ms__" + android.provider.Settings.Global.getInt(getContentResolver(), "clearbyhqb", 0));
//				} catch (Exception e) {
//					// TODO: handle exception
//					System.out.println("COMPONENT_ENABLED_STATE_ENABLED__2000ms__Exception");
//					e.printStackTrace();
//				}
//		        System.out.println("clearData__9");
		        String path = "/data/createbyhqb";
				File file = new File(path);
				try {
					file.createNewFile();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("create file exception");
					e.printStackTrace();
				}
//				System.out.println("clearData__10");
		        
				Message message = new Message();
				message.what = MSG_CLEARDATA_FINISH;
				mHandler.sendMessageDelayed(message, 5000);
			}
		});
		thread.start();
	}
	
	/**
	 * 加载默认不清楚的文件的路径
	 */
	private void addDefaultFilePath()
	{
		mDefaultPaths.add(ROOT + "/.readboy");
		mDefaultPaths.add(ROOT + "/microclass");
		mDefaultPaths.add(ROOT + "/教材全解");
		mDefaultPaths.add(ROOT + "/名师辅导班");
		mDefaultPaths.add(ROOT + "/视频播放");
		mDefaultPaths.add(ROOT + "/Wifi_analysis_3.6.2.apk");
//		for(int index = 0; index < mDefaultPaths.size(); index ++)
//		{
//			System.out.println("path " + index + " is " + mDefaultPaths.get(index));
//		}
	}
	
	/**
	 * load出所有安装的应用
	 */
    private void loadInstallApps() 
    {
    	mPackageNames.clear();
    	List<PackageInfo> mApps;	// AllApp列表
    	mApps = getPackageManager().getInstalledPackages(0);  
    	PackageInfo pakinfo;
    	JSONArray jsonArray = new JSONArray();
    	int versionCode;
    	for(int i=0; i<mApps.size(); i++){
    		pakinfo = mApps.get(i);
    		try {
				if (pakinfo!=null) {
					String packageName = pakinfo.packageName;
//					System.out.println("loadInstallApps__packageName = " + packageName);
//					if(!packageName.equals("com.sim.cit")
//						&& !packageName.equals("com.android.providers.media")
//						&& !packageName.equals("android")
						
//						&& !packageName.equals("com.qti.service.colorservice")
//						&& !packageName.equals("com.quicinc.cne.CNEService")
//						&& !packageName.equals("org.simalliance.openmobileapi.service")
//						&& !packageName.equals("com.qualcomm.wfd.service")
//						&& !packageName.equals("com.android.statementservice")
//						&& !packageName.equals("com.qti.dpmserviceapp")
//						&& !packageName.equals("com.android.printservice.recommendation")
//						&& !packageName.equals("android.ext.services")
//						&& !packageName.equals("com.qualcomm.qti.tetherservice")
//						&& !packageName.equals("com.qualcomm.qti.services.secureui")
//						&& !packageName.equals("com.android.bluetoothmidiservice")
//						&& !packageName.equals("com.qualcomm.timeservice")
//						&& !packageName.equals("com.android.mtp")
//						&& !packageName.equals("com.android.provision")
//						&& !packageName.equals("com.android.sharedstoragebackup")
//						&& !packageName.equals("android.ext.shared")
//						&& !packageName.equals("com.android.onetimeinitializer")
//						&& !packageName.equals("com.android.defcontainer")
//						&& !packageName.equals("com.android.externalstorage")
//						&& !packageName.equals("com.android.providers.settings")
//						&& !packageName.equals("com.dream.calculator")	
//						&& !packageName.equals("com.readboy.launcher_c10") && !packageName.equals("com.readboy.launcher_c10_primary")  
//						&& !packageName.equals("com.android.settings")
//						)
					if(packageName.startsWith("com.readboy") 
					   || packageName.startsWith("cn.dream") 
					   || packageName.startsWith("cn.classone") 
					   || packageName.startsWith("android.dream")
					   || packageName.startsWith("com.dream")
					   )
//					if(packageName.equals("com.readboy.launcher_c10_primary") || packageName.equals("com.readboy.launcher_c10"))
					{
						mPackageNames.add(packageName);
					}
		        }
			} catch (Exception e) {
				e.printStackTrace();
			}  
    	}
    	mPackageNames.add("com.sohu.inputmethod.sogouoem");
		mPackageNames.add("com.android.gallery3d");
		mPackageNames.add("com.adobe.air");
		mPackageNames.add("cn.wps.moffice_eng");
		mPackageNames.add("com.cyanogenmod.filemanager");
    }
	
    /**
     * 根据包名，清除缓存
     * @param packageName
     */
	private void initiateClearUserData(String packageName) {
        // Invoke uninstall or clear user data based on sysPackage
//        System.out.println("Clearing user data for package : " + packageName);
		mIsClearEnd = false;
        if (mClearDataObserver == null) {
            mClearDataObserver = new ClearUserDataObserver();
        }
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        boolean res = am.clearApplicationUserData(packageName, mClearDataObserver);
//        System.out.println("initiateClearUserData__packageName = " + packageName + "__" + System.currentTimeMillis());
        if (!res) {
            // Clearing data failed for some obscure reason. Just log error for now
//        	System.out.println("Couldn't clear application user data for package:"+packageName);
        } else {
        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.clear_data:
//				initiateClearUserData("com.readboy.fileexplore");
				clearData();
				break;
		}
	}
	
	class ClearUserDataObserver extends IPackageDataObserver.Stub {
	       public void onRemoveCompleted(final String packageName, final boolean succeeded) {
	           mIsClearEnd = true;
	        }
	    }
	
	/**
     * 判断当前activity是否有效
     *
     * @param activity
     * @return
     */
    public static boolean isActivityValid(Activity activity) {
        if (activity != null) {
            if (activity.isFinishing()) {
                return false;
            }
            return true;
        }
        return false;
    }

}
