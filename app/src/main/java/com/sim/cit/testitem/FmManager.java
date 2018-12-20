/*
 * Copyright (c) 2011-2012, QUALCOMM Incorporated.
 * All Rights Reserved.
 * QUALCOMM Proprietary and Confidential.
 * Developed by QRD Engineering team.
 */

package com.sim.cit.testitem;

import android.content.Context;
import android.content.Intent;
import qcom.fmradio.FmConfig;
import qcom.fmradio.FmReceiver;
import qcom.fmradio.FmRxEvCallbacksAdaptor;
import android.os.Handler;
import android.util.Log;

public class FmManager {

    public static FmReceiver mReceiver = null;
    private static final String RADIO_DEVICE = "/dev/radio0";
    private static boolean mFmOn = false;
    static String TAG = "FM";
//    private int frequency = 98500;//original code
    private int mfrequency;//new code
    Context mContext = null;
    Handler mHandler;

    public FmManager(Context context, Handler handler, int frequency) {

        mContext = context;
        mHandler = handler;
        mfrequency = frequency;
    }

    public int getFrequency() {
        return mfrequency;
    }

    public FmConfig getFmDefConfig() {

        FmConfig mFmConfig = new FmConfig();
        mFmConfig.setRadioBand(FmReceiver.FM_USER_DEFINED_BAND);
        mFmConfig.setChSpacing(FmReceiver.FM_CHSPACE_100_KHZ);
        mFmConfig.setEmphasis(FmReceiver.FM_DE_EMP50);
        mFmConfig.setRdsStd(FmReceiver.FM_RDS_STD_RDS);
        mFmConfig.setLowerLimit(87500);
        mFmConfig.setUpperLimit(108000);
        return mFmConfig;
    }

    FmRxEvCallbacksAdaptor mFmRxEvCallbacksAdaptor = new FmRxEvCallbacksAdaptor() {
        public void FmRxEvSearchComplete(int freq) {
            FmManager.this.mfrequency = freq;
            logd(freq);
            mHandler.sendEmptyMessage(0);
        }

        public void FmRxEvRadioTuneStatus(int freq) {
            FmManager.this.mfrequency = freq;
            logd(freq);
            mHandler.sendEmptyMessage(0);
        };
    };

    public boolean openFM() {

        boolean ret = false;

        if (mReceiver == null) {
            try {
                mReceiver = new FmReceiver(RADIO_DEVICE, mFmRxEvCallbacksAdaptor);
            } catch (InstantiationException e) {
                loge(e);
            }
        }

        if (mReceiver != null) {
            if (isFmOn()) {
                ret = true;
            } else {
                logd("to enable");
                FmConfig mFmConfig = getFmDefConfig();
                ret = mReceiver.enable(mFmConfig,mContext);
                if (ret) {
                    mFmOn = true;
                    logd("heare");
                   // Intent mIntent = new Intent("android.intent.action.FM");
                   // mIntent.putExtra("state", 1);
                   // mContext.sendBroadcast(mIntent);
                }
            }
        }
        logd(ret);
        return ret;
    }
    public void test(boolean t) {
        // Add fix CIT bug by lishuai 20140106 start
        if (mReceiver != null){
            mReceiver.setAnalogMode(t);
        }
        // Add fix CIT bug by lishuai 20140106 start
    }
    public boolean closeFM() {

        boolean ret = false;

        if (mReceiver != null) {
            ret = mReceiver.disable();
            Intent mIntent = new Intent("android.intent.action.FM");
            mIntent.putExtra("state", 0);
            mContext.sendBroadcast(mIntent);
            mReceiver = null;
        }
        logd(ret);
        if (ret) {
            mFmOn = false;
        }
        return ret;
    }

    public boolean isFmOn() {

        return mFmOn;
    }

    public int searchUP() {

        if (mReceiver != null) {
            try {
                mReceiver.searchStations(FmReceiver.FM_RX_SRCH_MODE_SEEK, FmReceiver.FM_RX_DWELL_PERIOD_1S,
                        FmReceiver.FM_RX_SEARCHDIR_UP);
            } catch (Exception e) {
                loge(e);
            }
        } else {
            logd("searchUP: mReceiver is null");
        }
        logd("searchUP: " + getFrequency());
        return getFrequency();
    }

    public int searchDown() {

        if (mReceiver != null) {
            try {
                mReceiver.searchStations(FmReceiver.FM_RX_SRCH_MODE_SEEK, FmReceiver.FM_RX_DWELL_PERIOD_1S,
                        FmReceiver.FM_RX_SEARCHDIR_DOWN);
            } catch (Exception e) {
                loge(e);
            }
        } else {
            logd("searchDown: mReceiver is null");
        }
        logd("searchDown: " + getFrequency());
        return getFrequency();
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

}

