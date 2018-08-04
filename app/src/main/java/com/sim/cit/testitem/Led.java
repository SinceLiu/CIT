package com.sim.cit.testitem;

import java.io.File;

import android.app.Service;
import android.net.Uri;
import android.os.Bundle;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sim.cit.CITTestHelper;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.os.storage.StorageManager;
//Modify for CIT optimization by xiasiping 20140730 start
import java.util.List;
import com.sim.cit.MyXml;
import com.sim.cit.XNode;
import com.sim.cit.XProperty;
import com.sim.cit.XMethod;
import com.sim.cit.MyXmlUtils;
import android.widget.Toast;
//Modify for CIT optimization by xiasiping 20140730 end

public class Led extends TestActivity {
private static final String TAG = "CIT_Led";

    private TextView tvMessage;
    private boolean isTest=true;
    final int SUB_ID=1;
    private StorageManager mStorageManager;
    //Modify for CIT optimization by xiasiping 20140730 start
    private static MyXml mxml;
    private List<XProperty> xProperties;
    private List<XMethod> xMethods;
    private List<XNode> xNodes;
    private String led_r = null;
    private String led_o = null;
    private String led_g = null;
    //Modify for CIT optimization by xiasiping 20140730 end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt;
        super.onCreate(savedInstanceState);
        mStorageManager = StorageManager.from(this);
        tvMessage = (TextView)findViewById(R.id.txt_one);
        tvMessage.setText(getString(R.string.led_hint));
        //Modify for CIT optimization by xiasiping 20140730 start
        try {
            mxml = new MyXmlUtils().getMxml();
            xProperties = mxml.getXProperties();
            xMethods = mxml.getXMethods();
            xNodes = mxml.getXNodes();
        }catch (Exception e) {
            Log.e(TAG,"xsp_May be config file has error");
        }
        if (xNodes != null) {
            for (XNode xn : xNodes) {
                String name = xn.getName();
                if ("led_r".equals(name)) {
                    led_r = xn.getValue();
                }
                if ("led_o".equals(name)) {
                    led_o = xn.getValue();
                }
                if ("led_g".equals(name)) {
                    led_g = xn.getValue();
                }
            }
        }
        //Modify for CIT optimization by xiasiping 20140730 end

    }

    private void startLed(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 3; i++) {
                    if (!isTest) {
                        break;
                    }

                    if(CommonDrive.getHWSubType().replace('\n',' ').contains("IS")){
                        Log.i(TAG," is IS version");
                        CommonDrive.lightControlForIS(i);
                    } else {
                        //Modify for CIT optimization by xiasiping 20140730 start
                        if (led_r != null && led_o != null && led_g != null) {
                            Log.e(TAG,"xsp_leds = " + led_r + "," + led_o + "," + led_g );
                            CommonDrive.lightControl_d(i, led_r, led_o, led_g);
                        }else{
                            Log.e(TAG,"xsp_leds is null " + led_r + "," + led_o + "," + led_g );
                            CommonDrive.lightControl(i);
                        }
                        //CommonDrive.lightControl(i);
                        //Modify for CIT optimization by xiasiping 20140730 end
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(i == 2){
                        i = -1;
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isTest=false;
        //Modify for CIT optimization by xiasiping 20140730 start
        if (led_r != null && led_o != null && led_g != null) {
            Log.e(TAG,"xsp_leds = " + led_r + "," + led_o + "," + led_g );
            CommonDrive.lightControl_d(-1, led_r, led_o, led_g);
        }else{
            Log.e(TAG,"xsp_leds is null " + led_r + "," + led_o + "," + led_g );
            CommonDrive.lightControl(-1);
        }
        //CommonDrive.lightControl(-1);
        //Modify for CIT optimization by xiasiping 20140730 end
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (CITTestHelper.HAS_LED) {
            startLed();
        }
    }
}
