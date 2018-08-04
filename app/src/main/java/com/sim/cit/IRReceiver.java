package com.sim.cit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;

public class IRReceiver extends BroadcastReceiver {
    private static final String TAG = "CIT_IRReceiver";
    private String irLed_switch = null;
    private FileOutputStream fosFL;
    final byte[] LIGHTE_ON1 = { '9', '9' };
    final byte[] LIGHTE_ON2 = { '1' };
    final byte[] LIGHTE_OFF = { '0' };
    private static final String FLASHLIGHT_SWITCH = "/sys/class/leds/ir-led/brightness";
    private static final String FLASHLIGHT_1 = "/sys/class/leds/ir-led-pwm/brightness";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "on IRReceiver !");
        irLed_switch = intent.getExtras().getString("irled_switch");
        Log.e("xsp", "irLed_switch = [" + irLed_switch + "]");
        if (irLed_switch != null && irLed_switch.equals("on")) {
            turnOnIRLed();
        } else if (irLed_switch != null && irLed_switch.equals("off")) {
            turnOffIRLed();
        }

    }

    public void turnOnIRLed() {
        set_node(LIGHTE_ON1, FLASHLIGHT_1);
        set_node(LIGHTE_ON2, FLASHLIGHT_SWITCH);
    }

    public void turnOffIRLed() {
        set_node(LIGHTE_OFF, FLASHLIGHT_1);
        set_node(LIGHTE_OFF, FLASHLIGHT_SWITCH);
    }

    private void set_node(byte[] values, String path) {
        try{
            fosFL = new FileOutputStream(path);

            fosFL.write(values);

            fosFL.flush();

            fosFL.close();
        } catch (Exception e) {
            Log.e("xsp", "OPEN [" + path + "] LED failed!" + e.toString());            
        }
    }

}

