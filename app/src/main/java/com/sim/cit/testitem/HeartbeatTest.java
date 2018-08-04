package com.sim.cit.testitem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//Modify for passButton clickable when locate success by songguangyu 20140220 start
import android.widget.Button;
//Modify for passButton clickable when locate success by songguangyu 20140220 end

import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
public class HeartbeatTest extends TestActivity {

    private static final String TAG = "Heartbeat_test";
    private static final int HB_TEST_ON = 0;
    private static final int HB_TEST_OFF = 1;
    private static final int HB_TEST_EXCEPTION = 2;
    private TextView mtv_testInfo;
    private TextView mtv_result;
    ImageView imageView[] = new ImageView[2];
    private String HB_testInfo;
    private Timer mTimerHBStatus = new Timer();
    private static final int MSG_SHOW_PSENSOR = 0;
    private int times = 0;

    class TimerReadHBStatus extends TimerTask {
        public void run() {
            int status = CommonDrive.CtestHeartBeat();
            Log.e(TAG, "~~~~~HB_STATUS = " + status);
            if (status == 1) {
                mmHandler.removeMessages(HB_TEST_ON);
                mmHandler.sendMessage(mmHandler.obtainMessage(HB_TEST_ON));
            } else if (status == -1) {
                mmHandler.removeMessages(HB_TEST_OFF);
                mmHandler.sendMessage(mmHandler.obtainMessage(HB_TEST_OFF));
            } else {
                mmHandler.removeMessages(HB_TEST_EXCEPTION);
                mmHandler.sendMessage(mmHandler.obtainMessage(HB_TEST_EXCEPTION));
            }

        }
    }

    Handler mmHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            Log.e(TAG, "~~~~~HB_msg.what = " + msg.what);
            switch(msg.what){
            case HB_TEST_ON:
                imageView[0].setVisibility(View.INVISIBLE);
                imageView[1].setVisibility(View.VISIBLE);
                mtv_result.setText(R.string.status_on);
                times++;
                if (times == 3) {
                    Log.e(TAG, "~~~~~HB_times = " + times);
                    btnPass.setEnabled(true);
                }
                break;
            case HB_TEST_OFF:
                times = 0;
                imageView[0].setVisibility(View.VISIBLE);
                imageView[1].setVisibility(View.INVISIBLE);
                mtv_result.setText(R.string.status_off);
                break;
            case HB_TEST_EXCEPTION:
                times = 0;
                imageView[0].setVisibility(View.VISIBLE);
                imageView[1].setVisibility(View.INVISIBLE);
                mtv_result.setText(R.string.status_exception);
                break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.test_heartbeat;
        super.onCreate(savedInstanceState);
        btnPass.setEnabled(false);
        HBinit();
    }

    private void HBinit() {
        mtv_testInfo = (TextView) findViewById(R.id.tv_hb_info);
        mtv_testInfo.setText(R.string.HB_message);
        mtv_result = (TextView) findViewById(R.id.tv_hb_status);
        imageView[0] = (ImageView) findViewById(R.id.test_hb_gray);
        imageView[1] = (ImageView) findViewById(R.id.test_hb_green);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimerHBStatus.schedule(new TimerReadHBStatus(), 1, 100);
    }

    @Override
    protected void onDestroy() {
        mTimerHBStatus.cancel();
        super.onDestroy();
    }

}
