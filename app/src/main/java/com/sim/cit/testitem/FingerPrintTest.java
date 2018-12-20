package com.sim.cit.testitem;

import android.os.Bundle;
import android.util.Log;
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

public class FingerPrintTest extends TestActivity {
    ///udisk   or  /storage/udisk
    private static final String TAG = "CIT_FingerPrintTest";

    private TextView tv_Touch;
    private Button btnPass;
    private Button btnTest;
    Intent i = new Intent();
    private Context context;
    private IntentFilter tp_pass_intentFilter;
    private int requestCode = 0;//Modified by xuyongfeng for fingerprint reuslt on 2016-5-12
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            btnPass.setEnabled(true);
        }
    };

    private boolean fpSuc = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt_one_btn;
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        tv_Touch = (TextView)findViewById(R.id.txt_one);
        tv_Touch.setText("Please choose the result of FingerPrint");
        tv_Touch.setText(R.string.fp_touch_key);
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
        i = new Intent("EgisInlineTool");
        btnTest = (Button)findViewById(R.id.btn_one);
        btnTest.setText("ReTest FingerPrint");
        btnTest.setOnClickListener(new OnClickListener(){
            public void onClick (View v) {
                btnTest.setEnabled(false);
                if (v.getId() == R.id.btn_one) {
                    startActivityForResult(i, requestCode);//Modified by xuyongfeng for fingerprint reuslt on 2016-5-12
                }
            }
        });
        btnTest.setVisibility(View.GONE);
        isAllowBack = true;
        
        /*btnTest.setEnabled(false);
        tp_pass_intentFilter = new IntentFilter();
        tp_pass_intentFilter.addAction("ACTION_TP_PASS");
        context.registerReceiver(mReceiver, tp_pass_intentFilter);*/
//        startActivityForResult(i, requestCode);//Modified by xuyongfeng for fingerprint reuslt on 2016-5-12
    }


    @Override
    protected void onResume() {
        super.onResume();
        btnTest.setEnabled(true);
    }
    //Add by xuyongfeng for fingerprint reuslt on 2016-5-12 start
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0){
            btnPass.setEnabled(resultCode == -1);
        }
    }
    //Add by xuyongfeng for fingerprint reuslt on 2016-5-12 End


	@Override
	public void onBackPressed() {
		if(!fpSuc){
			fpSuc = true;
			tv_Touch.setText(R.string.fp_identify_suc);
			btnPass.setEnabled(true);
		}
	}

    
    
}
