package com.sim.cit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;

import com.readboy.util.FileOperation;

import java.io.IOException;
import java.io.InputStream;

import com.sim.cit.testitem.BackCamera;
import com.sim.cit.testitem.BlueToothService;
import com.sim.cit.testitem.Bluetooth;
import com.sim.cit.testitem.ClearDataActivity;
import com.sim.cit.testitem.Colligate;
import com.sim.cit.testitem.FrontCamera;
import com.sim.cit.testitem.HeadsetLoopback;
import com.sim.cit.testitem.KeyPadHome;
import com.sim.cit.testitem.Keypad;
import com.sim.cit.testitem.Lcd;
import com.sim.cit.testitem.LightSensor;
import com.sim.cit.testitem.MotionSensor;
import com.sim.cit.testitem.PSensor;
import com.sim.cit.testitem.SecondMic;
import com.sim.cit.testitem.TFCard;
import com.sim.cit.testitem.TouchForG600;
import com.sim.cit.testitem.UDisk;
import com.sim.cit.testitem.Version;
import com.sim.cit.testitem.Receiver;
import com.sim.cit.testitem.UsbMode;
import com.sim.cit.testitem.SpeakerPinknoiseTest;
import com.sim.cit.testitem.ReceiverPinknoiseTest;
import com.sim.cit.testitem.PsensorValue;
import com.sim.cit.testitem.Wlan;
import com.sim.cit.testitem.WlanService;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
//add for mark version test results by songguangyu 20140220 start
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.List;
//add for mark version test results by songguangyu 20140220 end
//Modify for add readconfig in CIT by xiasiping 20140819 start
import android.content.ComponentName;
//Modify for add readconfig in CIT by xiasiping 20140819 end
import android.content.BroadcastReceiver;
import android.widget.Toast;
import android.util.Log;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class MainList extends ListActivity {
    private static final String LOG_TAG = "CIT_MainList";
    private static final int PCB = 0;
    //        private static final int SUBPCB = PCB + 1;
    private static final int SUBPCB = PCB;
    //private static final int COMPLETE = SUBPCB + 1;
//        private static final int COMPLETE = SUBPCB + 1;
    //private static final int ALL_ITEM = COMPLETE + 1;
    //private static final int VERSION = ALL_ITEM + 1;
    private static final int VERSION = SUBPCB + 1;
    //private static final int TDTEST = READ_CONFIG + 1;
    //private static final int AGING_TEST = TDTEST + 1;
    private static final int AGING_TEST = VERSION + 1;
    private static final int USBMODE = AGING_TEST + 1;
    //Modify for add readconfig in CIT by xiasiping 20140819 start
    //Modify for add readconfig in CIT by xiasiping 20140819 end
    private static final int SPEAKER_PINKNOISE = USBMODE + 1;
    private static final int RECEIVER_PINKNOISE = SPEAKER_PINKNOISE + 1;
    private static final int CHECK_P_SENSOR = RECEIVER_PINKNOISE + 1;
    private static final int READ_CONFIG = CHECK_P_SENSOR + 1;
    private GridView gridView;
    private Button completeAutoTest;
    //add for take out SUBPCB test when version is IS by songguangyu 20140211 start
    private boolean isISVersion = false;
    //add for take out SUBPCB test when version is IS by songguangyu 20140211 end
    private Context context;
    //add for mark version test results by songguangyu 20140220 start
    private CITTestHelper application;
    private ArrayList<String> mainList;
    private SimpleAdapter listItemAdapter;
    private ArrayList<HashMap<String, Object>> listItems;
    //add for mark version test results by songguangyu 20140220 end
    private boolean mIsCharging = false;

    private int oldBrightValue;

    private byte up_times = 0;
    private byte down_times = 0;

    private boolean twice_up = false;
    private boolean twice_down = false;
    private boolean single_up = false;
    private boolean single_down = false;

    private Boolean isStartedBySdCard;
    private List<TestItem> list = new ArrayList<TestItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableHomeKey(true);
//        try{
//	        Log.i("adsd",""+new File(Environment.getExternalStorageDirectory().toString()+File.separatorChar+"data.txt").createNewFile());
//	        Log.i("adsd",""+new File("storage/emulated/legacy/data.txt").createNewFile());
//        }catch(Exception e){
//        	Log.i("adsd","create fail:"+ e.toString());
//        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        application = (CITTestHelper) getApplication();
        isStartedBySdCard = application.isStartedBySdCard();
        //add for test wlan and blueTooth when app starts by lxx 20180727 start
        Intent startWlanService = new Intent(MainList.this, WlanService.class);
        startService(startWlanService);
        Intent startBlueToothService = new Intent(MainList.this, BlueToothService.class);
        startService(startBlueToothService);
        //add for test wlan and blueTooth when app starts by lxx 20180727 end
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = getApplicationContext();
        //20121205 modified for change the view by lvhongshan start
        //modify for mark version test results by songguangyu 20140220 start
        mainList = new ArrayList<String>();
        //modify for mark version test results by songguangyu 20140220 end
        mainList.add(getString(R.string.title_pcba_auto));
        //modify for take out SUBPCB test when version is IS by songguangyu 20140211 start
        /*if(CommonDrive.getHWSubType().replace('\n',' ').contains("IS")){
            isISVersion = true;
        }else{
            isISVersion = false;*/
//            mainList.add(getString(R.string.subpcb));
        //}
        //modify for take out SUBPCB test when version is IS by songguangyu 20140211 end
//    mainList.add(getString(R.string.complete));
        //mainList.add("All Item");
        mainList.add("VERSION");
        //mainList.add("RECEIVER TEST");
        //20121205 modified for change the view by lvhongshan end
        //modify for mark version test results by songguangyu 20140220 start
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mainList);
        getListView().setAdapter(adapter);*/
        //modify for mark version test results by songguangyu 20140220 end
        //Modify for add readconfig in CIT by xiasiping 20140819 start
        //mainList.add(getString(R.string.readconfig_name));
        //Modify for add readconfig in CIT by xiasiping 20140819 end
        //mainList.add("TDTest");
        mainList.add("Run-In-Test");
        mainList.add("USB-MODE");
        mainList.add("Speaker Pinknoise Test");
        mainList.add("Receiver Pinknoise Test");

        if (isStartedBySdCard) {
            application.removeSdCardTest();
        }
        application.setTestMode(CITTestHelper.MAIN_CIT);
        application.initTestList(false);
        List<String> titleList = new ArrayList<String>();
        List<String> classNameList = new ArrayList<String>();
        titleList = application.getTitleList();
        classNameList = application.getClassNameList();
        Log.i("MainList","size ："+application.getTitleList().size() + "       "+application.getTitleList().toString());
        if (titleList != null) {
            for (int index = 0; index < titleList.size(); index++) {
                list.add(new TestItem(titleList.get(index), classNameList.get(index), 0));
            }
        }
        try {
            oldBrightValue = Settings.System.getInt(MainList.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        Settings.System.putInt(MainList.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE - 1);
        //add for show complete test by gridView by lxx 20180726
        gridView = (GridView) findViewById(R.id.complete_list);
        completeAutoTest = (Button) findViewById(R.id.complete_auto_test);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                application.startTestActivity(MainList.this, position);
            }
        });

        completeAutoTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application.setAutoTest(true);
                application.startTestActivity(MainList.this, 0);
            }
        });
    }

    //add for mark version test results by songguangyu 20140220 start
    @Override
    protected void onResume() {
        if (isStartedBySdCard) {
            application.removeSdCardTest();
        }
        application.setTestMode(CITTestHelper.MAIN_CIT);
        application.initTestList(false);
        initListView();
        this.setListAdapter(listItemAdapter);
        registerReceiver(mBatteryChangeReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mBatteryChangeReceiver);
        super.onPause();
    }

    private final int NORESULTITEM = 6;

    private void initListView() {
        listItems = new ArrayList<HashMap<String, Object>>();
        List<TestItem> resultLists =
                application.getTestResultMaps().get(application.getTestMode());
        for (int i = 0; i < mainList.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemTitle", mainList.get(i));
            if (mainList.get(i).equals("VERSION")) {
                switch (application.getVersionTestResult()) {
                    case CITTestHelper.TEST_RESULT_PASS:
                        map.put("ItemImage", R.drawable.test_pass);
                        break;
                    case CITTestHelper.TEST_RESULT_FAIL:
                        map.put("ItemImage", R.drawable.test_fail);
                        break;
                    default:
                        map.put("ItemImage", null);
                        break;
                }
            } else if (mainList.get(i).equals("Speaker Pinknoise Test")) {
                switch (application.getSpeakerPinknoiseTestResult()) {
                    case CITTestHelper.TEST_RESULT_PASS:
                        map.put("ItemImage", R.drawable.test_pass);
                        break;
                    case CITTestHelper.TEST_RESULT_FAIL:
                        map.put("ItemImage", R.drawable.test_fail);
                        break;
                    default:
                        map.put("ItemImage", null);
                        break;
                }
            } else if (mainList.get(i).equals("Receiver Pinknoise Test")) {
                switch (application.getReceiverPinknoiseTestResult()) {
                    case CITTestHelper.TEST_RESULT_PASS:
                        map.put("ItemImage", R.drawable.test_pass);
                        break;
                    case CITTestHelper.TEST_RESULT_FAIL:
                        map.put("ItemImage", R.drawable.test_fail);
                        break;
                    default:
                        map.put("ItemImage", null);
                        break;
                }
            } else {
                map.put("ItemImage", null);
            }
            listItems.add(map);
        }

        listItemAdapter = new SimpleAdapter(this, listItems,
                R.layout.test_list_item,
                new String[]{"ItemTitle", "ItemImage"},
                new int[]{R.id.ItemTitle, R.id.ItemImage});
        for (int i = 0; i < list.size(); i++) {
            switch (resultLists.get(i).getTestResult()) {
                case CITTestHelper.TEST_RESULT_PASS:
                    list.get(i).setTextColor(Color.parseColor("#00ff00"));
                    break;
                case CITTestHelper.TEST_RESULT_FAIL:
                    list.get(i).setTextColor(Color.parseColor("#ff0000"));
                    break;
                default:
                    list.get(i).setTextColor(Color.parseColor("#333333"));
                    break;
            }
        }
        CompleteTestAdapter completeTestAdapter = new CompleteTestAdapter(MainList.this, list);
        gridView.setAdapter(completeTestAdapter);
    }
    //add for mark version test results by songguangyu 20140220 end

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = null;
        //add for take out SUBPCB test when version is IS by songguangyu 20140211 start
                /*if(isISVersion && position>0){
                    ++position;
                }*/
        //add for take out SUBPCB test when version is IS by songguangyu 20140211 end
        switch (position) {
            case PCB:
//            i = new Intent(context, FirstList.class);
//            i.putExtra(CITTestHelper.EXTRA_KEY_TEST_TYPE, CITTestHelper.EXTRA_VALUE_TEST_TYPE_PCB);

                i = new Intent(context, SecondList.class);
                i.putExtra(CITTestHelper.EXTRA_KEY_TEST_TYPE, CITTestHelper.EXTRA_VALUE_TEST_TYPE_PCB);
                i.putExtra(CITTestHelper.EXTRA_KEY_TO_TESTLIST, CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT1);
                break;
//        case SUBPCB:
//            // modified by xushuang at 20150509  BEGIN
//            i = new Intent(context, SecondList.class);
//            i.putExtra(CITTestHelper.EXTRA_KEY_TEST_TYPE, CITTestHelper.EXTRA_VALUE_TEST_TYPE_SUBPCB);
//            i.putExtra(CITTestHelper.EXTRA_KEY_TO_TESTLIST, CITTestHelper.EXTRA_VALUE_TO_FIRSTLIST_CIT1);
//            // modified by xushuang at 20150509  END
//            break;
//        case COMPLETE:
//            i = new Intent(context, FirstList.class);
//            i.putExtra(CITTestHelper.EXTRA_KEY_TEST_TYPE, CITTestHelper.EXTRA_VALUE_TEST_TYPE_COMPLETE);
//            break;
//      case ALL_ITEM:
//          i = new Intent(context, AllTest.class);
//          break;
            case VERSION:
                i = new Intent(context, Version.class);
                //add for mark test results by songguangyu 20140220 start
                i.putExtra(CITTestHelper.EXTRA_TEST_TYPE_VERSION, CITTestHelper.EXTRA_TEST_TYPE_VERSION);
                //add for mark test results by songguangyu 20140220 end
                break;
            case USBMODE:
                i = new Intent(context, UsbMode.class);
                break;
            case SPEAKER_PINKNOISE:
                i = new Intent(context, SpeakerPinknoiseTest.class);
                i.putExtra(CITTestHelper.EXTRA_TEST_TYPE_SPEAKER_PINKNOISE_TEST, CITTestHelper.EXTRA_TEST_TYPE_SPEAKER_PINKNOISE_TEST);
                break;
            case RECEIVER_PINKNOISE:
                i = new Intent(context, ReceiverPinknoiseTest.class);
                i.putExtra(CITTestHelper.EXTRA_TEST_TYPE_RECEIVER_PINKNOISE_TEST, CITTestHelper.EXTRA_TEST_TYPE_RECEIVER_PINKNOISE_TEST);
                break;
            //case RECEIVER:
            //    i = new Intent(context, Receiver.class);
            //    break;
            //Modify for add readconfig in CIT by xiasiping 20140819 start
//        case READ_CONFIG:
//                i = new Intent();
//                ComponentName readconfig = new ComponentName(
//                    "com.android.sim.readconfig", "com.android.sim.readconfig.MainActivity");
//                i.setComponent(readconfig);
//                break;
            //Modify for add readconfig in CIT by xiasiping 20140819 end
        /*case TDTEST:
                i = new Intent();
                ComponentName tdtest = new ComponentName(
                    "com.sim.tdtest", "com.sim.tdtest.TDTestActivity");
                i.setComponent(tdtest);
                break;*/
            //Modify for add readconfig in CIT by xiasiping 20140819 start
            case AGING_TEST:
                if (mIsCharging) {
                    ComponentName agingtest = new ComponentName(
                            "com.dream.agingtest", "com.dream.agingtest.MainActivity");
                    i = new Intent();
                    i.putExtra("AGING_CIT", true);
                    i.setComponent(agingtest);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.please_input_charger, 2000).show();
                    i = null;
                }
                break;
