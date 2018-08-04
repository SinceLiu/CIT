package com.sim.cit.testitem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.ComponentName;
import com.sim.cit.CommonDrive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import android.util.Log;

public class FPCalibration extends TestActivity {
    ///udisk   or  /storage/udisk
    private static final String TAG = "CIT_FPCalibration";

    private TextView tvFPresult;
    private Button btnPass;
    private Button btnTest;
    Intent i = new Intent();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt_one_btn;
        super.onCreate(savedInstanceState);
        tvFPresult = (TextView)findViewById(R.id.txt_one);
        tvFPresult.setText("Please choose the result of FPCalibration!");
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
        i = new Intent();
        ComponentName silead_fp = new ComponentName(
                            "www.sileadinc.com", "www.sileadinc.com.Silead_fpActivity");
        i.setComponent(silead_fp);
        btnTest = (Button)findViewById(R.id.btn_one);
        btnTest.setText("Go to Calibration APK");
        btnTest.setOnClickListener(new OnClickListener(){
            public void onClick (View v) {
                btnTest.setEnabled(false);
                if (v.getId() == R.id.btn_one) {
                    startActivity(i);
                }
            }
        });
        btnTest.setEnabled(false);
        startActivity(i);
    }
    @Override
    protected void onResume() {
        super.onResume();
        btnTest.setEnabled(true);
        btnPass.setEnabled(true);
    }
    @Override
    public void finish() {
        File f1 = new File("/data/local/tmp", "config.dat");
        File f2 = new File("/persist", "config.dat");
        if (f1.exists()) {
            //try {
                Log.i(TAG, "f1="+f1.getAbsolutePath()+"\n"+"f2="+f2.getAbsolutePath());
                CommonDrive.copyFile(f1.getAbsolutePath(), f2.getAbsolutePath());
            //} catch (FileNotFoundException e) {
                //Log.e("FPCalibration", "copy file failed : " + e.toString());
            //}
        }
        super.finish();
    }
}
