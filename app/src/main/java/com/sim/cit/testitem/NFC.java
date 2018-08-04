package com.sim.cit.testitem;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.os.Handler;
import android.os.Message;
// Modify for move the intent-filter of NFC tag action to NFC test in CIT by xiasiping 20141112 start
import android.app.PendingIntent;
// Modify for move the intent-filter of NFC tag action to NFC test in CIT by xiasiping 20141112 end
//Modify for 501 can not see the word on screen by lizhaobo 20151104 start
import android.graphics.Color;
//Modify for 501 can not see the word on screen by lizhaobo 20151104 end
public class NFC extends TestActivity {

    private static final String TAG = "CIT_NFC";
    private static final int ENABLE = 0x1;
    private NfcAdapter nfcAdapter;
    private TextView promt = null;
    private Button btnPass;
    protected static boolean isCIT = false;
    private boolean isEnabledSetting = true;
    // Modify for move the intent-filter of NFC tag action to NFC test in CIT by xiasiping 20141112 start
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    private Context mContext;
    private IntentFilter nfc_pass_intentFilter;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            btnPass.setEnabled(true);
        }
    };

    private void init(){
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, NFCDialog.class), 0);
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{tech};
        mTechLists = new String[][]{
            new String[]{android.nfc.tech.IsoDep.class.getName()},
            new String[]{android.nfc.tech.NfcV.class.getName()},
            new String[]{android.nfc.tech.NfcF.class.getName()},
            new String[]{android.nfc.tech.NfcA.class.getName()},
            new String[]{android.nfc.tech.NfcB.class.getName()},
            new String[]{android.nfc.tech.NfcBarcode.class.getName()},
            new String[]{android.nfc.tech.MifareClassic.class.getName()},
            new String[]{android.nfc.tech.MifareUltralight.class.getName()},
        };
        nfc_pass_intentFilter = new IntentFilter();
        nfc_pass_intentFilter.addAction("ACTION_NFC_PASS");
        mContext.registerReceiver(mReceiver, nfc_pass_intentFilter);
    }
    // Modify for move the intent-filter of NFC tag action to NFC test in CIT by xiasiping 20141112 end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.nfc;
        super.onCreate(savedInstanceState);
        promt = (TextView) findViewById(R.id.promt);
        //Modify for 501 can not see the word on screen by lizhaobo 20151104 start
        promt.setTextColor(Color.BLACK);
        //Modify for 501 can not see the word on screen by lizhaobo 20151104 end
        // get the default NFC controller
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            promt.setText("The device does not support NFC！");
        }
        mContext = this;
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
        // Modify for move the intent-filter of NFC tag action to NFC test in CIT by xiasiping 20141112 start
        init();
        // Modify for move the intent-filter of NFC tag action to NFC test in CIT by xiasiping 20141112 end
    }
    @Override
    protected void onResume() {
        super.onResume();
      //  if (!nfcAdapter.isEnabled()) {
        /*    Intent nfcIntent = new Intent();
            nfcIntent.setAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
            nfcIntent.putExtra(NfcAdapter.EXTRA_ADAPTER_STATE,NfcAdapter.STATE_ON);
            sendBroadcast(nfcIntent);
            promt.setText("please start the function of NFC in setting！");*/
        //    finish();
        //    return;
        // Modify for move the intent-filter of NFC tag action to NFC test in CIT by xiasiping 20141112 start
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
        // Modify for move the intent-filter of NFC tag action to NFC test in CIT by xiasiping 20141112 end
        if(!nfcAdapter.isEnabled()){
            isEnabledSetting = false;
            if(nfcAdapter.enable()){
                new Thread() {
                    public void run() {
                        try {
                            //Modify for fix bug 7364 that the NFC is enable and sometimes is disable by lvhongshan 20140127 start
                            int i = 0;
                            Thread.sleep(2000);
                            while((nfcAdapter.getAdapterState() == NfcAdapter.STATE_TURNING_ON) && i < 10){
                                i ++;
                                Thread.sleep(2000);
                            }
                            //Modify for fix bug 7364 that the NFC is enable and sometimes is disable by lvhongshan 20140127 end
                            if(nfcAdapter.enableNdefPush()){
                                Message message = new Message();
                                message.what = ENABLE;
                                mnfcHandler.sendMessage(message);
                            } else {
                                promt.setText(R.string.nfc_disable);
                                btnPass.setEnabled(false);
                            }
                        } catch (Exception ex) {
                            loge(ex);
                        }
                    }
                }.start();
            } else {
                promt.setText(R.string.nfc_disable);
                btnPass.setEnabled(false);
                isCIT = false;
            }
        }else if(nfcAdapter.enableNdefPush()){
            Message message = new Message();
            message.what = ENABLE;
            mnfcHandler.sendMessage(message);
        } else {
            promt.setText(R.string.nfc_disable);
            btnPass.setEnabled(false);
        }
        //}else {
        //    promt.setText("The NFC is enable！");
        //    btnPass.setEnabled(true);
        //}
        //obtain the ACTION_TECH_DISCOVERED
       /* if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
               //handle this intent
            Log.i(TAG,"onResume  getIntent().getAction()"+getIntent().getAction());
            processIntent(getIntent());
        }*/
    }
    private Handler mnfcHandler = new Handler() {
        public void handleMessage(Message msg) {
          if (msg.what == ENABLE) {
               if(nfcAdapter.isNdefPushEnabled()) {
                  promt.setText(R.string.nfc_enable);
                  // btnPass.setEnabled(true);
                  isCIT = true;
                } else {
                  promt.setText(R.string.nfc_disable);
                  btnPass.setEnabled(false);
                  isCIT = false;
                }
           }
        }
    };

    void loge(Object e) {
         Log.e(TAG, "" + e);
    }

    @Override
    public void finish() {
        unregisterReceiver(mReceiver);
        isCIT = false;
        if (nfcAdapter.isEnabled()) {
        /*    Intent nfcIntent = new Intent();
            nfcIntent.setAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
            nfcIntent.putExtra(NfcAdapter.EXTRA_ADAPTER_STATE,NfcAdapter.STATE_OFF);
            sendBroadcast(nfcIntent);*/
            if(!isEnabledSetting){
                nfcAdapter.disable();
            }
        }
        super.finish();
    }
    // Modify for move the intent-filter of NFC tag action to NFC test in CIT by xiasiping 20141112 start
    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }
    // Modify for move the intent-filter of NFC tag action to NFC test in CIT by xiasiping 20141112 end

    @Override
    protected void onStop(){
        super.onStop();
        isCIT = false;
    }

    private void processIntent(Intent intent) {
        //get the tag in intent
        StringBuffer sbuffer = new StringBuffer();
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        for (String tech : tagFromIntent.getTechList()) {
            Log.i(TAG,"processIntent  tagFromIntent.getTechList()" + tech);
            sbuffer.append(tech);
        }
        promt.setText(sbuffer.toString());
    }
}
