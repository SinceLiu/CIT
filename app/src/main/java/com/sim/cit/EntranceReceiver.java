package com.sim.cit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class EntranceReceiver extends BroadcastReceiver {
    private static final String TAG = "CIT_EntranceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive Host = " + intent.getData().getHost());
        Intent i = null;
        if (intent.getData().getHost().equals("248")) {
            i = new Intent(context, MainList.class);
//            i = new Intent(context, TempListActivity.class);
        }else if(intent.getData().getHost().equals("76388378")){
//            i = new Intent(context, SoftTest.class);
        }else if(intent.getData().getHost().equals("8266")){
//            i = new Intent(context, VCOMTest.class);
        }else if(intent.getData().getHost().equals("49")){
//            i = new Intent(context, HWtest.class);
        }else if(intent.getData().getHost().equals("1030")){
//            i = new Intent(context, DialogActivity.class);
        }else if(intent.getData().getHost().equals("0")){
//            i = new Intent(context, com.android.sim.others.SmallTest.class);
        }
        if (i != null) {
            try {
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } catch (Exception e) {
                // TODO: handle exception
                Log.e(TAG, "startActivity exception "+ e);
            }
        }
    }

}

