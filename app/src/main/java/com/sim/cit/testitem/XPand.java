package com.sim.cit.testitem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

//import com.qualcomm.factory.Utilities;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
import com.sim.cit.NodeHelper;
import com.sim.cit.CommonDrive;

public class XPand extends TestActivity{

    static String TAG = "XPand";
    static String URT_TEST_STR = "test";
    boolean isUrtTesting = false;
    Button urtButton = null;
    Button usbButton = null;
    TextView mTextView = null;
    TextView mTvUrtVol = null;
    TextView mTvUrtRel = null;
    Context mContext;
    Dialog warningDialog;
    private boolean isUsbTesting = false;
    private boolean isUrtVolPass = false;
    private boolean isUrtTtyPass1 = false;
    private boolean isUrtTtyPass2 = false;
    private int oldBrightValue;

    public void onCreate(Bundle savedInstanceState) {

        layoutId=R.layout.xpand;
        super.onCreate(savedInstanceState);

        mContext = this;
        btnPass.setEnabled(false);
        NodeHelper.writeUARTNode();
        isUrtVolPass = ("0".equals(NodeHelper.readUARTNode()));
        Log.d(TAG,"onCreate isUrtVolPass is " +isUrtVolPass);
        bindView();
        try {
            oldBrightValue = Settings.System.getInt(XPand.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
            Settings.System.putInt(XPand.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE - 1);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        //isExit = true;

        if (isUrtTesting) {
            NodeHelper.disableUART();
        }
        if (isUsbTesting) {
            NodeHelper.disableUSB();
        }
        NodeHelper.writeOTG();
        Settings.System.putInt(XPand.this.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, oldBrightValue);

        super.finish();

    }

    void showWarningDialog(String title) {

        new AlertDialog.Builder(mContext).setTitle(title).setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    void bindView() {

        urtButton = (Button) findViewById(R.id.urt_bt);
        usbButton = (Button) findViewById(R.id.usb_bt);
        mTextView = (TextView) findViewById(R.id.xpand_hint);
        mTvUrtVol = (TextView) findViewById(R.id.urt3_tv);
        mTvUrtRel = (TextView) findViewById(R.id.urt_tv);
        usbButton.setEnabled(false);

        //mTvUrtRel.setText(getString(R.string.headset_to_record));
        //mTextView.setText(getString(R.string.headset_to_record));

        urtButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (isUsbTesting){
                    isUsbTesting = !(NodeHelper.disableUSB());
                    Log.d(TAG,"urtButton onClick isUsbTesting is " +isUsbTesting);
                }
                NodeHelper.writeUARTNode();
                isUrtVolPass = ("0".equals(NodeHelper.readUARTNode()));
                Log.d(TAG,"onCreate isUrtVolPass is " +isUrtVolPass);
                if (isUrtVolPass) {
                    mTvUrtVol.setText(getString(R.string.urt_vol)+" "+getString(R.string.xpand_test_pass));
                } else{
                    mTvUrtVol.setText(getString(R.string.urt_vol)+" "+getString(R.string.urtvol_test_fail));
                }

                mTextView.setText(getString(R.string.usb_hint));
                //if (isUrtVolPass) {
                    try {
                        urtButton.setEnabled(false);
                        usbButton.setEnabled(true);
                        //NodeHelper.writeUARTNode();
                        Log.d(TAG,"urtButton onClick urtTtyTest1 start");
                        String urt1 = CommonDrive.urtTtyTestOne(URT_TEST_STR);
                        String urt2 = CommonDrive.urtTtyTestTwo(URT_TEST_STR);
                        isUrtTtyPass1 = (urt1 != null && URT_TEST_STR.equals(urt1.substring(0,4)));
                        isUrtTtyPass2 = (urt2 != null && URT_TEST_STR.equals(urt2.substring(0,4)));

                        Log.d(TAG,"urtButton onClick isUrtTtyPass1 is " +isUrtTtyPass1+" and isUrtTtyPass2 is "+isUrtTtyPass2);
                        if (isUrtTtyPass1 && isUrtTtyPass2){
                            mTvUrtRel.setText(getString(R.string.urt_result)+" "+getString(R.string.xpand_test_pass));
                        } else {
                            mTvUrtRel.setText(getString(R.string.urt_result)+" "+getString(R.string.xpand_test_fail));
                        }
                        isUrtTesting = true;

                    } catch (Exception e) {
                        loge(e);
                        mTvUrtRel.setText(getString(R.string.urt_result)+" "+getString(R.string.xpand_test_fail));
                    }
                /*} else{
                    showWarningDialog(getString(R.string.urt_vol)+" "+getString(R.string.xpand_test_fail));
                }*/
                if(isUrtTtyPass1 && isUrtTtyPass2 && isUrtTesting && isUrtVolPass){
                    btnPass.setEnabled(true);
                }

            }
        });

        usbButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (isUrtTesting) {
                    isUrtTesting = !(NodeHelper.disableUART());
                    Log.d(TAG,"usbButton onClick isUrtTesting is " +isUrtTesting);
                }

                mTextView.setText(getString(R.string.usb_light));
                Log.d(TAG,"usbButton onClick start");
                    try {
                        usbButton.setEnabled(false);
                        isUsbTesting = NodeHelper.writeUSBNode();
                        Log.d(TAG,"usbButton onClick isUsbTesting is " +isUsbTesting);
                    } catch (Exception e) {
                        loge(e);
                    }
                    if (isUrtTtyPass1 && isUrtTtyPass2 && isUrtVolPass){
                        Log.d(TAG,"usbButton onClick tty test pass ");
                    } 
                    urtButton.setEnabled(true);
                        //mTextView.setText(getString(R.string.urt_hint));

                if(isUrtTtyPass1 && isUrtTtyPass2 && isUsbTesting && isUrtVolPass){
                    btnPass.setEnabled(true);
                }
                //NodeHelper.writeOTG();
            }
        });
    }

    void fail(Object msg) {

        loge(msg);
        toast(msg);
        setResult(RESULT_CANCELED);
   //     Utilities.writeCurMessage(this, TAG,"Failed");
        finish();
    }

    void pass() {

        setResult(RESULT_OK);
   //     Utilities.writeCurMessage(this, TAG,"Pass");
        finish();
    }

    public void toast(Object s) {

        if (s == null)
            return;
        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
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

    @SuppressWarnings("unused")
    private void logd(Object s) {

        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }

}
