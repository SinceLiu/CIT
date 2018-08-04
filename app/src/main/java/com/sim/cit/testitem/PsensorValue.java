package com.sim.cit.testitem;

import java.io.File;

import android.util.Log;
import android.widget.TextView;
import com.sim.cit.CITTestHelper;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
//import com.qualcomm.qcnvitems.QcNvItems;
//add by liyunlong start
import android.os.IBinder;
import android.os.Message;
import android.widget.Button;
import android.os.Handler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//add by liyunlong end

public class PsensorValue extends TestActivity {
private static final String TAG = "CIT_PSENSOR_VALUE";

    private Button btnPass;
    private TextView tv_first;
    private TextView tv_now;
    private static final int GET_P_SENSOR = 521;
    //private static QcNvItems mNv;

    private Handler mmHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
            case GET_P_SENSOR:
                int result_first = 0;
                int result_now = 0;
                try{
                    //result_first = mNv.get_p_sensor_first();
                    //result_now = mNv.get_p_sensor();
                    tv_first.setText("The value first : " + result_first);
                    tv_now.setText("The value now : " + result_now);
                    Log.i("xsp_get_p_sensor_first", "get_p_sensor_frist() = [" + result_first + "]" );
                    Log.i("xsp_get_p_sensor_now", "get_p_sensor() = [" + result_now + "]" );
                } catch (Exception e1) {
                    Log.i("xsp_get_psensor", "get psensor frist or now has Exception : " + e1.toString());
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.two_txt;
        super.onCreate(savedInstanceState);
        btnPass = super.btnPass;
        //mNv = new QcNvItems(getApplicationContext());
        tv_first = (TextView)findViewById(R.id.txt_one);
        tv_now = (TextView)findViewById(R.id.txt_two);
        tv_first.setText(R.string.tips_waiting_first);
        mmHandler.removeMessages(GET_P_SENSOR);
        mmHandler.sendMessageDelayed(mmHandler.obtainMessage(GET_P_SENSOR), 1000);
    }

}
