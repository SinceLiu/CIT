package com.sim.cit.testitem;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.util.Log;
import com.sim.cit.CITTestHelper;
import com.sim.cit.CommonDrive;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import com.android.server.fpservice;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import java.nio.ByteBuffer;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.os.Message;
import android.os.Handler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class FingerPrintCali extends TestActivity implements OnClickListener{
    private static final String TAG = "FingerPrintCali";
    private static final int FINGER_IMAGE_WIDTH = 256;//256
    private static final int FINGER_IMAGE_HEIGHT = 360;//360
    private static final int FINGER_BITMAP_WIDTH = 256;//256
    private static final int FINGER_BITMAP_HEIGHT = 360;//360
    private static final float FINGER_WSCALE = FINGER_BITMAP_WIDTH/(FINGER_IMAGE_WIDTH*1.0f);
    private static final float FINGER_HSCALE = FINGER_BITMAP_HEIGHT/(FINGER_IMAGE_HEIGHT*1.0f);
    private static final int MSG_SHOWIMAGE = 0xAA55;
    private static final int MSG_STOPIMAGE = 0xAA56;
    private static final int MSG_UPDATE_ENROLL = 201;

    final int Max_Enroll_Touch_Count = 8;
    final int SL_ENROLL   = 0x8001;
    private int Enroll_count = 0;
    private int enrollResult = 0;
 
    private Button btnFpCali;
    private Button btnFpStart;
    private Button btnFpStop;
    private Button btnPass;
    //private TCS mTcs;
    private LinearLayout mFingerImageLayout;
    private FingerView mFingerView;
    private Bitmap mBitmap;
    private ByteBuffer dst;
    private Canvas canvas;
    private Paint paint;
    private Matrix matrix;
    private byte[] mbuffer;
    private Bitmap mImageBitmap;
    private Bitmap mBitmapWhite;
    private boolean threadRunning;
    private fpservice mjni = null;
    private Thread enrollThread;
    private MyHandler mHandler = null;
    private int oldBrightValue;

    static {
        System.loadLibrary("SL_fp");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        int state;

        layoutId=R.layout.fingerprintcali;
        super.onCreate(savedInstanceState);
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
        mjni = new fpservice();
        mHandler = new MyHandler();

        try {
            oldBrightValue = Settings.System.getInt(FingerPrintCali.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
            Settings.System.putInt(FingerPrintCali.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE - 1);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        try {
            mbuffer = new byte[FINGER_IMAGE_WIDTH * FINGER_IMAGE_HEIGHT];
            mBitmap = Bitmap.createBitmap(FINGER_IMAGE_WIDTH, FINGER_IMAGE_HEIGHT, Bitmap.Config.ALPHA_8);
            dst = ByteBuffer.allocate(FINGER_IMAGE_WIDTH * FINGER_IMAGE_HEIGHT);
            matrix = new Matrix();
            matrix.postScale(FINGER_WSCALE, FINGER_HSCALE);	
            mImageBitmap = Bitmap.createBitmap(FINGER_BITMAP_WIDTH,FINGER_BITMAP_HEIGHT, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(mImageBitmap);
            paint = new Paint();
            paint.setColor(Color.WHITE);  

            mFingerImageLayout = (LinearLayout)findViewById(R.id.finger_image_layout);
            mFingerView = new FingerView(this);
            mFingerView.setLayoutParams(new LayoutParams(256,360));//new LayoutParams(256,360)
            mFingerImageLayout.addView(mFingerView);

            mBitmapWhite = BitmapFactory.decodeResource(getResources(),R.raw.white);
            mFingerView.setImageBitmap(mBitmapWhite);

            btnFpCali = (Button)findViewById(R.id.btn_fpcali);
            btnFpCali.setOnClickListener(this);
            btnFpCali.setVisibility(View.GONE);
            btnFpStart = (Button)findViewById(R.id.btn_fpstart);
            btnFpStart.setOnClickListener(this);
            btnFpStop = (Button)findViewById(R.id.btn_fpstop);
            btnFpStop.setOnClickListener(this);
            btnFpStop.setVisibility(View.GONE);

            mjni.InitService();//init fingerprint service
            threadRunning = false;
            //mTcs.TcsSetNVMFile("/data/data/nvm");
            //state =  mTcs.TcsSenSorInit();
            /*if (state != 0)
            {
                Toast.makeText(this, R.string.fail, Toast.LENGTH_SHORT).show();
            }
            else
            {
                //set power state
                mTcs.TcsSetState(TCS.STANDBY_MODE);
                Log.v(TAG, "TcsSoftwareVersion =" + mTcs.TcsSoftwareVersion());
            }

            Runtime.getRuntime().exec("chmod 777  /data/data/nvm");*/
            }
        catch(Exception e)
        {
            Log.v(TAG, "in oncreate err="+e.toString());
            btnFpStart.setEnabled(false);
            btnFpStop.setEnabled(false);
            btnFpCali.setEnabled(false);
        }
    }

    /*private void stopfinger(final TCS Tcs) {
        threadRunning = false;
    }*/

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_fpcali:
            btnFpStart.setEnabled(false);
            btnFpStop.setEnabled(false);
            btnFpCali.setEnabled(false);
            btnPass.setEnabled(false);
            btnFail.setEnabled(false);
            Toast.makeText(getApplicationContext(), R.string.fp_startcali, Toast.LENGTH_SHORT).show();
            //califinger(mTcs);
        break;

        case R.id.btn_fpstart:
            //btnFpCali.setEnabled(false);
            btnFpStart.setEnabled(false);
            //startfinger(mTcs);
            addFinger();
        break;

        case R.id.btn_fpstop:
            //stopfinger(mTcs);
            btnFpStart.setEnabled(true);
            btnFpCali.setEnabled(true);
        break;
        }//end switch
    }

    class MyHandler extends Handler{
        public void handleMessage(Message msg){
            switch(msg.what){
                case MSG_UPDATE_ENROLL:
                    Log.d(TAG,"msg.arg1 = " + msg.arg1);
                    /*if(msg.arg1 < 360){
                        pw.setProgress(msg.arg1);
                    }*/
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        mImageBitmap = BitmapFactory.decodeFile("/data/local/tmp/template.bmp");
                        btnPass.setEnabled(true);
                        msg = mHandler.obtainMessage();
                        msg.what = MSG_SHOWIMAGE;
                        mHandler.sendMessageDelayed(msg, 500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_SHOWIMAGE:
                    try {
                        mFingerView.setImageBitmap(big (mImageBitmap,256f,360f));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_STOPIMAGE:
                    mFingerView.setImageBitmap(mBitmapWhite);
                    break;
                default:
                    break;
            }
        }
    }
    public static Bitmap big(Bitmap b,float x,float y) {
        int w = b.getWidth();
        int h = b.getHeight();
        Log.i(TAG, "big() width is "+ w +", height is "+ h);
        float sx = (float)x/w;
        float sy = (float)y/h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
        return resizeBmp;
    }
    private void addFinger() {
        threadRunning = true;
        enrollThread = new Thread(mRunnable);
        enrollThread.start();
    }

    Runnable mRunnable = new Runnable(){
        public void run(){
            //mHandler.removeMessages(MSG_ENROLL_UNPASS);
            EnrollFpLoop();
        }
    };

    public void EnrollFpLoop() {
        Log.w(TAG, "EnrollFpLoop(), BEGIN");
        //first get fpIndex
        //int fpIndex = findFpDataMinIndex();
        int fpIndex = 4;
        Log.d(TAG,"fpIndex = "+ fpIndex);
        /*t1.setText("Finger input");
        t1.setTextColor(Color.BLACK);*/
        while(threadRunning){
            Log.d(TAG, "EnrollFpLoop, threadRunning == " + threadRunning);
            //if(Enroll_count < Max_Enroll_Touch_Count){//s
                //default enable value: true
                enrollResult = mjni.EnrollNewFPCredential(fpIndex, true, SL_ENROLL);
                Log.d(TAG, "FingerInputActivity, EnrollFpLoop, enrollResult == " + enrollResult);
                /*if(enrollResult < 0){
                    if(Enroll_count < Max_Enroll_Touch_Count - 1){
                        if(Enroll_count != 0 ){
                            Message msg = mHandler.obtainMessage();
                            msg.what = MSG_ENROLL_UNPASS;
                            mHandler.sendMessage(msg);
                        }
                    }
                    Enroll_count++;
                    Log.d(TAG, "Enroll_count = " + Enroll_count);
                    continue;
                }*/
                //add vibrate
                /*vibrator = (Vibrator)getApplication().getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);*/
                //because enrollResult is from 0 to 100
                //enrollResult = enrollResult * 36;//s
                //enrollResult = enrollResult / 10;//s
                //if((enrollResult > 0) && (enrollResult < 360)){//s
                if(enrollResult > 0){
                    Log.d(TAG, "enrollResult = " + enrollResult);
                    //enable button
                    Message msg = mHandler.obtainMessage();
                    //Message msg = new Message();
                    msg.what = MSG_UPDATE_ENROLL;
                    msg.arg1  = enrollResult;
                    mHandler.sendMessage(msg);
                }
                /*if(enrollResult >= 360){
                    Log.d(TAG, "enrollResult = " + enrollResult);
                    Log.d(TAG,"enroll success, enroll times = " + Enroll_count);
                    Message msg = mHandler.obtainMessage();
                    //Message msg = new Message();
                    msg.what = MSG_ENROLL_FINISH;
                    msg.arg1  = enrollResult;
                    mHandler.sendMessage(msg);
                    break;
                }*///s
            /*}else{//s
                //count is MaxCount
                Log.e(TAG,"Enroll times surpass Max_Enroll_Touch_Count");
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_ENROLL_ERROR;
                mHandler.sendMessage(msg);
                break;
            }*/
        }
        Log.d(TAG, "EnrollFpLoop(), END");
    }

    protected void onDestroy() {
        //Log.v(TAG, "TcsSenSorExit =" + mTcs.TcsSenSorExit());
 
        if (mBitmap != null && mBitmap.isRecycled())
        {
            mBitmap.recycle();
            mBitmap = null;
        }
		
        if (mImageBitmap != null && mImageBitmap.isRecycled())
        {
            mImageBitmap.recycle();
            mImageBitmap = null;
        }

        if (mBitmapWhite != null && mBitmapWhite.isRecycled())
        {
            mBitmapWhite.recycle();
            mBitmapWhite = null;
        }

        threadRunning = false;

        Settings.System.putInt(FingerPrintCali.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, oldBrightValue);
        super.onDestroy();
    }

    protected void onPause() {		
        threadRunning = false;
        mjni.FpCancelOperation();
        mjni.DeinitService();
        super.onPause();
    }
}
