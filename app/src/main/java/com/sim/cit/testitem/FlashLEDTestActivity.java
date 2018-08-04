package com.sim.cit.testitem;

import java.io.FileOutputStream;
import java.io.IOException;

import com.sim.cit.R;
import com.sim.cit.TestActivity;

//import com.qualcomm.qcnvitems.QcNvItems;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.os.PowerManager;

public class FlashLEDTestActivity extends TestActivity {

    private static final String TAG = "flash_led_calibration";

    private int level_r = 15;
    private int level_g = 15;
    private int level_b = 15;
    //PowerManager pm;
	//private QcNvItems mNv;

    private Button btn_r_d;
    private Button btn_r_u;
    private Button btn_g_d;
    private Button btn_g_u;
    private Button btn_b_d;
    private Button btn_b_u;
    private Button btn_save;

    private TextView red_level;
    private TextView green_level;
    private TextView blue_level;


    /*private SeekBar red_bar;
    private SeekBar green_bar;
    private SeekBar blue_bar;

    private OnSeekBarChangeListener seekBar_lis = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            int i = arg0.getProgress();
            switch(arg0.getId()){
            case R.id.blue_progressbar:
                level_b = i;
                blue_level.setText("" + (level_b-30));
                // interface to set brightness blue
                pm.setLedLeftBrightness(level_b-30);
                break;
            case R.id.red_progressbar:
                level_r = i;
                red_level.setText("" + (level_r-30));
                // interface to set brightness red
                pm.setLedMidBrightness(level_r-30);
                break;
            case R.id.green_progressbar:
                level_g = i;
                green_level.setText("" + (level_g-30));
                // interface to set brightness green
                pm.setLedRightBrightness(level_g-30);
                break;
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onStopTrackingTouch(SeekBar arg0) {
            // TODO Auto-generated method stub
        }
    };*/

    private OnClickListener mListener = new OnClickListener() {
        public void onClick(View arg0) {
            switch (arg0.getId()) {
            case R.id.btn_r_d:
                android.util.Log.e("xsp_led_test", "onClick Red_down");
                level_r--;
                if (level_r <= 0) {
                    level_r = 0;
                }
                red_level.setText((level_r-15) + "");
                //pm.setLedMidBrightness((level_r-15)*4);
                break;
            case R.id.btn_r_u:
                android.util.Log.e("xsp_led_test", "onClick red_up");
                level_r++;
                if (level_r >= 30) {
                    level_r = 30;
                }
                red_level.setText((level_r-15) + "");
                //pm.setLedMidBrightness((level_r-15)*4);
                break;
            case R.id.btn_g_d:
                android.util.Log.e("xsp_led_test", "onClick green_down");
                level_g--;
                if (level_g <= 0) {
                    level_g = 0;
                }
                green_level.setText((level_g-15) + "");
                //pm.setLedRightBrightness((level_g-15)*4);
                break;
            case R.id.btn_g_u:
                android.util.Log.e("xsp_led_test", "onClick green_up");
                level_g++;
                if (level_g >= 30) {
                    level_g = 30;
                }
                green_level.setText((level_g-15) + "");
                //pm.setLedRightBrightness((level_g-15)*4);
                break;
            case R.id.btn_b_d:
                android.util.Log.e("xsp_led_test", "onClick blue_down");
                level_b--;
                if (level_b <= 0) {
                    level_b = 0;
                }
                blue_level.setText((level_b-15) + "");
                //pm.setLedLeftBrightness((level_b-15)*4);
                break;
            case R.id.btn_b_u:
                android.util.Log.e("xsp_led_test", "onClick blue_up");
                level_b++;
                if (level_b >= 30) {
                    level_b = 30;
                }
                blue_level.setText((level_b-15) + "");
                //pm.setLedLeftBrightness((level_b-15)*4);
                break;

            case R.id.btn_save_led:
                android.util.Log.e("xsp_led_test", "onClick save");
                byte[] values_save = {0,0,0};
                values_save[0] = (byte)(level_r*4);
                values_save[1] = (byte)(level_g*4);
                values_save[2] = (byte)(level_b*4);
                try {
                    //mNv.set_led_cal(values_save[0], values_save[1], values_save[2]);
                    android.util.Log.e("xsp_led_test", "save led value to NV has Successful!" + values_save[0]+","+ values_save[1]+","+ values_save[2]);
                    Toast.makeText(FlashLEDTestActivity.this, "save led value to NV has Successful!", 3000).show();
                } catch (Exception e) {
                    Toast.makeText(FlashLEDTestActivity.this, "save led value to NV has Exception!", 3000).show();
                    android.util.Log.e("xsp_led_test", "save led value to NV has Exception :[" + e.toString() + "]");
                }
                break;
            }
        }
    };

    /*private byte getByte(int id){
        byte result = 0;
        result = (byte)(id*6);
        return result;
    }

    private void set_led(byte[] values, String path) {
        try{
            fosFL = new FileOutputStream(path);

            fosFL.write(values);

            fosFL.flush();

            fosFL.close();
            android.util.Log.e("xsp_led_test", path +" write(On)");
        } catch (Exception e) {
            android.util.Log.e("xsp_led_test", "OPEN [" + path + "] LED failed!" + e.toString());            
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId=R.layout.fltest;
    	super.onCreate(savedInstanceState);
        android.util.Log.e("xsp_led_test", "onCreate()");
        //mNv = new QcNvItems(getApplicationContext());
        //pm= (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        bindView();

    } 

    private void bindView() {

        red_level = (TextView)findViewById(R.id.txt_r_value);
        green_level = (TextView)findViewById(R.id.txt_g_value);
        blue_level = (TextView)findViewById(R.id.txt_b_value);

        red_level.setText("" + 0);
        green_level.setText("" + 0);
        blue_level.setText("" + 0);

        btn_r_d = (Button)findViewById(R.id.btn_r_d);
        btn_r_u = (Button)findViewById(R.id.btn_r_u);
        btn_g_d = (Button)findViewById(R.id.btn_g_d);
        btn_g_u = (Button)findViewById(R.id.btn_g_u);
        btn_b_d = (Button)findViewById(R.id.btn_b_d);
        btn_b_u = (Button)findViewById(R.id.btn_b_u);
        btn_save = (Button)findViewById(R.id.btn_save_led);

        btn_r_d.setOnClickListener(mListener);
        btn_r_u.setOnClickListener(mListener);
        btn_g_d.setOnClickListener(mListener);
        btn_g_u.setOnClickListener(mListener);
        btn_b_d.setOnClickListener(mListener);
        btn_b_u.setOnClickListener(mListener);
        btn_save.setOnClickListener(mListener);

    }
}


