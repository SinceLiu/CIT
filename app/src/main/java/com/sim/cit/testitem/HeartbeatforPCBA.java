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
import android.widget.TextView;
import android.widget.Toast;
//Modify for passButton clickable when locate success by songguangyu 20140220 start
import android.widget.Button;
//Modify for passButton clickable when locate success by songguangyu 20140220 end

import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
public class HeartbeatforPCBA extends TestActivity {

    private static final String TAG = "PCBA_Heartbeat_test";
    private TextView mtv_result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.two_txt;
        super.onCreate(savedInstanceState);
        btnPass.setEnabled(false);
        mtv_result = (TextView) findViewById(R.id.txt_two);
        int result = CommonDrive.PtestHeartBeat();
        if (result == 1) {
            mtv_result.setText("The test is Successful!");
            btnPass.setEnabled(true);
        } else if (result ==0) {
            mtv_result.setText("No data has returned!");
        } else {
            mtv_result.setText("The data returned was not valid!");
        }
    }
}
