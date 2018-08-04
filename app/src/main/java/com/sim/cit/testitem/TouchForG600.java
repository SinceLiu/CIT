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

public class TouchForG600 extends TestActivity {
    ///udisk   or  /storage/udisk

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.touch_for_g600;
        super.onCreate(savedInstanceState);

    }

    /*@Override
    public void finish() {
        unregisterReceiver(mReceiver);
        super.finish();
    }*/

//    @Override
//    protected void onResume() {
//        super.onResume();
//        btnTest.setEnabled(true);
//    }
}
