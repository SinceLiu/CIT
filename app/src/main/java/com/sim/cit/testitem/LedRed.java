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


public class LedRed extends TestActivity {
    private static final String TAG = "CIT_Led";

    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt;
        super.onCreate(savedInstanceState);
        tvMessage = (TextView)findViewById(R.id.txt_one);
        tvMessage.setText(R.string.led_red_test);
    }       
}
