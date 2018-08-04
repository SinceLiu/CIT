package com.sim.cit.testitem;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.MotionEvent;

import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class Lcd extends TestActivity{
	private LcdView lcdvLcd;
	private TextView tvLcdIllustration;
	private LinearLayout llPF;
	
	private boolean isFirst = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getScreenMetries();
		layoutId = R.layout.lcd;
		super.onCreate(savedInstanceState);
		
		lcdvLcd = (LcdView)findViewById(R.id.lcdv_lcd);
		tvLcdIllustration = (TextView)findViewById(R.id.txt_lcd_illustration);
		llPF = (LinearLayout)findViewById(R.id.ll_p_f);
		
		lcdvLcd.setVisibility(View.INVISIBLE);
		llPF.setVisibility(View.INVISIBLE);
	}
	
	private void getScreenMetries() {
		DisplayMetrics displaysMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
		
		LcdView.screen_X = displaysMetrics.widthPixels;
		LcdView.screen_Y = displaysMetrics.heightPixels;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
        //        if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (isFirst) {
				lcdvLcd.setVisibility(View.VISIBLE);
				tvLcdIllustration.setVisibility(View.INVISIBLE);
				isFirst = false;
				return true;
			}
			lcdvLcd.biListIndex ++;
			lcdvLcd.invalidate();
			if (lcdvLcd.isLastColor) {
				llPF.setVisibility(View.VISIBLE);
			}
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final int mAction = event.getAction();
        if ((mAction == MotionEvent.ACTION_UP)) {

            if (isFirst) {
                lcdvLcd.setVisibility(View.VISIBLE);
                tvLcdIllustration.setVisibility(View.INVISIBLE);
                isFirst = false;
                return true;
            }
            lcdvLcd.biListIndex ++;
            lcdvLcd.invalidate();
            if (lcdvLcd.isLastColor) {
                llPF.setVisibility(View.VISIBLE);
            }

        }
        return true;
    }
}
