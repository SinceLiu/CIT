package com.sim.cit.testitem;

import java.io.File;

import android.app.Service;
import android.net.Uri;
import android.os.Bundle;
import android.os.StatFs;
import android.os.Vibrator;
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

public class Vibrate extends TestActivity {
    private static final String TAG = "CIT_Vibrate";

    private TextView tvMessage;
    private boolean isTest=true;
    final int SUB_ID=1;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt;
        super.onCreate(savedInstanceState);
        tvMessage = (TextView)findViewById(R.id.txt_one);
        tvMessage.setText(getString(R.string.vibrate_hint));
    }

    private void startVibration(){
        Log.i(TAG, "vibrator start");
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (!vibrator.hasVibrator()){
            Toast.makeText(this, "The device has no vibrator ", 2000).show();
        }
        vibrator.vibrate(new long[] { 0, 1000, 1000, 1000, 1000 }, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isTest=false;
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        startVibration();
    }
}
