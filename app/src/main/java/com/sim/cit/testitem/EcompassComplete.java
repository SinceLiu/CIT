package com.sim.cit.testitem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class EcompassComplete extends TestActivity {
    private static final String TAG = "OrientationTest";
    private TextView tvQuality;
    private TextView tvRid;
    private ArrowView avArrow;

    private boolean isTest;
    private int[] orienValues;
    private float[] mValues;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.ecompass_complete;
        super.onCreate(savedInstanceState);

        isTest = true;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        tvQuality = (TextView)findViewById(R.id.tv_quality);
        tvRid = (TextView)findViewById(R.id.tv_rid);
        avArrow = new ArrowView(this);

        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, Gravity.CENTER);
        addContentView(avArrow, lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isTest = true;
        //mSensorManager.registerListener(eventListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        if(mSensor == null) {
            Toast.makeText(this, "Ecompass Sensor is null: ", 2000).show();
        }
        boolean bSucceed = mSensorManager.registerListener(eventListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
        if(!bSucceed) {
            Toast.makeText(this, "registerListener is faild: ", 2000).show();
        }

//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                while (isTest) {
//                    try {
//                        sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    orienValues = CommonDrive.getOrientationValues();
//                    mHandler.sendMessage(new Message());
//                }
//            }
//        }.start();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(eventListener);
        isTest = false;
        super.onPause();
        Log.i(TAG, "onPause");
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tvQuality.setText("Quality = " + String.valueOf(orienValues[8]));
            tvRid.setText("RID = " + String.valueOf((float)orienValues[9] / 64));
        }
    };

    private SensorEventListener eventListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent event) {
            mValues = event.values;
            Log.i(TAG, "mValues[0] = " + mValues[0]);
            
            
            //add by hwj20170318
            if(mValues[0] == 0){
            	btnPass.setEnabled(false);
            }else{
            	btnPass.setEnabled(true);
            }
            
            
            if (avArrow != null) {
                avArrow.invalidate();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private class ArrowView extends View {

        private Paint mPaint = new Paint();
        private Path mPath = new Path();
        private boolean mAnimate;
        private long mNextTime;

        public ArrowView(Context context) {
            super(context);
            mPath.moveTo(0, -50);
            mPath.lineTo(-20, 60);
            mPath.lineTo(0, 50);
            mPath.lineTo(20, 60);
            mPath.close();

            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cx = w / 2;
            int cy = h / 2;

            canvas.save();
            canvas.translate(cx, cy);
            if (mValues != null) {
                canvas.rotate(-mValues[0]);
                canvas.drawPath(mPath, mPaint);
            }
            canvas.restore();
        }
    }
}

