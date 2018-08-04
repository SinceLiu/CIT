package com.sim.cit.testitem;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.sim.cit.CITTestHelper;
import com.sim.cit.R;
import com.sim.cit.CommonDrive;
import com.sim.cit.TestActivity;

import java.util.List;
import com.sim.cit.MyXml;
import com.sim.cit.XNode;
import com.sim.cit.XProperty;
import com.sim.cit.XMethod;
import com.sim.cit.MyXmlUtils;
import android.widget.Toast;
import java.lang.reflect.Method;

public class KeyLightHome extends TestActivity {
    private Button btnPass;
    //private List<TextView> keyList;
    //private List<String> supportKeyList;
    private TextView keyhome;
    private Context context;
    private CITTestHelper application;
    private int count;

    private static MyXml mxml;
        
    private List<XProperty> xProperties;
    private List<XMethod> xMethods;
    private List<XNode> xNodes;
    private String button_backlight_node = null;
    private static final String TAG = "Keypad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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

        layoutId = R.layout.keylighthome;
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        application = (CITTestHelper)getApplication();
        //supportKeyList = application.getKeypadList();

        btnPass = super.btnPass;
        /*keyList = new ArrayList<TextView>();
        keyList.add((TextView)findViewById(R.id.txt_one));*/
        keyhome = (TextView)findViewById(R.id.txt_one);


        btnPass.setEnabled(false);
        /*for (int i = 0; i < keyList.size(); i++) {
            TextView view = keyList.get(i);
            if (i < supportKeyList.size()) {
                view.setText(supportKeyList.get(i));
            }else{
                view.setVisibility(View.GONE);
            }
        }*/

        registerReceiver(homeKeyReceiver, new IntentFilter("android.sim.home_key_for_cit"));

        if (button_backlight_node != null) {
            Log.e(TAG,"xsp_button_backlight_node = " + button_backlight_node);
            CommonDrive.buttonlightControl_d(1, button_backlight_node);            
        } else {
            Log.e(TAG,"xsp_button_backlight_node is null ");
            CommonDrive.buttonlightControl(1);            
        }
        //CommonDrive.buttonlightControl(1);
        Toast.makeText(getApplicationContext(), R.string.keypad_lightison, 1000).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(homeKeyReceiver);
        if (button_backlight_node != null) {
            Log.e(TAG,"xsp_button_backlight_node = " + button_backlight_node);
            CommonDrive.buttonlightControl_d(0, button_backlight_node);            
        } else {
            Log.e(TAG,"xsp_button_backlight_node is null ");
            CommonDrive.buttonlightControl(0);            
        }
        //CommonDrive.buttonlightControl(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("Keypad","onKeyDown keycode is " +keyCode);
        switch (keyCode) {
        /*case KeyEvent.KEYCODE_VOLUME_DOWN:
            changeColor("down");
            break;
        case KeyEvent.KEYCODE_VOLUME_UP:
            changeColor("up");
            break;
        case KeyEvent.KEYCODE_MENU:
            changeColor("menu");
            break;
        case KeyEvent.KEYCODE_BACK:
            changeColor("back");
            break;*/
        /*case KeyEvent.KEYCODE_PTT:
            changeColor("ptt");
            break;*/
        /*case KeyEvent.KEYCODE_SOS:
            changeColor("sos");
            break;*/
        /*case KeyEvent.KEYCODE_HEADSETHOOK:
            changeColor("headset");
            break;*/
        case KeyEvent.KEYCODE_HOME:
            /*changeColor("home");*/
            if (keyhome.getTag() == null) {
                keyhome.setTag(true);
                btnPass.setEnabled(true);
            }
            if ((Boolean)keyhome.getTag()) {
                keyhome.setBackgroundResource(R.color.green);
                keyhome.setTag(false);
            }else{
                keyhome.setBackgroundResource(R.color.gray);
                keyhome.setTag(true);
            }
            break;
        /*case KeyEvent.KEYCODE_B:
            Log.e("ToT","Key_Reset_onClick");
            changeColor("reset");
            break;*/
        /*case KeyEvent.KEYCODE_POWER:
            changeColor("power");
            break;*/
  /*    case KeyEvent.KEYCODE_ALARM:
            changeColor("home");
            break;*/
        //case KeyEvent.KEYCODE_A:
        //case 224:
        /*case KeyEvent.KEYCODE_SOS:
            changeColor("sos");
            break;*/
        default:
            break;
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return true;
    }

    /*private void changeColor(String keyName){
        for (int i = 0; i < supportKeyList.size(); i++) {
            if (keyName.equals(supportKeyList.get(i))) {
                TextView view = keyList.get(i);
                if (view.getTag() == null) {
                    view.setTag(true);
                    count++;
                    if (count == supportKeyList.size()) {
                        btnPass.setEnabled(true);
                    }
                }
                if ((Boolean)view.getTag()) {
                    view.setBackgroundResource(R.color.green);
                    view.setTag(false);
                }else{
                    view.setBackgroundResource(R.color.gray);
                    view.setTag(true);
                }
                break;
            }
        }
    }*/

    private BroadcastReceiver homeKeyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //changeColor("home");
            if (keyhome.getTag() == null) {
                keyhome.setTag(true);
                btnPass.setEnabled(true);
            }
            if ((Boolean)keyhome.getTag()) {
                keyhome.setBackgroundResource(R.color.green);
                keyhome.setTag(false);
            }else{
                keyhome.setBackgroundResource(R.color.gray);
                keyhome.setTag(true);
            }
        }
    };
}
