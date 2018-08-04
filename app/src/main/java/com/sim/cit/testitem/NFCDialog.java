package com.sim.cit.testitem;

import java.io.IOException;

import com.sim.cit.R;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
// Add interceptor of PASSbtn in NFC test by xsp 20150402 start
import android.content.Context;
// Add interceptor of PASSbtn in NFC test by xsp 20150402 end
/*  Modify to add NFC Feature by liyunlong 20140301 */

public class NFCDialog extends Activity{
	private static final String TAG = "CIT_NFC_DIALOG";
	private TextView promt_dialog = null;
	private Button btnOk = null;
    private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_dialog);
        mContext = this;
		promt_dialog = (TextView) findViewById(R.id.promt_dialog);
		btnOk = (Button)findViewById(R.id.nfc_ok);
		btnOk.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0){
				finish();//close current activity
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// obtain the ACTION_TECH_DISCOVERED
		if (NFC.isCIT && NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
			// handle this intent
			loge("onResume  getIntent().getAction()"
					+ getIntent().getAction());
			if(getIntent() != null){
				processIntent(getIntent());
			}
		    if(promt_dialog.getText().length() == 0){
			    promt_dialog.setText(R.string.nfc_dialog_info);
		    }
		} else{
            //do nothing
            //because CIT is not running
            finish();
        }
	}

	void loge(Object e) {
		Log.e(TAG, "" + e);
	}

    private void processIntent(Intent intent) {
        //get the tag from intent
        StringBuffer sbuffer = new StringBuffer();
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        for (String tech : tagFromIntent.getTechList()) {
            loge("processIntent  tagFromIntent.getTechList()" + tech);
            sbuffer.append(tech + "\n");
        }
	    promt_dialog.setText(sbuffer);
        // Add interceptor of PASSbtn in NFC test by xsp 20150402 start
        Intent nfc_intent = new Intent();
        nfc_intent.setAction("ACTION_NFC_PASS");
        mContext.sendBroadcast(nfc_intent);
        // Add interceptor of PASSbtn in NFC test by xsp 20150402 end
    }
}
