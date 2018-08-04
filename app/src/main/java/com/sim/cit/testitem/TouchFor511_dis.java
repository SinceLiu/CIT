package com.sim.cit.testitem;


import com.sim.cit.R;
/*
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import com.sim.cit.R;
import com.sim.cit.CommonDrive;
import com.sim.cit.TestActivity;

public class TouchFor511_dis extends TestActivity{

    private TextView tv_data;
    private TextView tv_instruction;
    private TextView tv_x;
    private TextView tv_y;
    private TextView tv_pressure;
    private Button bt_test;
    private int flag = 0;
    private short[] TouchFor511_dis;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		layoutId = R.layout.TouchFor511_dis;
		super.onCreate(savedInstanceState);
		tv_data = (TextView)findViewById(R.id.tv_data);
		tv_x = (TextView)findViewById(R.id.tv_x);
		tv_y = (TextView)findViewById(R.id.tv_y);
		tv_pressure = (TextView)findViewById(R.id.tv_p);
		tv_instruction = (TextView)findViewById(R.id.tv_instruction);
		bt_test = (Button)findViewById(R.id.TouchFor511_dis_test);

		bt_test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(flag){
				case 0:
					flag ++;

					TouchFor511_dis = CommonDrive.TouchFor511_dis_test();
                                        if(null != TouchFor511_dis){
					    tv_x.setText(getString(R.string.TouchFor511_dis_X)+" "+TouchFor511_dis[0]);
					    tv_y.setText(getString(R.string.TouchFor511_dis_Y)+" "+TouchFor511_dis[1]);
					    tv_pressure.setText(getString(R.string.TouchFor511_dis_pressure)+" "+TouchFor511_dis[2]);
                                        }
					tv_instruction.setText(getString(R.string.TouchFor511_dis_instruction2));
                                        bt_test.setText(getString(R.string.TouchFor511_dis_next));
					break;
				case 1:
					flag ++;
					TouchFor511_dis = CommonDrive.TouchFor511_dis_test();
                                        if(null != TouchFor511_dis){
					    tv_x.setText(getString(R.string.TouchFor511_dis_X)+" "+TouchFor511_dis[0]);
					    tv_y.setText(getString(R.string.TouchFor511_dis_Y)+" "+TouchFor511_dis[1]);
					    tv_pressure.setText(getString(R.string.TouchFor511_dis_pressure)+" "+TouchFor511_dis[2]);
                                        }
					tv_instruction.setText(getString(R.string.TouchFor511_dis_instruction3));
                                        bt_test.setText(getString(R.string.TouchFor511_dis_last));
				        break;
		                case 2:
		        	        flag = 0;
		        	        TouchFor511_dis = CommonDrive.TouchFor511_dis_test();
                                        if(null != TouchFor511_dis){
					    tv_x.setText(getString(R.string.TouchFor511_dis_X)+" "+TouchFor511_dis[0]);
					    tv_y.setText(getString(R.string.TouchFor511_dis_Y)+" "+TouchFor511_dis[1]);
					    tv_pressure.setText(getString(R.string.TouchFor511_dis_pressure)+" "+TouchFor511_dis[2]);
                                        }
					tv_instruction.setText(getString(R.string.TouchFor511_dis_instruction1));
                                        bt_test.setText(getString(R.string.TouchFor511_dis_start));
			                break;
			        default:
			    	        break;
				}
		    }
		});
	}
}
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;  
import android.app.AlertDialog;
import android.content.Context;  
import android.content.DialogInterface;
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.graphics.Path;  
import android.os.Bundle;  
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;  
import android.view.SurfaceHolder;  
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;  
import android.view.View;
import android.view.Window;  
import android.view.WindowManager;  
import android.widget.Toast;
import com.sim.cit.TestActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

  
public class TouchFor511_dis extends Activity {
  
    MyView mAnimView = null;  
    //private Timer TouchFor511_dis_timer = new Timer();
    private Timer TouchFor511_dis_timer ;
    private int time_count=0;
    AlertDialog.Builder alertDialogBuilder = null;
    private String tp_version_s = null;
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        //full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        //params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        getWindow().setAttributes(params);
        //Show the defined game View
        mAnimView = new MyView(this);
        setContentView(mAnimView);
        tp_version_s = getCacheInfo();
    }

    public class MyView extends SurfaceView implements Callback,Runnable {

        public static final int TIME_IN_FRAME = 50;

        Paint mPaint = null;
        Paint mTextPaint = null;
        SurfaceHolder mSurfaceHolder = null;

        boolean mRunning = false;  
        Canvas mCanvas = null;  
        private boolean mIsRunning = false,pressure_flag=false,time_flag=false;
        private Path mPath;  
      
        private float mposX, mposY,rposX,rposY;  
        private int point_counts=0,first_click=0,click_times=0;
      
        public MyView(Context context) {  
            super(context);
            this.setFocusable(true);
            this.setFocusableInTouchMode(true);
            mSurfaceHolder = this.getHolder();
            mSurfaceHolder.addCallback(this);
            mCanvas = new Canvas();
            mPaint = new Paint();
            mPaint.setColor(Color.RED);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(5);
            mPath = new Path();
            mTextPaint = new Paint();
            mTextPaint.setColor(Color.BLACK);
            mTextPaint.setTextSize(60);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            float x = event.getX();
            float y = event.getY();
            double sin_01 = 1,sin_02=1,sin_03=1,sin_04=1,sin_05=0;
            String event_name;
            //Log.d("@@@@RainTouchPanel02Activity", "##################event.getXPrecision: "+event.getXPrecision());
            //Log.d("@@@@RainTouchPanel02Activity", "##################event.getYPrecision: "+event.getYPrecision());
            //if(event.getXPrecision()>10){
            Log.d("RainTouchPanel02Activity", "xsp onTouchEvent event.getXPrecision = " + event.getXPrecision());
            //if(event.getXPrecision() > 9){
                switch (action) {
                case MotionEvent.ACTION_OUTSIDE:
                    Log.d("RainTouchPanel02Activity", "##################ACTION_OUTSIDE");
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.d("RainTouchPanel02Activity", "##################ACTION_CANCEL");
                    break;

                case MotionEvent.ACTION_POINTER_2_DOWN:
                    Log.d("RainTouchPanel02Activity", "##################ACTION_POINTER_2_DOWN");
                    break;
                case MotionEvent.ACTION_POINTER_2_UP:
                    Log.d("RainTouchPanel02Activity", "##################ACTION_POINTER_2_UP");
                    break;

                case MotionEvent.ACTION_DOWN:
                    first_click++;
                    //if(first_click==1)
                    mPath.moveTo(x, y);
                    Log.d("mPath.moveTo(x, y);################", "first_click: "+first_click);
	            break;
                case MotionEvent.ACTION_MOVE:
                    mPath.lineTo(x, y);
                    time_flag = false;
                    time_count = 0;
                    //mPath.quadTo(rposX, rposY, x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    //mPath.reset();
                    pressure_flag=true;
                    click_times++;
                    if(click_times>1){
                        TouchFor511_dis_timer.cancel();
                    }
                    TouchFor511_dis_timer = new Timer();
                    if(click_times>=1){
                        TouchFor511_dis_timer.scheduleAtFixedRate(new TimerTask(){
                            @Override
                            public void run(){
                                // TODO Auto-generated method stub
                                try{
                                    time_count++;
                                    if(time_count>1){
                                        Log.d("TouchFor511_dis################", "time_count: "+time_count);
                                        TouchFor511_dis.this.runOnUiThread(new Runnable() {					
                                            @Override
                                            public void run() {
                                               // TODO Auto-generated method stub
                                               final DialogInterface.OnClickListener AgreeDialog = new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // TODO Auto-generated method stub
                                                        //dialog.cancel();
                                                        //toast.show();
                                                        //TouchFor511_dis.this.setTestResult(1);
                                                        //autoTestNextItem(true);
                                                        TouchFor511_dis.this.finish_self();
                                                    }
                                                };

                                                final DialogInterface.OnClickListener CancelDialog = new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // TODO Auto-generated method stub
                                                        //dialog.cancel();
                                                        //toast.show();
                                                        //TouchFor511_dis.this.setTestResult(-1);
                                                        //autoTestNextItem(false);
                                                        TouchFor511_dis.this.finish_self();
                                                    }
                                                };

                                                //toast.show();
                                                /*if (alertDialogBuilder == null) {
                                                    alertDialogBuilder = new AlertDialog.Builder(TouchFor511_dis.this);
                                                    alertDialogBuilder.setIcon(R.drawable.ic_launcher);
                                                    alertDialogBuilder.setTitle("Touch Test");
                                                    alertDialogBuilder.setMessage(R.string.txt_test_pass);
                                                    alertDialogBuilder.setNegativeButton(R.string.txt_test_cancel, CancelDialog);
                                                    alertDialogBuilder.setPositiveButton(R.string.txt_test_sure, AgreeDialog);
                                                    alertDialogBuilder.create();
                                                    alertDialogBuilder.show();
                                                }*/
                                                TouchFor511_dis.this.finish_self();
                                                Log.d("TouchFor511_dis", "RRRRRRRRRRRRRRRRR TouchFor511_dis");				
                                            }
                                        });
                                    }else if(time_count==3){
                                        //TouchFor511_dis.this.setTestResult(-1);
                                        //autoTestNextItem(false);
                                        //TouchFor511_dis.this.finish();
                                    }
                                }catch (Exception e) {
                                    // TODO: handle exception
                                    //TouchFor511_dis.this.setTestResult(-1);
                                    //autoTestNextItem(false);
                                    //TouchFor511_dis.this.finish();
                                }
                            }
                        }, 0, 1000*2);
                    }
                    break;
                }
                if(pressure_flag){
                    mPath.moveTo(x, y);
                }
                rposX = x;
                rposY = y;	        	
	    /*}else{
                Log.d("TouchFor511_dis", "xsp_____onTouchEvent else!~~~~ TouchFor511_dis");
                //mCanvas.drawText("Please use your finger to test !!", 0, 120,mTextPaint);
            }*/
            Log.d("TouchFor511_dis", "xsp_____end of onTouchEvent TouchFor511_dis");
            return true;
        }

        private String getTouchFor511_disVersion_w() {
            String procVersionStr;

            /*try {
                //BufferedReader reader = new BufferedReader(new FileReader("/sys/module/TouchFor511_dis_w9002_eben/parameters/FirmVersion"), 256);
                BufferedReader reader = new BufferedReader(new FileReader("/sys/devices/platform/TouchFor511_dis_penpad.0/version"), 256);
                try {
                    procVersionStr = reader.readLine();
                } finally {
                    reader.close();
                }*/
                /*int temp = Integer.valueOf(procVersionStr);
                procVersionStr = Integer.toHexString(temp);
                return "0" + procVersionStr;*/
                /*return procVersionStr;
            } catch (IOException e) {
                Log.e("TouchFor511_dis","IO Exception when getting kernel version for Device Info screen",e);
                */return "Unavailable";
            //}
        }

        private void Draw() {
            mCanvas.drawColor(Color.WHITE);
            //mCanvas.drawPoint(rposX, rposY, mPaint) ;
            mCanvas.drawPath(mPath, mPaint);
            //mCanvas.drawPoint(rposX, rposY, mPaint);
            //mCanvas.drawText("TouchPanel_X:  " + mposX, 0, 20,mTextPaint);
            //mCanvas.drawText("TouchPanel_Y:  " + mposY, 0, 40,mTextPaint);

            //mCanvas.drawText(toString(R.string.TouchFor511_dis_draw_attention));
            //List<String> TouchFor511_disVersion = getCacheInfo();
            //mCanvas.drawText(getString(R.string.ntrig_torch_test), 0, 60,mTextPaint);
            //mCanvas.drawText("TouchFor511_dis_Version: " + tp_version_s, 0, 120,mTextPaint);
            /*if (TouchFor511_disVersion != null) {
                for (int i = 0; i < TouchFor511_disVersion.size(); i++) {
                    mCanvas.drawText("N-trig_Version:  " + TouchFor511_disVersion.get(i), 0, 60*(i + 1),mTextPaint);
                }
            }*/
            //mCanvas.drawText("N-trig_Version:  " + tp_version_s, 0, 60,mTextPaint);
        }



        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mIsRunning = true;
            new Thread(this).start();
        }

        @Override  
        public void surfaceDestroyed(SurfaceHolder holder) {
            mIsRunning = false;
        }
  
        @Override
        public void run() {
            while (mIsRunning) {
                long startTime = System.currentTimeMillis();

                synchronized (mSurfaceHolder) {

                    try{
                        mCanvas = mSurfaceHolder.lockCanvas();
                        Draw();
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    }catch (Exception e) {
                    // TODO: handle exception
                        Log.d("TouchFor511_dis", "######################### TouchFor511_dis Exception");
                        //TouchFor511_dis.this.setTestResult(-1);
                  //      autoTestNextItem(false);
                        //TouchFor511_dis.this.finish();
                    }
                }

                long endTime = System.currentTimeMillis();
                int diffTime = (int) (endTime - startTime);

                /*
                    while (diffTime <= TIME_IN_FRAME) {
                    diffTime = (int) (System.currentTimeMillis() - startTime);
                    Thread.yield();
                }
                */
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("@@@@##########Diagnosis TouchFor511_dis", "onDestroy ");
        //Modify for fix the bug 24307 by lvhongshan 20140218 start 
        if (TouchFor511_dis_timer != null) {
            TouchFor511_dis_timer.cancel();
        }
        //Modify for fix the bug 24307 by lvhongshan 20140218 end 
        super.onDestroy();
    }

    private String getCacheInfo() {
        String s = "";
        BufferedReader in = null;
        List<String> N_trig_version = new ArrayList<String>();
        try {
            Process p = Runtime.getRuntime().exec("dfu -v");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
                //s += line + "\n";
                N_trig_version.add(line);
                Log.e("getN-trig_version", "N-trig_version : \n" + line);
            }
        } catch (Exception e) {
            Log.e("getN-trig_version", "getN-trig_version has Excepiton : \n" + e.toString());
            return "00";
        } finally{
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    Log.e("getN-trig_version", "getN-trig_version has Excepiton : \n" + ioe.toString());
                }
            }
        }
        if (N_trig_version.get(2) != null) {
            return N_trig_version.get(2);
        } else {
            return "00";
        }
    }

    private synchronized void finish_self(){
        TouchFor511_dis.this.finish();
    }

    public boolean onKeyUp(int keyCode, KeyEvent msg) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //setTestResult(-1);
            TouchFor511_dis.this.finish_self();
            //autoTestNextItem(false);
        }
        return true;
    }
}
