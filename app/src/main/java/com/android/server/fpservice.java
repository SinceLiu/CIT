package com.android.server;

import android.util.Log;
//add this class for FingUnlock function by songguangyu 20140930
public class fpservice{

    private  boolean  serviceInited; 
    final int SL_SUCCESS = 0;
    private final String TAG = "Settings_fpservice";

    public fpservice() {
        serviceInited = false;
        Log.d(TAG, "settings fpservice() construction fun");
    }

    public native int InitFPService();
    public int  InitService() {
        int ret = -1;
        if (serviceInited == false ) {
            Log.d(TAG, "InitService()");
            ret = InitFPService();
            if(ret == SL_SUCCESS) {
                serviceInited = true;
            }
        }
        return ret;
    }

    public native int DeinitFPService();
    public int DeinitService() {
        int ret = -1;
        if (serviceInited == true) {
            Log.d(TAG, "DeinitService()");
            ret = DeinitFPService();
            if (ret == SL_SUCCESS) {
                serviceInited = false;
            }
        }
        return ret;
    }

    public native int EnrollCredential(int fpIndex);
    public int EnrollNewFPCredential(int fpIndex, boolean isEnabled, int cmd) {
        Log.d(TAG, "EnrollCredential()");
        return EnrollCredential(fpIndex);
    }

    public native int IdentifyCredential(int fpIndex);
    public int IdentifyUser(int fpIndex, int cmd) {
        Log.d(TAG, "IdentifyCredential()");
        return IdentifyCredential(fpIndex);
    }

    public native int RemoveCredential(int fpIndex);

    public native int EnalbeCredential(int fpIndex, boolean isEnabled);

    public native int FpCancelOperation();

    public native boolean GetEnableCredential(int fpIndex);

}
