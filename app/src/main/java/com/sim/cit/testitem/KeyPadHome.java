package com.sim.cit.testitem;


import com.sim.cit.R;
import com.sim.cit.TestActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
//import android.os.LocalPowerManager;
import android.os.IPowerManager;
import android.os.ServiceManager;
import android.os.RemoteException;
//import com.android.server.PowerManagerService;

public class KeyPadHome extends TestActivity{
    String TAG = "TouchPanelKey";
    final static int MENU = 1;
    final static int HOME = 2;
    final static int SEARCH = 3;
    final static int UP=4;
    final static int DOWN=5;
    final static int HEADSET=6;
    final static int BACK=7;

    int key = MENU;
    Panel mPanel;
    WindowManager mWindowManager;
    public IPowerManager pw;
    private boolean isTest=false;
    public TurnOnThread turnOnThread = null;

    @Override
    public void finish() {

        isTest = false;
        super.finish();
    }

    private void init() {

        mPanel = new Panel(getApplicationContext());
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(mPanel);
    }

    public void onCreate(Bundle savedInstanceState) {

    	layoutId=0;
        super.onCreate(savedInstanceState);
        init();
        /*20130124 modify for let the buttonLight turning on by lvhongshan start*/
        pw = IPowerManager.Stub.asInterface(
                            ServiceManager.getService("power"));
        isTest = true;
   /*     turnOnThread = new TurnOnThread();
        turnOnThread.start();*/
        /*20130124 modify for let the buttonLight turning on by lvhongshan end*/
   //     Toast.makeText(this, getString(R.string.keypad_lightison), Toast.LENGTH_LONG).show();

    }
    
    class Panel extends View {

        Paint mPaint = new Paint();

        public Panel(Context context) {
            
            super(context);
        }

        public void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        public void onDraw(Canvas canvas) {

            super.onDraw(canvas);
            // get panel size
            DisplayMetrics mDisplayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
            int heightPix = mDisplayMetrics.heightPixels, widthPix = mDisplayMetrics.widthPixels;
            mPaint.setTextSize(widthPix / 20);
            mPaint.setColor(Color.MAGENTA);
            
            getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
            switch (key) {
            /*20121225 modified for add keyMenu test by lvhongshan start*/
            case MENU:
                canvas.drawText("Please Press <MENU KEY>", 0, heightPix - widthPix / 20, mPaint);
                break;
            /*20121225 modified for add keyMenu test by lvhongshan end*/
            case HOME:
                canvas.drawText("Please Press <HOME KEY>", 0, heightPix - widthPix / 20, mPaint);
                break;
            case SEARCH:
                canvas.drawText("Please Press <SEARCH KEY>", 0, heightPix - widthPix / 20, mPaint);
                break;
         /*20130418 modified for add KeyUp and keyDown test by lvhongshan start*/
            case UP:
                canvas.drawText("Please Press <UP KEY>", 0, heightPix - widthPix / 20, mPaint);
                break;
            case DOWN:
                canvas.drawText("Please Press <DOWN KEY>", 0, heightPix - widthPix / 20, mPaint);
                break;
          /*20130418 modified for add KeyUp and keyDown test by lvhongshan end*/
          /*20121210 modified for add Keyheadset test by lvhongshan start*/
            case HEADSET:
                canvas.drawText("Please Press <HEADSET KEY>", 0, heightPix - widthPix / 20, mPaint);
                break;   
          /*20121210 modified for add Keyheadset test by lvhongshan end*/                
            case BACK:
                canvas.drawText("Please Press <BACK KEY>", 0, heightPix - widthPix / 20, mPaint);
                break;
            default:
                break;
            }
        }

    }

    public boolean onKeyUp(int keyCode, KeyEvent msg) {

        logd(keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK && key != BACK) {
            setResult(RESULT_CANCELED);
       //     Utilities.writeCurMessage(this, TAG, "Failed");
         //   finish();
            autoTestNextItem(false);
        }
        switch (key) {
        /*20121214 modified for remove keyMenu test by lvhongshan start*/
        case MENU:
            if (keyCode == KeyEvent.KEYCODE_MENU)
                key = HOME;
            break;
        /*20121214 modified for remove keyMenu test by lvhongshan end*/
        case HOME:
            if (keyCode == KeyEvent.KEYCODE_HOME)
	    //Remove search key test in touch panel key test start.
            //    key = HEADSET;
                key = UP;
            //Remove search key test in touch panel key test end.
            break;
        case SEARCH:
            if (keyCode == KeyEvent.KEYCODE_SEARCH)
              //  key = BACK;
            break;
        /*20130418 modified for add KeyUp and keyDown test by lvhongshan start*/
        case UP:
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                key = DOWN;
            break;
        case DOWN:
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                key = HEADSET;
            break;
        /*20130418 modified for add KeyUp and keyDown test by lvhongshan end*/
        /*20121210 modified for add Keyheadset test by lvhongshan start*/
        case HEADSET:
            Log.i(TAG,"keycode is "+keyCode);
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK)
                key = BACK;
            break;
        /*20121210 modified for add Keyheadset test by lvhongshan end*/
            
        case BACK:
            if(keyCode == KeyEvent.KEYCODE_BACK){
                setResult(RESULT_OK);
         //   Utilities.writeCurMessage(this, TAG, "Pass");
           // finish();
                autoTestNextItem(true);
            }
        }
        mPanel.invalidate();
        return true;
    }

    private void loge(Object e) {

        if (e == null)
            return;
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();
        e = "[" + mMethodName + "] " + e;
        Log.e(TAG, e + "");
    }

    private void logd(Object s) {
        
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }

    /*20130124 modify for let the buttonLight turning on by lvhongshan start*/
    class TurnOnThread extends Thread{

        public void run(){
        /*       while(isTest){
                    Log.i(TAG,"isTest= "+ isTest);
                    //mBatteryLight.setBrightness(mButtonBrightness);
                    if(pw!=null){
                         try {
                              // power.setBacklightBrightness(brightness);
                              pw.setBacklightBrightness(150);
                         } catch (RemoteException doe) {
                         }
                    }
                    try {
                              Thread.sleep(50);
                         } catch (InterruptedException ite) {
                         }
               }
               if(!isTest){
                    if(pw!=null){
                        try {
                              // power.setBacklightBrightness(brightness);
                              pw.setBacklightBrightness(150);
                        } catch (RemoteException doe) {
                        }
                   }
               }*/
        }
    }
    /*20130124 modify for let the buttonLight turning on by lvhongshan end*/
}
