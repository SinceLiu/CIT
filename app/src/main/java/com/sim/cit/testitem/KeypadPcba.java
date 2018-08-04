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
//Modify for CIT optimization by xiasiping 20140730 start
import java.util.List;
import com.sim.cit.MyXml;
import com.sim.cit.XNode;
import com.sim.cit.XProperty;
import com.sim.cit.XMethod;
import com.sim.cit.MyXmlUtils;
import android.widget.Toast;
import java.lang.reflect.Method;
//Modify for CIT optimization by xiasiping 20140730 end

public class KeypadPcba extends TestActivity {
    private Button btnPass;
    private List<TextView> keyList;
    private List<String> supportKeyList;
    private Context context;
    private CITTestHelper application;
    private int count;
    //Modify for CIT optimization by xiasiping 20140730 start
    private static MyXml mxml;
    private List<XProperty> xProperties;
    private List<XMethod> xMethods;
    private List<XNode> xNodes;
    private String button_backlight_node = null;
    private static final String TAG = "Keypad";
    //Modify for CIT optimization by xiasiping 20140730 end
    //Modify for PA568 keypad test by xiasiping 20140807 start
    //private int xcount = 0;
    //Modify for PA568 keypad test by xiasiping 20140807 end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        layoutId = R.layout.keypad;
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        application = (CITTestHelper)getApplication();
        supportKeyList = application.getKeypadList();

        btnPass = super.btnPass;
        keyList = new ArrayList<TextView>();
        keyList.add((TextView)findViewById(R.id.txt_one));
        keyList.add((TextView)findViewById(R.id.txt_two));
        keyList.add((TextView)findViewById(R.id.txt_three));
        keyList.add((TextView)findViewById(R.id.txt_four));
        keyList.add((TextView)findViewById(R.id.txt_five));
        keyList.add((TextView)findViewById(R.id.txt_six));
        keyList.add((TextView)findViewById(R.id.txt_seven));
        keyList.add((TextView)findViewById(R.id.txt_eight));
        keyList.add((TextView)findViewById(R.id.txt_nine));
        keyList.add((TextView)findViewById(R.id.txt_ten));

        btnPass.setEnabled(false);
        for (int i = 0; i < keyList.size(); i++) {
            TextView view = keyList.get(i);
            if (i < (supportKeyList.size() - 1)) {
                view.setText(supportKeyList.get(i));
            }else{
                view.setVisibility(View.GONE);
            }
        }

        registerReceiver(homeKeyReceiver, new IntentFilter("android.sim.home_key_for_cit"));
        //Modify for CIT optimization by xiasiping 20140730 start
        if (button_backlight_node != null) {
            Log.e(TAG,"xsp_button_backlight_node = " + button_backlight_node);
            CommonDrive.buttonlightControl_d(1, button_backlight_node);            
        } else {
            Log.e(TAG,"xsp_button_backlight_node is null ");
            CommonDrive.buttonlightControl(1);            
        }
        //CommonDrive.buttonlightControl(1);
        //Modify for CIT optimization by xiasiping 20140730 end
        Toast.makeText(getApplicationContext(), R.string.keypad_lightison, 1000).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(homeKeyReceiver);
        //Modify for CIT optimization by xiasiping 20140730 start
        if (button_backlight_node != null) {
            Log.e(TAG,"xsp_button_backlight_node = " + button_backlight_node);
            CommonDrive.buttonlightControl_d(0, button_backlight_node);            
        } else {
            Log.e(TAG,"xsp_button_backlight_node is null ");
            CommonDrive.buttonlightControl(0);            
        }
        //CommonDrive.buttonlightControl(0);
        //Modify for CIT optimization by xiasiping 20140730 end
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("Keypad","onKeyDown keycode is " +keyCode);
        switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            changeColor("down");
            break;
        //Modify for PA568 keypad test by xiasiping 20140807 start
        case KeyEvent.KEYCODE_VOLUME_UP:
            changeColor("up");
            break;
        /*case KeyEvent.KEYCODE_MENU:
            changeColor("menu");
            break;
        case KeyEvent.KEYCODE_BACK:
            changeColor("back");
            break;
        case KeyEvent.KEYCODE_PTT:
            changeColor("ptt");
            break;
        case KeyEvent.KEYCODE_POKING:
            if (xcount == 0) {
                Log.e("KEYPAD","Key_POKING_onClick");
                changeColor("poking");
                xcount++;
            } else {
                Log.e("KEYPAD","Key_POKING_onClick");
            }
            break;
        case KeyEvent.KEYCODE_SOS:
            changeColor("sos");
            break;*/
        case KeyEvent.KEYCODE_HEADSETHOOK:
            changeColor("headset");
            break;
        case KeyEvent.KEYCODE_HOME:
            changeColor("home");
            break;
        //Modify for PA568 keypad test by xiasiping 20140807 end
        case KeyEvent.KEYCODE_POWER:
            changeColor("power");
            break;
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
        //Modify for PA568 keypad test by xiasiping 20140807 start
        Log.i("Keypad","onKeyUp keycode is " +keyCode);
        /*if (keyCode ==KeyEvent.KEYCODE_POKING && xcount == 1){
            Log.e("ToT","Key_POKING_onClick");
            changeColor("poking");
            xcount = 0;
        } else {
            Log.e("ToT","Key_POKING_onClick");
        }*/
        //Modify for PA568 keypad test by xiasiping 20140807 end
        return true;
    }

    private void changeColor(String keyName){
        for (int i = 0; i < supportKeyList.size()-1; i++) {
            if (keyName.equals(supportKeyList.get(i))) {
                TextView view = keyList.get(i);
                if (view.getTag() == null) {
                    view.setTag(true);
                    count++;
                    if (count == supportKeyList.size()-1) {
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
    }

    private BroadcastReceiver homeKeyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            changeColor("home");
        }
    };
}
