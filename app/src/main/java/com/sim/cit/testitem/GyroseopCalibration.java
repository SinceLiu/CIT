package com.sim.cit.testitem;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import android.text.TextUtils;
import android.os.Handler;
import android.os.Message;
//import com.qualcomm.qcnvitems.QcNvItems;

public class GyroseopCalibration extends TestActivity {
    private TextView txtMsg;
    private Button btnCalibration;
    private Button btnPass;
    private static final String TAG = "MotionSensorCalibration";
    // add for gsensor test by liuzhihao 20131129 end
    private static final int SET_GYRO_SENSOR = 521;
    public int result = 10;
    private Context context;
//    private static QcNvItems mNv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.one_txt_one_btn;
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        txtMsg = (TextView)findViewById(R.id.txt_one);
        txtMsg.setText(R.string.gyro_cali_tips);
        btnCalibration = (Button) findViewById(R.id.btn_one);
        btnCalibration.setText(R.string.str_calibration);
        btnCalibration.setOnClickListener(mCalibration);
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
//        mNv = new QcNvItems(getApplicationContext());
    }

    private OnClickListener mCalibration = new OnClickListener() {
        public void onClick(View arg0) {
            result = CommonDrive.GyroCalibration();
            if (result == 0) {
                mmHandler.removeMessages(SET_GYRO_SENSOR);
                mmHandler.sendMessageDelayed(mmHandler.obtainMessage(SET_GYRO_SENSOR), 1000);
            } else {
                Toast.makeText(GyroseopCalibration.this, "GyroseopCalibration Failed!~", 2000).show();
            }
        }
    };

    private final Handler mmHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
            case SET_GYRO_SENSOR:
                try {
                    int x = CommonDrive.getGyro_x();
                    int y = CommonDrive.getGyro_y();
                    int z = CommonDrive.getGyro_z();
                    Log.i("xsp", "GyroCalibration xxxxx = " + x);
                    Log.i("xsp", "GyroCalibration yyyyy = " + y);
                    Log.i("xsp", "GyroCalibration zzzzz = " + z);
//                    mNv.set_gyro_sensor(x, y, z);
                    int [] values = new int[3];
//                    = mNv.get_gyro_sensor();
                    Log.i("xsp", "GyroCalibration xxxxx = " + values[0]);
                    Log.i("xsp", "GyroCalibration yyyyy = " + values[1]);
                    Log.i("xsp", "GyroCalibration zzzzz = " + values[2]);
                    btnPass.setEnabled(true);
                    Toast.makeText(GyroseopCalibration.this, "GyroseopCalibration Successful!~", 2000).show();
                } catch (Exception e) {
                    Log.i("GsensorCalibration", "QcNvItems error");
                    Toast.makeText(context, "Failed", 1500).show();
                    e.printStackTrace();
                }
                break;
            }
        }
    };

}
