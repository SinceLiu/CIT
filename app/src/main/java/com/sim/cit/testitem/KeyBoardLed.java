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

public class KeyBoardLed extends TestActivity {
private static final String TAG = "CIT_KeyBoardLed";

    private TextView tvMessage;
    private boolean isTest=true;
    final int SUB_ID=1;
    private StorageManager mStorageManager;
    //Modify for CIT optimization by xiasiping 20140730 start
    private static MyXml mxml;
    private List<XProperty> xProperties;
    private List<XMethod> xMethods;
    private List<XNode> xNodes;
    private String button_backlight_node = null;
    //Modify for CIT optimization by xiasiping 20140730 end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt;
        super.onCreate(savedInstanceState);
        mStorageManager = StorageManager.from(this);
        tvMessage = (TextView)findViewById(R.id.txt_one);
        tvMessage.setText(getString(R.string.keyboardled));
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
                if ("button_backlight".equals(name)) {
                    button_backlight_node = xn.getValue();
                }
            }
        }
        //Modify for CIT optimization by xiasiping 20140730 end

    }

    private void startLed(){
        if (button_backlight_node != null) {
            Log.e(TAG,"xsp_button_backlight_node = " + button_backlight_node);
            CommonDrive.buttonlightControl_d(1, button_backlight_node);            
        } else {
            Log.e(TAG,"xsp_button_backlight_node is null ");
            CommonDrive.buttonlightControl(1);            
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (button_backlight_node != null) {
            Log.e(TAG,"xsp_button_backlight_node = " + button_backlight_node);
            CommonDrive.buttonlightControl_d(0, button_backlight_node);            
        } else {
            Log.e(TAG,"xsp_button_backlight_node is null ");
            CommonDrive.buttonlightControl(0);            
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (CITTestHelper.HAS_LED) {
            startLed();
        }
    }
}
