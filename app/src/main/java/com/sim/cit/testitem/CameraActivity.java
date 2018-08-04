package com.sim.cit.testitem;

import android.app.Activity;
import android.os.Bundle;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera;
import android.view.SurfaceHolder.Callback;
import android.content.pm.ActivityInfo;
import android.view.Window;
import android.view.WindowManager;

import com.sim.cit.CITTestHelper;
import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class CameraActivity extends TestActivity {



    private Camera camera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        layoutId = R.layout.activity_camera;
        super.onCreate(savedInstanceState);

        SurfaceView surfaceView = (SurfaceView) this
            .findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceCallback());

    }

    private final class SurfaceCallback implements Callback {
        private boolean preview;
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
        }
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera = Camera.open(2);
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                preview = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                if (preview) {
                    camera.stopPreview();
                    preview = false;
                }
                camera.release();
                camera = null;
            }
        }
    }


}

