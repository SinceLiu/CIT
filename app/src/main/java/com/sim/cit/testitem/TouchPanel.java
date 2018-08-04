package com.sim.cit.testitem;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.util.Log;

import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class TouchPanel extends TestActivity{

    public static String TAG = "TouchPanel";
    private static Paint mPaint;
    private static Paint mLinePaint;
    public static LinearLayout llPF;
    //private TouchView touchView;
    private TouchViewRect touchView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen();
        getScreenMetries();
        layoutId = R.layout.touch_screen;
        super.onCreate(savedInstanceState);

        llPF = (LinearLayout)findViewById(R.id.ll_p_f);
        touchView = (TouchViewRect)findViewById(R.id.tsv_touch_screen);
        llPF.setVisibility(View.INVISIBLE);
        mPaint = new Paint();
        setPaintProperty(mPaint,Color.RED,2);

        mLinePaint = new Paint();
        setPaintProperty(mLinePaint,Color.BLACK,1);
        Toast.makeText(this, R.string.touch_illustration1, 3000).show();
    }
    private void setPaintProperty(Paint paint,int color,int width){
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
    }
    private void getScreenMetries(){
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
        TouchViewRect.screen_X = displaysMetrics.widthPixels;
        TouchViewRect.screen_Y = displaysMetrics.heightPixels;
    }
    private void setFullscreen() {   
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        WindowManager.LayoutParams.FLAG_FULLSCREEN);   
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK||keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            llPF.setVisibility(View.VISIBLE);
	}
        //if (keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_HOME) {
        if (keyCode==KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
