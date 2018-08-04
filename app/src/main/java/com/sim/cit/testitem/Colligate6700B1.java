package com.sim.cit.testitem;

import java.io.File;

import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.os.StatFs;
import android.os.Vibrator;
//import android.telephony.MSimTelephonyManager;
import android.telephony.TelephonyManager;
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

public class Colligate6700B1 extends TestActivity {
    private static final String TAG = "CIT_Colligate6700B1";

    private TextView tvSim;
    private Button btnPass;
    private Vibrator vibrator;
    private boolean simIsPass;
    private boolean isTest=true;
    final int SUB_ID=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.sim_speaker_virbrate;
        super.onCreate(savedInstanceState);
        tvSim = (TextView)findViewById(R.id.txt_one);
        btnPass = super.btnPass;
    }

    private void startVibration(){
        Log.i(TAG, "vibrator start");
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (null != vibrator){
            vibrator.vibrate(new long[] { 0, 1000, 1000, 1000, 1000 }, 0);
        }else
            Log.i(TAG, "vibrator is null");
    }

    private void getSimStatus(){
        String strStatus = "";
        int nSimStatus;
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    	nSimStatus = tm.getSimState();
        switch (nSimStatus) {
        case TelephonyManager.SIM_STATE_UNKNOWN:
            strStatus = getString(R.string.sim_status_0);
            simIsPass = false;
            break;
        case TelephonyManager.SIM_STATE_ABSENT:
            strStatus = getString(R.string.sim_status_1);
            simIsPass = false;
            break;
        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
            strStatus = getString(R.string.sim_status_2);
            simIsPass = false;
            break;
        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
            strStatus = getString(R.string.sim_status_3);
            simIsPass = false;
            break;
        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            strStatus = getString(R.string.sim_status_4);
            simIsPass = false;
            break;
        case TelephonyManager.SIM_STATE_READY:
            strStatus = getString(R.string.sim_status_5);
            simIsPass = true;
            break;
        default:
            strStatus = getString(R.string.sim_status_0);
            simIsPass = false;
            break;
        }
        Log.i(TAG, "SIM card status: " + strStatus);
        if (simIsPass) {
            tvSim.setText("SIM card:\tPass");
        }else{
            tvSim.setText("SIM card:\tFail");
            Toast.makeText(this, "SIM card status: " + strStatus, 2000).show();
        }
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
        getSimStatus();
        startVibration();
        btnPass.setEnabled(simIsPass);
    }
}