//        case CHECK_P_SENSOR:
//                i = new Intent(context, PsensorValue.class);
//                break;

            //Modify for add readconfig in CIT by xiasiping 20140819 end
            default:
                int classNameListIndex = position - NORESULTITEM;
                application.startTestActivity(this, classNameListIndex);
                break;
        }
        if (i != null) {
            startActivity(i);
        }
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(LOG_TAG, "*****MainList.onKeyDown  BEGIN*****");
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                up_times++;
                if (up_times <= 2) {
                    if (up_times == 2)
                        twice_up = true;
                    return true;
                }
                if (twice_up && twice_down) {
                    single_up = true;
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                down_times++;
                if (twice_up && down_times <= 2) {
                    if (down_times == 2)
                        twice_down = true;
                    return true;
                }
                if (single_up) {
                    Intent i = new Intent(context, UsbMode.class);
                    startActivity(i);
                }
                break;
        }

        up_times = 0;
        down_times = 0;
        twice_down = false;
        twice_up = false;
        single_up = false;
        single_down = false;
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(MainList.this).setTitle(
                    R.string.alert_title).setMessage(
                    R.string.alert_content).setPositiveButton(
                    R.string.alert_dialog_ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            finish();
                            enableHomeKey(false);
                        }
                    }).setNegativeButton(R.string.alert_dialog_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {

                        }
                    }).create().show();
            return true;

        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    public void enableHomeKey(boolean enable) {
        Intent homeKeyIntent = new Intent("android.sim.home_key_for_useless");
        homeKeyIntent.putExtra("OnOrOff", enable);
        sendBroadcast(homeKeyIntent);
    }

    private BroadcastReceiver mBatteryChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "*****mBatteryChangeReceiver.onReceive  BEGIN*****");
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                Log.i(LOG_TAG, "Intent.ACTION_BATTERY_CHANGED");
                int plugType = intent.getIntExtra("plugged", 0);
                Log.i(LOG_TAG, "plugType = " + plugType);
                int status = intent.getIntExtra("status",
                        BatteryManager.BATTERY_STATUS_UNKNOWN);
                Log.i(LOG_TAG, "status = " + status);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    Log.i(LOG_TAG, "BatteryManager.BATTERY_STATUS_CHARGING");
                    mIsCharging = true;
                } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                    Log.i(LOG_TAG, "BatteryManager.BATTERY_STATUS_FULL");
                    mIsCharging = true;
                } else {
                    mIsCharging = false;
                }
            }
            Log.i(LOG_TAG, "*****mBatteryChangeReceiver.onReceive  END*****");
        }
    };

    @Override
    protected void onDestroy() {
        Settings.System.putInt(MainList.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, oldBrightValue);
        super.onDestroy();
        Log.i("MainList", "onDestroy()");
        if(application.isStartedBySdCard()){
            application.setStartedBySdCard(false);
        }
        application.fillAllList();//get again to recover SdCard test
        Intent stopWlanService = new Intent(MainList.this, WlanService.class);
        stopService(stopWlanService);
        Intent stopBlueToothService = new Intent(MainList.this, BlueToothService.class);
        stopService(stopBlueToothService);
    }

}
