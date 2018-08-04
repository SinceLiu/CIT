package com.sim.cit.testitem;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
//import com.qualcomm.qcnvitems.QcNvItems;
import com.readboy.nv.NvJniItems;
import com.readboy.nv.NvItemIds;
import com.readboy.nv.NvInfo;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.os.Handler;
import android.os.Message;
//add for read the auto test results to NV2499 by songguangyu 20140218 start
//import com.qualcomm.qcrilhook.QcRilHook;
//add for read the auto test results to NV2499 by songguangyu 20140218 end
//Modify for CIT optimization by xiasiping 20140730 start
import java.util.List;
import com.sim.cit.MyXml;
import com.sim.cit.XNode;
import com.sim.cit.XProperty;
import com.sim.cit.XMethod;
import com.sim.cit.MyXmlUtils;
import android.widget.Toast;
import java.lang.reflect.Method;
import com.sim.cit.CITTestHelper;
//Modify for CIT optimization by xiasiping 20140730 end
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Version extends TestActivity {

    private static final String TAG = "Version";
        //add to support snNum by songguangyu 20131201 start
//	private static QcNvItems mNv;
        private static final int GET_SN_NUMBER = 121;
        private static String SN_NUM = "unknown";
	private static TextView[] tv = null;
        //add to support snNum by songguangyu 20131201 end
	TelephonyManager tm;
	final int WCDMA_FT=23;
	final int WCDMA_BT=24;
	final int CDMA_FT=27;
	final int CDMA_BT=28;
	final int GSM_FT=25;
	final int GSM_BT=26;
	final byte FT_BT_PASS=80;
	final byte FT_BT_PASS_SHIFT=112;
	final int FT_BT_FAIL=26;
        //add for read the auto test results to NV2499 by songguangyu 20140218 start
//        private QcRilHook mRilHook;
        private final int GET_FACTORY_DATA3 = 122;
        private final int SHOW_FACTORY_DATA3 = 123;
        //add for read the auto test results to NV2499 by songguangyu 20140218 end
        //Modify for CIT optimization by xiasiping 20140730 start
        private MyXml mxml;
        private List<XProperty> xProperties;
        private List<XMethod> xMethods;
        private List<XNode> xNodes;
        //Modify for CIT optimization by xiasiping 20140730 end

	protected void onCreate(Bundle savedInstanceState) {
                //Modify for CIT optimization by xiasiping 20140730 start
                Log.e(TAG,"Version_activity start!~~");
                try {
                    mxml = new MyXmlUtils().getMxml();
                    xProperties = mxml.getXProperties();
                    xMethods = mxml.getXMethods();
                    xNodes = mxml.getXNodes();
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"xsp_May be config file has error");
                    Toast.makeText(this, "get xml resources fail", 3000).show();
                }
                //Modify for CIT optimization by xiasiping 20140730 end
		layoutId = R.layout.versionlayout;
		super.onCreate(savedInstanceState);
		tv = new TextView[25];
		//mNv = new QcNvItems(this);
//                mNv = new QcNvItems(getApplicationContext());
                //add for read the auto test results to NV2499 by songguangyu 20140218 start
//                mRilHook = new QcRilHook(this);
                //add for read the auto test results to NV2499 by songguangyu 20140218 end
		tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		InitWeigt();
                //Modify for CIT optimization by xiasiping 20140730 start
                String model = null;
                String baseband = null;
                if (xProperties != null) {
                    for (XProperty xpp : xProperties) {
                        String name = xpp.getName();
                        if ("model".equals(name)) {
                            model = xpp.getValue();
                        }
                        if ("baseband".equals(name)) {
                            baseband = xpp.getValue();
                        }

                    }

		}

		if (model != null) {
			Log.e(TAG, "xsp_model = " + model);
			tv[1].setText(getSystemproString(model)); // 型号
		} else {
			Log.e(TAG, "xsp_model is null");
			tv[1].setText(getSystemproString("ro.product.model"));

		}
		// tv[1].setText(getSystemproString("ro.product.model"));
		// Modify for CIT optimization by xiasiping 20140730 end
		String innerVersion = getInternal_version() + getSECVersion();
		Log.i(TAG, "---------getInternal_version = "+getInternal_version()+"----sec = "+getSECVersion());
		if (TextUtils.isEmpty(innerVersion) || innerVersion.contains("unknown"))
			innerVersion = getSoftwareVersion2();
		tv[11].setText(innerVersion);	//软件版本
		// tv[5].setText(SystemProperties.get("ro.product.version.hardware","hardware"));
		tv[5].setText(getHW_version());	//硬件版本
		// Modify for CIT optimization by xiasiping 20140730 start
		if (baseband != null) {
			Log.e(TAG, "xsp_baseband = " + baseband);
			tv[7].setText(SystemProperties.get(baseband, getResources().getString(R.string.cit_info_default)));
		} else {
			Log.e(TAG, "xsp_baseband is null");
			tv[7].setText(
					SystemProperties.get("gsm.version.baseband", getResources().getString(R.string.cit_info_default)));
		}
		/*
		 * tv[7].setText(SystemProperties.get("gsm.version.baseband",
		 * getResources().getString(R.string.cit_info_default)));
		 */
		// Modify for CIT optimization by xiasiping 20140730 end
		getSnVersion();
		tv[3].setText(getSoftwareVersion());	//内部版本
		/*
		 * tv[12].setText(getBt_Ft_info(WCDMA_FT));
		 * tv[13].setText(getBt_Ft_info(WCDMA_BT));
		 * tv[14].setText(getBt_Ft_info(CDMA_FT));
		 * tv[15].setText(getBt_Ft_info(CDMA_BT));
		 * tv[16].setText(getBt_Ft_info(GSM_FT));
		 * tv[17].setText(getBt_Ft_info(GSM_BT));
		 */
		/* 20130111-modify for remove IMEI test by lvhongshan start */
		// tv[19].setText(tm.getDeviceId());
		/* 20130111-modify for remove IMEI test by lvhongshan end */

                //add for read the auto test results to NV2499 by songguangyu 20140218 start
                mmHandler.removeMessages(GET_FACTORY_DATA3);
                mmHandler.sendMessageDelayed(mmHandler.obtainMessage(GET_FACTORY_DATA3),1000);
                //add for read the auto test results to NV2499 by songguangyu 20140218 end
                if (CITTestHelper.getIsFromCIT()) {
                    Log.i(TAG, "the cit_config_item.xml is found from CIT");
                    tv[18].setText(R.string.use_pa568_list_item);
                } else {
                    Log.i(TAG, "the cit_config_item.xml is found from storage");
                    tv[18].setText(R.string.use_cit_config_item);
                }
                if (MyXmlUtils.getIsFromStorage()) {
                    Log.i(TAG, "the cit_conf_node.xml is found from storage");
                    tv[19].setText(R.string.use_cit_conf_node_from_storage);
                } else {
                    Log.i(TAG, "the cit_conf_node.xml is found from CIT");
                    tv[19].setText(R.string.use_cit_conf_node_from_cit);
                }
                // add PA568 list item by xiasiping 20140811 start
                tv[20].setText(getString(R.string.tp_type) +":" + getTPManufacturer());
                tv[21].setText(getString(R.string.lcd_type) +":" + getLCDManufacturer());
                //Modify for add front camera information by lizhaobo 20151120 start
                tv[22].setText(getString(R.string.front_camera_type) +":" + getFrontCameraManufacturer());
                tv[23].setText(getString(R.string.camera_type) +":" + getCameraManufacturer());
                tv[24].setText(getString(R.string.rom_type) +":" + getROMSupplier() + "_" + getROMSize());
                //Modify for add front camera information by lizhaobo 20151120 start
	}

	private void InitWeigt() {
		tv[0] = (TextView) findViewById(R.id.t1);
		tv[1] = (TextView) findViewById(R.id.t2);
		tv[2] = (TextView) findViewById(R.id.t3);
		tv[3] = (TextView) findViewById(R.id.t4);
		tv[4] = (TextView) findViewById(R.id.t5);
		tv[5] = (TextView) findViewById(R.id.t6);
		tv[6] = (TextView) findViewById(R.id.t7);
		tv[7] = (TextView) findViewById(R.id.t8);
		tv[8] = (TextView) findViewById(R.id.t9);
		tv[9] = (TextView) findViewById(R.id.t10);
		tv[10] = (TextView) findViewById(R.id.t11);
		tv[11] = (TextView) findViewById(R.id.t12);
                //modify for read the auto test results to NV2499 by songguangyu 20140218 start
                tv[12] = (TextView) findViewById(R.id.t13);
                tv[13] = (TextView) findViewById(R.id.t14);
                tv[14] = (TextView) findViewById(R.id.t15);
                tv[15] = (TextView) findViewById(R.id.t16);
                tv[16] = (TextView) findViewById(R.id.t17);
                tv[17] = (TextView) findViewById(R.id.t18);
                // add PA568 list item by xiasiping 20140811 start
                tv[18] = (TextView) findViewById(R.id.t19);
                tv[19] = (TextView) findViewById(R.id.t20);
                // add PA568 list item by xiasiping 20140811 end
                //modify for read the auto test results to NV2499 by songguangyu 20140218 end
                /*20130121-modify for add encryption sn test by lvhongshan start*/
	/*	tv[18] = (TextView) findViewById(R.id.t19);
		tv[19] = (TextView) findViewById(R.id.t20);*/
                /*20130121-modify for add encryption sn test by lvhongshan end*/
                /*20130111-modify for remove IMEI test by lvhongshan start*/
//		tv[18] = (TextView) findViewById(R.id.t19);
                /*20130111-modify for remove IMEI test by lvhongshan end*/
                tv[20] = (TextView) findViewById(R.id.t21);
                tv[21] = (TextView) findViewById(R.id.t22);
                tv[22] = (TextView) findViewById(R.id.t23);
                tv[23] = (TextView) findViewById(R.id.t24);
                //Modify for add front camera information by lizhaobo 20151120 start
                tv[24] = (TextView) findViewById(R.id.t25);
                //Modify for add front camera information by lizhaobo 20151120 start

//		tv[0].setText("" + getString(R.string.sn_model));
//		tv[2].setText(getString(R.string.software_title));
//		tv[4].setText(getString(R.string.baseband_version));
//		tv[6].setText(getString(R.string.sn_version));
//		tv[8].setText(getString(R.string.internal_version));
//		tv[10].setText(getString(R.string.software_title));
//		tv[12].setText(getString(R.string.baseband_version));
//		tv[14].setText(getString(R.string.sn_version));
//		tv[16].setText(getString(R.string.internal_version));
	}

	private static String getSystemproString(String property) {
		return SystemProperties.get(property, "unknown");
	}
        //modify to support snNum by songguangyu 20131201 start
	private void getSnVersion() {
		mmHandler.removeMessages(GET_SN_NUMBER);
	        mmHandler.sendMessageDelayed(mmHandler.obtainMessage(GET_SN_NUMBER), 1000);
	}
        //modigy to support snNum by songguangyu 20131201 end

	private String getSoftwareVersion() {
                //Modify for CIT optimization by xiasiping 20140730 start
                String software_title = null;
                String software_time = null;
                String software_buildtime = null;
                if (xProperties != null) {
                    for (XProperty xpp : xProperties) {
                        String name = xpp.getName();
                        if ("software_title".equals(name)) {
                            software_title = xpp.getValue();
                        }
                        if ("software_time".equals(name)) {
                            software_time = xpp.getValue();
                        }
                        if ("software_buildtime".equals(name)) {
                            software_buildtime = xpp.getValue();
                        }
                    
                    }
                }

                String softwareTitle = null;
                String softwareTime = null;
                String softwareBuildTime = null;
                if (software_title != null) {
                    Log.e(TAG,"xsp_software_title = "+software_title);
		    softwareTitle = getSystemproString(software_title);
                }else{
		    softwareTitle = getSystemproString("ro.product.version.software");
                    Log.e(TAG,"xsp_software_title is null ");
                }
                if (software_time != null) {
                    Log.e(TAG,"xsp_software_time = "+software_time);
		    softwareTime = getSystemproString(software_title);
                }else{
		    softwareTime = getSystemproString("ro.build.version.incremental");
                    Log.e(TAG,"xsp_software_time is null");
                }
                if (software_buildtime != null) {
                    Log.e(TAG,"xsp_software_buildtime = "+software_buildtime);
		    softwareBuildTime = getSystemproString(software_buildtime);
                }else{
		    softwareBuildTime = getSystemproString("ro.product.date");
                    Log.e(TAG,"xsp_software_buildtime is null");
                }
                
		//String softwareTitle = getSystemproString("ro.product.version.software");
		//String softwareTime = getSystemproString("ro.build.version.incremental");
                //String softwareBuildTime = getSystemproString("ro.product.date");
                //Modify for CIT optimization by xiasiping 20140730 end
//                softwareTitle = softwareTitle + getSECVersion() + "_" + softwareBuildTime;
        		String head = softwareTitle + getSECVersion();
        		if (TextUtils.isEmpty(head) || head.contains("unknown"))
        			head = getSoftwareVersion2();
        		softwareTitle = head + "_" + softwareBuildTime;
		/*if (softwareTime.length() > 18) {
			int nPos = softwareTime.indexOf(".");
			nPos = softwareTime.indexOf(".", nPos);
			softwareTime = softwareTime.substring(nPos + 1);
			// modify 2011-1-5
			nPos = softwareTime.indexOf(".");
			nPos = softwareTime.indexOf(".", nPos);

			softwareTime = softwareTime.substring(nPos + 1);

			StringBuffer time = new StringBuffer(softwareTime);
			time.insert(4, '-');
			time.insert(7, '-');
			time.replace(10, 11, " ");
			time.insert(13, ':');
			time.insert(16, ':');

		//	softwareTitle += "\n" + time.toString();
		}*/

		return softwareTitle;

	}
	private String getInternal_version() {
                //Modify for CIT optimization by xiasiping 20140730 start
                String internal_version = null;
                if (xProperties != null) {
                    for (XProperty xpp : xProperties) {
                        String name = xpp.getName();
                        if ("internaledition".equals(name)) {
                            internal_version = xpp.getValue();
                        }
                    }
                }

                String ivStr = null;
                if (internal_version != null) {
                    Log.e(TAG,"xsp_internal_version = " + internal_version);
                    ivStr = getSystemproString(internal_version);
                } else {
                    ivStr = getSystemproString("ro.product.internaledition");
                    Log.e(TAG,"xsp_internal_version is null ");
                }
		//String ivStr = getSystemproString("ro.product.internaledition");
                //Modify for CIT optimization by xiasiping 20140730 end
		return ivStr;
	}
        private String getHW_version() {

                String hwVersion = "";
        /*        String hversion = SystemProperties.get("ro.hw_version_volt_mv");
                if(null == hversion){
                    return "hardware";
                }
                int iversion = Integer.parseInt(hversion);
                Log.i(TAG," "+iversion);
                if(iversion==131072){
                    hwVersion = "V1.00";
                }else if(iversion==65536){
                    hwVersion = "V0.00";
                }else {
                    hwVersion = "V2.00";
                }*/
                //Modify for CIT optimization by xiasiping 20140730 start
                String hw_version = null;
                String hw_subtype = null;
                String hw_version_promission = null;
                String hw_subtype_promission = null;

                if (xNodes != null) {
                    for (XNode xn : xNodes) {
                        String name = xn.getName();
                        if ("hw_version".equals(name)) {
                            hw_version = xn.getValue();
                            hw_version_promission = xn.getPermission();
                        }
                        if ("hw_subtype".equals(name)) {
                            hw_subtype = xn.getValue();
                            hw_subtype_promission = xn.getPermission();
                        }
                    }
                }

                String hwversion = null;
                String hwsubtype = null;
                if (hw_version != null && "O_RDONLY".equals(hw_version_promission)) {
                    Log.e(TAG,"xsp_hw_version = " + hw_version);
                    hwversion = CommonDrive.getHWVersion_d(hw_version);
                } else {
                    Log.e(TAG,"xsp_hw_version is null or the promission is not O_RDONLY");
                    hwversion = CommonDrive.getHWVersion();
                }
                if (hw_subtype != null && "O_RDONLY".equals(hw_subtype_promission)) {
                    Log.e(TAG,"xsp_hw_subtype = " + hw_subtype);
                    hwsubtype = CommonDrive.getHWVersion_d(hw_subtype);
                } else {
                    Log.e(TAG,"xsp_hw_version is null or the promission is not O_RDONLY");
                    hwsubtype = CommonDrive.getHWSubType();
                }

                //hwVersion = hwsubtype.replace('\n',' ') + "_" + hwversion.replace('\n',' ');
                hwVersion = hwversion.replace('\n',' ');
                //hwVersion = CommonDrive.getHWSubType().replace('\n',' ') + "_" + CommonDrive.getHWVersion().replace('\n',' ');
                //Modify for CIT optimization by xiasiping 20140730 end
                Log.i(TAG,"hwVersion is " + hwVersion);
                return hwVersion;
        }
	/*20121211 added for display the information of bt&ft by lvhongshan start*/
	private String getBt_Ft_info(int bt_ftindex) {
	//	int infos[]=mNv.get_NV2499_sn_number();
		byte[]infos=new byte[128]; 
		
	/*	try{
			mNv.get_NV2499_factory_data_3(infos);
		}
		catch(Exception e)
		{
			
		}
		for(int i=0;i<28;i++)
		{
			Log.i("lvhongshan", "infos["+i+"] is"+infos[i]);
			Log.i("lvhongshan", "infos["+(127-i)+"] is"+infos[127-i]);
		}*/
		String bt_ftinfo="FAIL";
	/*	switch(bt_ftindex){
		case WCDMA_FT:
			if(infos[WCDMA_FT]==FT_BT_PASS||infos[WCDMA_FT]==FT_BT_PASS_SHIFT){
				Log.i("lvhongshan_pass", "wcdma_ft"+infos[WCDMA_FT]);
				bt_ftinfo = getString(R.string.wft_result)+"pass";
			}
			else{
				Log.i("lvhongshan_fail", "wcdma_ft"+infos[WCDMA_FT]);
				bt_ftinfo = getString(R.string.wft_result)+"fail";
			}			
			break;
		case WCDMA_BT:
			if(infos[WCDMA_BT]==FT_BT_PASS||infos[WCDMA_BT]==FT_BT_PASS_SHIFT){
				Log.i("lvhongshan_pass", "wcdma_bt"+infos[WCDMA_BT]);
				bt_ftinfo = getString(R.string.wbt_result)+"pass";
			}
			else{
				Log.i("lvhongshan_fail", "wcdma_bt"+infos[WCDMA_BT]);
				bt_ftinfo = getString(R.string.wbt_result)+"fail";
			}
			break;
		case CDMA_FT:
			if(infos[CDMA_FT]==FT_BT_PASS||infos[CDMA_FT]==FT_BT_PASS_SHIFT){
				bt_ftinfo = getString(R.string.cft_result)+"pass";
			}
			else{
				bt_ftinfo = getString(R.string.cft_result)+"fail";
			}
			break;
			
		case CDMA_BT:
			if(infos[CDMA_BT]==FT_BT_PASS||infos[CDMA_BT]==FT_BT_PASS_SHIFT){
				bt_ftinfo = getString(R.string.cbt_result)+"pass";
			}
			else{
				bt_ftinfo = getString(R.string.cbt_result)+"fail";
			}
			break;			
		case GSM_FT:
			if(infos[GSM_FT]==FT_BT_PASS||infos[GSM_FT]==FT_BT_PASS_SHIFT){
				bt_ftinfo = getString(R.string.gft_result)+"pass";
			}
			else{
				bt_ftinfo = getString(R.string.gft_result)+"fail";
			}
			break;			
		case GSM_BT:
			if(infos[GSM_BT]==FT_BT_PASS||infos[GSM_BT]==FT_BT_PASS_SHIFT){
				bt_ftinfo = getString(R.string.gbt_result)+"pass";
			}
			else{
				bt_ftinfo = getString(R.string.gbt_result)+"fail";
			}
			break;
		default:
			break;
		}*/
		
		return bt_ftinfo;
		
	}
	/*20121211 added for display the information of bt&ft by lvhongshan end*/
        //add to support snNum by songguangyu 20131201 start
        private final Handler mmHandler = new Handler(){
                public void handleMessage(Message msg){
                        switch(msg.what){
                        case GET_SN_NUMBER:
                                //Modify for CIT optimization by xiasiping 20140730 start
                                try {
                                    String method_get_nv2497 = null;

                                    if (xMethods != null) {
                                        for (XMethod xm : xMethods) {
                                            String name = xm.getName();
                                            if ("qcnvitem_read2497".equals(name)) {
                                                method_get_nv2497 = xm.getValue();
                                            }
                                        }
                                    }

//					if (method_get_nv2497 != null) {
//						Log.e(TAG, "xsp_method_get_nv2497 = " + method_get_nv2497);
//						Object[] mArguments = new Object[0];
//						Method mMethod_readNV2497 = mNv.getClass().getMethod(method_get_nv2497, new Class[0]);
//						Log.i(TAG, "--------------mMethod_readNV2497 = " + mMethod_readNV2497);
//						SN_NUM = (String) mMethod_readNV2497.invoke(mNv, mArguments);
//						Log.i(TAG, "--------------SN_NUM = " + SN_NUM);
//					} else {
//						Log.e(TAG, "xsp_method_get_nv2497 is null ");
//						SN_NUM = mNv.get_NV2497_sn_number();
//					}
					// SN_NUM = mNv.get_NV2497_sn_number();
					// Modify for CIT optimization by xiasiping 20140730 end
				} catch (Exception e) {
					Toast.makeText(Version.this, getString(R.string.invoke_method_fail), 3000).show();
					Log.i("Version", e.toString());
					// SN_NUM = mNv.get_NV2497_sn_number();
				}
//				String serialNum = android.os.Build.SERIAL;
//				Log.i(TAG, "------------------serialNum = " + serialNum);
				if (SN_NUM.equals("unknown"))
					SN_NUM = getSerialNumber();
				tv[9].setText(SN_NUM); //
				break;
			// add for read the auto test results to NV2499 by songguangyu
			// 20140218 start
			case GET_FACTORY_DATA3:
				// Modify for CIT optimization by xiasiping 20140730 start
				try {
					Log.i(TAG, "GET_FACTORY_DATA3");
					String method_test_process = null;
					if (xMethods != null) {
						for (XMethod xm : xMethods) {
							String name = xm.getName();
							if ("qcrilhook_factory_test_process".equals(name)) {
								method_test_process = xm.getValue();
							}
						}
					}

//					if (method_test_process != null) {
//						Log.e(TAG, "xsp_method_test_process = " + method_test_process);
//						Object[] mArguments = new Object[] { 2499, 0, 0 };
//						Method mMethod_test_process = mRilHook.getClass().getMethod(method_test_process,
//								new Class[] { int.class, int.class, int.class });
//						mMethod_test_process.invoke(mRilHook, mArguments);
//
//					} else {
//						Log.e(TAG, "xsp_method_test_process is null ");
//						mRilHook.FactoryTestProcess(2499, 0, 0);
//					}
					parseNv2499(((CITTestHelper)getApplication()).getNv2499Data());
					// mRilHook.FactoryTestProcess(2499,0 ,0);
				} catch (Exception e) {
					Log.e(TAG, "xsp_method test_process has error");
					Toast.makeText(Version.this, getString(R.string.invoke_method_fail), 3000).show();
				}
				// Modify for CIT optimization by xiasiping 20140730 end
				mmHandler.removeMessages(SHOW_FACTORY_DATA3);
				mmHandler.sendMessageDelayed(mmHandler.obtainMessage(SHOW_FACTORY_DATA3), 1000);
				break;
			case SHOW_FACTORY_DATA3:
				Log.i(TAG, "SHOW_FACTORY_DATA3");
				// Modify for CIT optimization by xiasiping 20140730 start
				String data3_2499 = null;
				for (XProperty xpp : xProperties) {
					String name = xpp.getName();
					if ("data3_2499".equals(name)) {
						data3_2499 = xpp.getValue();
					}
				}
				if (data3_2499 != null) {
					Log.e(TAG, "xsp_data3_2499 = " + data3_2499);
					ShowBt_Ft_info(SystemProperties.get(data3_2499));
				} else {
					Log.e(TAG, "xsp_data3_2499 is null");
					ShowBt_Ft_info(SystemProperties.get("gsm.sim.data3"));
				}
				// ShowBt_Ft_info(SystemProperties.get("gsm.sim.data3"));
				// Modify for CIT optimization by xiasiping 20140730 end
				break;
			// add for read the auto test results to NV2499 by songguangyu
			// 20140218 end
			}
		}
	};
	// add to support snNum by songguangyu 20131201 end

        //add for read the auto test results to NV2499 by songguangyu 20140218 start
        private void ShowBt_Ft_info(String strData){
            char[] infos = new char[128];
		//add by dgy 20170223
		strData = ((CITTestHelper)getApplication()).getNv2499String();
		// add end
		Log.i(TAG, "-------------strData = "+strData);
		if (30 <= strData.length()) {
			infos = strData.toCharArray();
			for (int i = 0; i < 6; i++) {
				int j = i * 5;
				tv[i + 12].setText(" " + infos[j] + " " + infos[j + 1] + " " + infos[j + 2] + " " + infos[j + 3] + " "
						+ infos[j + 4]);
			}
		} else {
			tv[12].setText("Get Bt FT fail");
			Log.i(TAG, "Get factory data 3 fail ...");
		}
	}
	// add for read the auto test results to NV2499 by songguangyu 20140218 end

	@Override
	public void finish() {
		Log.i(TAG, "finish() start");
//		mRilHook.dispose();
		super.finish();
	}

    private String getTPManufacturer() {
        String procCurrentStr;

		try {
			Log.i(TAG, "----------------getTPManufacturer1");
			// BufferedReader reader = new BufferedReader(new
			// FileReader("/sys/class/i2c-dev/i2c-5/device/5-0038/tp_vendor"),
			// 256);
			// BufferedReader reader = new BufferedReader(new
			// FileReader("/sys/class/i2c-dev/i2c-3/device/3-005d/tp_vendor"),
			// 256);
			// BufferedReader reader = new BufferedReader(new
			// FileReader("/sys/devices/soc/78b7000.i2c/i2c-3/i2c-dev/i2c-3"),
			// 256);
			BufferedReader reader = new BufferedReader(new FileReader("/proc/tp_vendor"), 256);
			Log.i(TAG, "----------------getTPManufacturer2");
			try {
				procCurrentStr = reader.readLine();
				Log.i(TAG, "----------------getTPManufacturer procCurrentStr = " + procCurrentStr);
			} finally {
				reader.close();
			}
			return procCurrentStr;
		} catch (IOException e) {
			e.printStackTrace();
			return "unknown";
		}
	}

	private String getLCDManufacturer() {
		String procCurrentStr;

		try {
			Log.i(TAG, "----------------getLCDManufacturer1");
			BufferedReader reader = new BufferedReader(
					new FileReader("/sys/devices/soc.0/1a98000.qcom,mdss_dsi/lcd_vendor"), 256);
			Log.i(TAG, "----------------getLCDManufacturer");
			try {
				procCurrentStr = reader.readLine();
				Log.i(TAG, "----------------getLCDManufacturer procCurrentStr = " + procCurrentStr);
			} finally {
				reader.close();
			}
			return procCurrentStr;
		} catch (IOException e) {
			e.printStackTrace();
			return "unknown";
		}
	}

	private String getCameraManufacturer() {
		String procCurrentStr;

		try {
			BufferedReader reader = new BufferedReader(
					new FileReader("/sys/devices/soc.0/1b0c000.qcom,cci/3.qcom,eeprom/camera_name"), 256);
			try {
				procCurrentStr = reader.readLine();
			} finally {
				reader.close();
			}
			return procCurrentStr;
		} catch (IOException e) {
			e.printStackTrace();
			return "unknown";
		}
	}

	// Modify for add front camera information by lizhaobo 20151120 start
	private String getFrontCameraManufacturer() {
		String procCurrentStr;

		try {
			BufferedReader reader = new BufferedReader(
					new FileReader("/sys/devices/soc.0/1b0c000.qcom,cci/1.qcom,eeprom/sub_camera_name"), 256);
			try {
				procCurrentStr = reader.readLine();
			} finally {
				reader.close();
			}
			return procCurrentStr;
		} catch (IOException e) {
			e.printStackTrace();
			return "unknown";
		}
	}
	// Modify for add front camera information by lizhaobo 20151120 end



    private String getROMSize() {
        String procCurrentStr;

		try {
			BufferedReader reader = new BufferedReader(
					new FileReader("/sys/class/mmc_host/mmc0/mmc0:0001/block/mmcblk0/size"), 256);
			try {
				procCurrentStr = reader.readLine();
			} finally {
				reader.close();
			}
			Log.e("Version_getROMSize", "getROMSize()  = [" + procCurrentStr + "]");

            int size_ROM = Integer.parseInt(procCurrentStr);

            if (size_ROM >= 60000000) {
                return "32G";
            } else if (size_ROM <= 31000000) {
                return "16G";
            }
            return "unknown";
        } catch (Exception e) {
            Log.e("Version_getROMSize", "getROMSize() has Exception   = [" + e.toString() + "]");
            return "unknown";
        }
    }

    private String getSECVersion() {
        String procCurrentStr;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sys/devices/soc0/secure_boot_version"), 256);
            try {
                procCurrentStr = reader.readLine();
            } finally {
                reader.close();
            }
            Log.e("Version_getSECVersion", "getSECVersion()  = [" + procCurrentStr + "]");

            int isSec = Integer.parseInt(procCurrentStr);

            if (isSec == 1) {
                return "-sec";
            } else if (isSec == 0) {
                return "";
            }
            return "";
        } catch (Exception e) {
            Log.e("Version_getSECVersion", "getSECVersion() has Exception   = [" + e.toString() + "]");
            return "";
        }
    }

    private String getROMSupplier() {
        String procCurrentStr;

		try {
			BufferedReader reader = new BufferedReader(new FileReader("/sys/class/mmc_host/mmc0/mmc0:0001/manfid"),
					256);
			try {
				procCurrentStr = reader.readLine();
			} finally {
				reader.close();
			}
			Log.e("Version_getROMSupplier", "getROMSupplier()  = [" + procCurrentStr + "]");
			if ("0x000015".equals(procCurrentStr)) {
				return "Samsung";
			} else if ("0x000090".equals(procCurrentStr)) {
				return "Hynix";
			}
			return "unknown";
		} catch (IOException e) {
			Log.e("Version_getROMSupplier", "getROMSupplier() has Exception   = [" + e.toString() + "]");
			return "unknown";
		}
	}

	private String getSerialNumber() {
		String serial = SystemProperties.get("ro.serialno", "unknow");
		return serial;
	}

	private String getSoftwareVersion2() {
		return android.os.Build.DISPLAY;
	}
	
	// add by dgy 20170223
	private void parseNv2499(byte[] data){
		try {
			String nv2499 = new String(data, "UTF-8");
			if(30<=data.length){
				byte[] nullStr = new byte[1];
				nullStr[0] = (byte)0;
				nv2499 = nv2499.substring(0,30);
				nv2499 = nv2499.replace(new String(nullStr, "UTF-8"), "U");
				CITTestHelper application = (CITTestHelper)getApplication();
				application.setNv2499String(nv2499);
				SystemProperties.set("gsm.sim.data3", nv2499.substring(0,30) );
				Log.d(TAG, "processUnsolOemhookResponse  send factory_data_3 to ap=: " + nv2499);
			}
		} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		}
	}
	// add end

}
