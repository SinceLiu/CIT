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
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

public class IRISking extends TestActivity {
    ///udisk   or  /storage/udisk
    private static final String TAG = "CIT_IRISK";

    private TextView tv_Touch;
    private Button btnPass;
    private Button btnTest;
    Intent i = new Intent();
    private Context context;
    private IntentFilter tp_pass_intentFilter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            btnPass.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt_one_btn;
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        tv_Touch = (TextView)findViewById(R.id.txt_one);
        tv_Touch.setText("Please choose the result of IRISking");
        btnPass = super.btnPass;
        //btnPass.setEnabled(false);
        i = new Intent("android.media.action.IRISKING");
        btnTest = (Button)findViewById(R.id.btn_one);
        btnTest.setText("ReTest IRISKING");
        btnTest.setOnClickListener(new OnClickListener(){
            public void onClick (View v) {
                btnTest.setEnabled(false);
                if (v.getId() == R.id.btn_one) {
                    startActivity(i);
                }
            }
        });
        /*btnTest.setEnabled(false);
        tp_pass_intentFilter = new IntentFilter();
        tp_pass_intentFilter.addAction("ACTION_TP_PASS");
        context.registerReceiver(mReceiver, tp_pass_intentFilter);*/
        startActivity(i);
    }


    @Override
    protected void onResume() {
        super.onResume();
        btnTest.setEnabled(true);
    }
}
