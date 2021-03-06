/*
 * Copyright (c) 2011-2012, QUALCOMM Incorporated.
 * All Rights Reserved.
 * QUALCOMM Proprietary and Confidential.
 * Developed by QRD Engineering team.
 */

package com.sim.cit.testitem;

//import static com.qualcomm.factory.Values.LOG;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
//add for mark test results by songguangyu 20140220 start
import com.sim.cit.CITTestHelper;
//add for mark test results by songguangyu 20140220 end

import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class CameraFront extends TestActivity implements SurfaceHolder.Callback {

    private Camera mCamera = null;
    private Button takeButton, passButton, failButton;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    final static String TAG = "CameraFront";
    private static Context mContext = null;
    public static final boolean LOG = true;


    @Override
    public void finish() {

        stopCamera();
        super.finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        layoutId=0;
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.camera_front);

        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(CameraFront.this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        mContext = this;
        bindView();

    }

    void bindView() {

        takeButton = (Button) findViewById(R.id.take_picture);
        passButton = (Button) findViewById(R.id.camera_pass);
        failButton = (Button) findViewById(R.id.camera_fail);
        takeButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {

                takeButton.setVisibility(View.GONE);
                try {
                    if (mCamera != null) {
                        // mCamera.autoFocus(new AutoFocusCallback());
                        takePicture();
                    } else {
                        finish();
                    }
                } catch (Exception e) {
                    loge(e.toString());
                }
            }
        });

        passButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {

                setResult(RESULT_OK);
                stopCamera();
                autoTestNextItem(true);
                //add for mark test results by songguangyu 20140220 start
                setTestResult(CITTestHelper.TEST_RESULT_PASS);
                //add for mark test results by songguangyu 20140220 end
//                Utilities.writeCurMessage(mContext, TAG, "Pass");
//                finish();
            }
        });
        failButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {

                setResult(RESULT_CANCELED);
                stopCamera();
                autoTestNextItem(false);
                //add for mark test results by songguangyu 20140220 start
                setTestResult(CITTestHelper.TEST_RESULT_FAIL);
                //add for mark test results by songguangyu 20140220 end
//                Utilities.writeCurMessage(mContext, TAG, "Failed");
//                finish();
            }
        });

    }

    public void surfaceCreated(SurfaceHolder surfaceholder) {
        int oritationAdjust = 180;
//        String hwPlatform = SystemProperties.get("ro.hw_platform");
//        if (Values.PRODUCT_MSM8X25_SKU5.equals(hwPlatform)) {
            // oritationAdjust = 180;
//        }
        logd("surfaceCreated");
        try {
        	int cameraId = getCameraId(false);
            mCamera = Camera.open(cameraId);
            //mCamera.setDisplayOrientation(oritationAdjust);
        } catch (Exception exception) {
            showToast(getString(R.string.cameraback_fail_open));
            mCamera = null;
        }

        if (mCamera == null) {
            finish();
        } else {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            //    mCamera.setDisplayOrientation(180);
            } catch (IOException exception) {
                mCamera.release();
                mCamera = null;
                finish();
            }
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w, int h) {

        logd("surfaceChanged");
        startCamera();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {

        logd("surfaceDestroyed");
        stopCamera();
    }

    private void takePicture() {

        logd("takePicture");
        if (mCamera != null) {
            try {
                mCamera.takePicture(mShutterCallback, rawPictureCallback, jpegCallback);
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        } else {
            finish();
        }
    }

    private ShutterCallback mShutterCallback = new ShutterCallback() {

        public void onShutter() {

            logd("onShutter");
            try {
                takeButton.setVisibility(View.GONE);
                passButton.setVisibility(View.VISIBLE);
                failButton.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                loge(e);
            }
        }
    };

    private PictureCallback rawPictureCallback = new PictureCallback() {

        public void onPictureTaken(byte[] _data, Camera _camera) {
            logd("rawPictureCallback onPictureTaken");
            try {
                takeButton.setVisibility(View.GONE);
                passButton.setVisibility(View.VISIBLE);
                failButton.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                loge(e);
            }
        }
    };

    private PictureCallback jpegCallback = new PictureCallback() {

        public void onPictureTaken(byte[] _data, Camera _camera) {
            logd("jpegCallback onPictureTaken");
            try {
                takeButton.setVisibility(View.GONE);
                passButton.setVisibility(View.VISIBLE);
                failButton.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                loge(e);
            }
        }
    };

    public final class AutoFocusCallback implements android.hardware.Camera.AutoFocusCallback {

        public void onAutoFocus(boolean focused, Camera camera) {

            if (focused) {
                takePicture();
            }
        }
    };

    private void startCamera() {

        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//                parameters.setRotation(CameraInfo.CAMERA_FACING_FRONT);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
                loge(e);
            }
        }

    }

    private void stopCamera() {

        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void logd(Object d) {

        if (LOG)
            Log.d(TAG, d + "");
    }

    private void loge(Object e) {

        if (LOG)
            Log.e(TAG, e + "");
    }

    private void showToast(String str) {

        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    
    private int getCameraId(boolean backCamera){
		//切换前后摄像头
        int cameraCount = 0;
        CameraInfo cameraInfo = new CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        Log.i(TAG, "----------------------cameraCount = "+cameraCount);
		Log.i(TAG, "---------CAMERA_FACING_BACK = " + Camera.CameraInfo.CAMERA_FACING_BACK
				+ "------CAMERA_FACING_BACK = " + Camera.CameraInfo.CAMERA_FACING_FRONT);
        for(int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            Log.i(TAG, "----------------------cameraInfo.facing = "+cameraInfo.facing);
            if(backCamera && cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK){
            	return i;
            }else if (!backCamera && cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				return i;
			}
        }
        Log.i(TAG, "----------------------no id");
        return -11111;
	}
}
