package com.sim.cit.testitem;

import java.io.File;
import android.app.Activity;
import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.UserManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import com.sim.cit.CITTestHelper;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.os.storage.StorageManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.hardware.usb.UsbManager;
import android.os.UserManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.View;
import android.view.WindowManager;
/*
 *from the "T8G" transplant calculator input e^010101^e function by wangkai 20141110
 *add for calculator input "e^010101^e" start port switch by songguangyu 20140426
 */
public class UsbMode extends Activity {

    private static final String TAG = "UsbMode";
    private static final String MASS_ADB = "diag,serial_smd,rmnet_bam,adb";
    private static final String MASS = "diag,serial_smd,rmnet_bam";
    //add for port switch by songguangyu 20140429 start
    private static final String MTP_ADB = "mtp,adb";
    private static final String MTP = "mtp";
    private String function = "none";
    //add for port switch by songguangyu 20140429 end
    private static RadioGroup raGroup1;
    private static RadioButton but_portOn;
    private static RadioButton but_portOff;
    //private static TextView tv_curState;

    private UsbManager mUsbManager;
    private boolean mUsbAccessoryMode;
    private boolean isadbmode = false;
    String currentFunction = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.usbmode);
        currentFunction = SystemProperties.get("sys.usb.config", "000");
        Log.i(TAG, "currentFunction is " +currentFunction);
        if (currentFunction != null && currentFunction.endsWith("adb")){
            Log.i(TAG, "currentFunction.endWith() is " + currentFunction.endsWith("adb"));
            isadbmode = true;
        }
        mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
        raGroup1 = (RadioGroup)findViewById(R.id.radioGroup2);
        but_portOn = (RadioButton)findViewById(R.id.but_portOn);
        but_portOff = (RadioButton)findViewById(R.id.but_portOff);
        //tv_curState = (TextView)findViewById(R.id.tv_curState);
        //tv_curState.setText(currentFunction);
        //modify for port switch by songguangyu 20140429 start
        if (MASS.equals(currentFunction)||MASS_ADB.equals(currentFunction)) {
            but_portOn.setChecked(true);
            but_portOff.setChecked(false);
        } else {
            but_portOn.setChecked(false);
            but_portOff.setChecked(true);
        }
        //modify for port switch by songguangyu 20140429 end

        but_portOn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isadbmode){
                    SystemProperties.set("sys.usb.config", MASS_ADB);
                }else {
                    SystemProperties.set("sys.usb.config", MASS);
                }
                //tv_curState.setText(SystemProperties.get("sys.usb.config", "000"));
                but_portOn.setChecked(true);
                but_portOff.setChecked(false);
                //add for port switch by songguangyu 20140429 start
                function = MASS;
                mUsbManager.setCurrentFunction(function/*, true*/);
                //add for port switch by songguangyu 20140429 end
            }
        });

        but_portOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //modify for port switch by songguangyu 20140429 start
                if (isadbmode){
                    SystemProperties.set("sys.usb.config", MTP_ADB);
                    function = UsbManager.USB_FUNCTION_MTP;
                }else {
                    SystemProperties.set("sys.usb.config", MTP);
                    function = UsbManager.USB_FUNCTION_MTP;
                }
                //SystemProperties.set("sys.usb.config", currentFunction);
                //tv_curState.setText(SystemProperties.get("sys.usb.config", "000"));
                but_portOn.setChecked(false);
                but_portOff.setChecked(true);
                mUsbManager.setCurrentFunction(function/*, true*/);
                //modify for port switch by songguangyu 20140429 end
            }
        });
    }
    @Override
    protected void onResume() {
        android.util.Log.d("xsp", "change USB mode as same B8G by xsp!~~");
        super.onResume();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
