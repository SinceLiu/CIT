package com.sim.cit.testitem;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
//Modify for change word color on the screen by lizhaobo 20151110 start
import android.graphics.Color;
//Modify for change word color on the screen by lizhaobo 20151110 end
public class MotionSensor extends TestActivity {
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float[] mValues;
	ImageView imageView[];
    TextView ms_tv_XYZ[];
    TextView ms_tv_pass;
    int m_rotation = 0;
    int m_nCurArrow;
    private boolean isTest;
    private float[] cmdValues;

    //Add SensorCheck for MotionSensor by xiasiping 20140626 start
    private float mValue_x;
    private float mValue_y;
    private boolean isUp = false;
    private boolean isRight = false;
    private boolean isDown = false;
    private boolean isLeft = false;
    //Add SensorCheck for MotionSensor by xiasiping 20140626 end
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutId = R.layout.motion_senor;
		super.onCreate(savedInstanceState);
                //Add SensorCheck for MotionSensor by xiasiping 20140626 start
                btnPass.setEnabled(false);
                //Add SensorCheck for MotionSensor by xiasiping 20140626 end
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//2012-12-06 add for test by lvhongshan--start
				if(null==mSensor){
					Log.i("lvhongshan_mSensor", "mSensor is "+"null");
				}
				else{
					Log.i("lvhongshan_mSensor", "mSensor is "+"not null");
				}	
		//2012-12-06 add for test by lvhongshan--start
        imageView = new ImageView[4];
		ms_tv_XYZ = new TextView[3];
        
		initAllControl();
	}

	private void initAllControl() {
		
		imageView[0] = (ImageView) findViewById(R.id.ms_arrow_up);
		imageView[1] = (ImageView) findViewById(R.id.ms_arrow_right);
		imageView[2] = (ImageView) findViewById(R.id.ms_arrow_down);
		imageView[3] = (ImageView) findViewById(R.id.ms_arrow_left);
                //Modify for change word color on the screen by lizhaobo 20151110 start
                ms_tv_XYZ[0] = (TextView) findViewById(R.id.ms_tv_x);
                ms_tv_XYZ[0].setTextColor(Color.BLACK);
                ms_tv_XYZ[1] = (TextView) findViewById(R.id.ms_tv_y);
                ms_tv_XYZ[1].setTextColor(Color.BLACK);
                ms_tv_XYZ[2] = (TextView) findViewById(R.id.ms_tv_z);
                ms_tv_XYZ[2].setTextColor(Color.BLACK);
		
                ms_tv_pass = (TextView)findViewById(R.id.ms_pass);
                ms_tv_pass.setTextColor(Color.BLACK);
                //Modify for change word color on the screen by lizhaobo 20151110 end
	}

    @Override
    protected void onResume() {
        super.onResume();
        isTest = true;
//		new Thread(){
//			@Override
//			public void run() {
//				super.run();
//				while (isTest) {
//					try {
//						sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					cmdValues = CommonDrive.getMotionXYZ();
//					for (int i = 0; i < cmdValues.length; i++) {
//						Log.i("MotionSenorTest", "cmdValues" + i + " = " + cmdValues[i]);
//					}
//					mHandler.sendEmptyMessage(0);
//				}
//			}
//		}.start();

        if(mSensor == null) {
            Toast.makeText(this, "MotionSensor is null: ", 2000).show();
        }
        boolean bSucceed = mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if(!bSucceed) {
            Toast.makeText(this, "registerListener is faild: ", 2000).show();
        }
    }

	@Override
	protected void onPause() {
		super.onPause();
		isTest = false;
		mSensorManager.unregisterListener(mListener);

	}

	private SensorEventListener mListener = new SensorEventListener() {
		
		public void onSensorChanged(SensorEvent event) {
			mValues = event.values;

                        float val_x = (float)(Math.round((-mValues[0])*1000000))/1000000;
                        float val_y = (float)(Math.round((-mValues[1])*1000000))/1000000;
                        float val_z = (float)(Math.round((-mValues[2])*1000000))/1000000;
			ms_tv_XYZ[0].setText("X = " + val_x);
			ms_tv_XYZ[1].setText("Y = " + val_y);
			ms_tv_XYZ[2].setText("Z = " + val_z);

			if(mValues[0]>=2.2 && mValues[0]<=3.2
			  &&mValues[1]>=-3.6&&mValues[1]<=-2.6
			  &&mValues[2]>=-6.0&&mValues[2]<=-5.0)
			{	
				ms_tv_pass.setText("PASS");
			}

			int nNewArrow = 0;
			int nDirect = GetDirection(-(int) mValues[0], -(int) mValues[1]);

			switch (nDirect) {
			case 1:
				nNewArrow = 1;
				break;
			case 2:
				nNewArrow = 2;
				break;
			case 3:
				nNewArrow = 3;
				break;
			case 4:
				nNewArrow = 4;
				break;
			default:
				nNewArrow = 1;
				break;
			}
			nNewArrow = nNewArrow - 1;

			if (nNewArrow != m_nCurArrow) {
				imageView[m_nCurArrow].setVisibility(View.INVISIBLE);
				m_nCurArrow = nNewArrow;
				imageView[m_nCurArrow].setVisibility(View.VISIBLE);
			} else {
				imageView[m_nCurArrow].setVisibility(View.VISIBLE);
			}
                        //Add SensorCheck for MotionSensor by xiasiping 20140626 start
                        mValue_x = -mValues[0];
                        mValue_y = -mValues[1];
                        if (m_nCurArrow == 0 && (mValue_y>1&&Math.abs(mValue_y)>Math.abs(mValue_x))) {
                            isUp = true;
                        }
                        if (m_nCurArrow == 1 && (mValue_x>0&&Math.abs(mValue_y)<Math.abs(mValue_x))) {
                            isRight = true;
                        }
                        if (m_nCurArrow == 2 && (mValue_y<1&&Math.abs(mValue_y)>Math.abs(mValue_x))) {
                            isDown = true;
                        }
                        if (m_nCurArrow == 3 && (mValue_x<0&&Math.abs(mValue_y)<Math.abs(mValue_x))) {
                            isLeft = true;
                        }
                        if (isUp && isRight && isDown && isLeft) {
                            btnPass.setEnabled(true);
                        }
                        //Add SensorCheck for MotionSensor by xiasiping 20140626 end
		}
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}
	};

	private int GetDirection(int Hi, int Md) {
		int nDirect = 0;
		int nHi, nMd;

		nHi = Math.abs(Hi);
		nMd = Math.abs(Md);

		if (nHi > nMd) {
			if (Hi > 0) {
				nDirect = 2;
			} else {
				nDirect = 4;
			}
		} else {
			if (Md > 0) {
				nDirect = 1;
			} else {
				nDirect = 3;
			}
		}

		if (m_rotation == 90) {
			nDirect = (nDirect + 1) % 4;
		} else if (m_rotation == 270) {
			nDirect = (nDirect + 3) % 4;
		}
		if (nDirect == 0) {
			nDirect = 4;
		}

		return nDirect;
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ms_tv_XYZ[0].setText("X = " + cmdValues[0]);
			ms_tv_XYZ[1].setText("Y = " + cmdValues[1]);
			ms_tv_XYZ[2].setText("Z = " + cmdValues[2]);
		}
	};
}
