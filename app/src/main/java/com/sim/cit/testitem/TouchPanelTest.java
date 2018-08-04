package com.sim.cit.testitem;

import android.app.Activity;
import android.os.Bundle;
import com.sim.cit.R;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.Display;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import java.util.Stack;

public class TouchPanelTest extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setFullScreen();
		setTestPoint();
		initStack();
		test_view = new TPTestView(this);
        setContentView(test_view);
        mContext = getApplicationContext();
    }

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		menu.add(0,TEST_AGAIN,0,R.string.retest);
		menu.add(0,TEST_SUCCESS,0,R.string.success);
		menu.add(0,TEST_FAIL,0,R.string.fail);

		return true;
	}*/

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case TEST_AGAIN:
				test_view.clean();
				initStack();
				return true;
			case TEST_SUCCESS:
				finishTest(true);
				return true;
			case TEST_FAIL:
				finishTest(false);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}*/

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		test_view.free();
	}

	private void setFullScreen()
	{
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private void checkCurrentXY(float x,float y)
	{
        android.util.Log.e("xsp_TPT", "checkCurrentXY start");
		if(pStack.empty())
		{
			showTestSuccess();
			return;
		}
		Point top_point = (Point)pStack.peek();
        android.util.Log.e("xsp_TPT", Math.abs((int)x - top_point.x) + " && "+ Math.abs((int)y - top_point.y));
        android.util.Log.e("xsp_TPT", "point x =["+top_point.x + "],point y =[" + top_point.y+"]");
		if( Math.abs((int)x - top_point.x) <= TEST_TOLERANCE && Math.abs((int)y - top_point.y) <= TEST_TOLERANCE )
		{
			pStack.pop();
            android.util.Log.e("xsp_TPT", "stack pop");
		}
        android.util.Log.e("xsp_TPT", "checkCurrentXY end");
	}

	private synchronized void showTestSuccess()
	{
		Toast.makeText(this,"TP test Successful!",Toast.LENGTH_SHORT).show();
		finishTest(true);
	}

	private void finishTest(boolean result)
	{
        Intent tp_intent = new Intent();
        tp_intent.setAction("ACTION_TP_PASS");
        mContext.sendBroadcast(tp_intent);
		finish();
	}

	private void initStack()
	{
		pStack = new Stack();
		if(null == test_point)
		{
			return;
		}
		for(int[] tmp:test_point)
		{
			pStack.addElement(new Point(tmp[0],tmp[1]));
		}
	}

	private void setTestPoint()
	{
		Display display = getWindowManager().getDefaultDisplay();
		int w = display.getWidth();
		int h = display.getHeight();
		test_point = new int[][]{
			{  (int)(w/4*3),   (int)(h/4)},
			{    (int)(w/4), (int)(h/4*3)},
			{  (int)(w/4*3), (int)(h/4*3)},
			{    (int)(w/2),   (int)(h/2)},
			{    (int)(w/4),   (int)(h/4)},
			{           100,   (int)(h/4)},
			{           100,   (int)(h/2)},
			{           100, (int)(h/4*3)},
			{           100,            h-100},
			{    (int)(w/2),            h-100},
			{             w-100,            h-100},
			{             w-100, (int)(h/4*3)},
			{             w-100,   (int)(h/2)},
			{             w-100,   (int)(h/4)},
			{             w-100,            100},
			{    (int)(w/2),            100},
			{             100,            100}
		};
	}

	private TPTestView test_view;
	private Stack pStack;
	private final int TEST_TOLERANCE = 100;
	private final int TEST_AGAIN = 0;
	private final int TEST_SUCCESS = TEST_AGAIN + 1;
	private final int TEST_FAIL = TEST_SUCCESS + 1;
	private int test_point[][];
    private Context mContext;

	public class Point
	{
		public int x;
		public int y;

		Point(int x,int y)
		{
			this.x = x;
			this.y = y;
		}
	}
		
	public class TPTestView extends View
	{
		private Bitmap  mBitmap;
		private Canvas 	mCanvas;
		private Path    mPath;
		private Paint  	mBitmapPaint;
		private Paint 	mPaint;
		private Paint   textPaint;
		private String  str;
		private int     width,height;
		private float   mX,mY;
		private static final float TOUCH_TOLERANCE = 10;

		public TPTestView(Context c)
		{
			super(c);
			Display display = getWindowManager().getDefaultDisplay();
			width = display.getWidth();
			height = display.getHeight();
			mBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mPath = new Path();
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			str = getResources().getString(R.string.touch_panel_test);
			setBackgroundColor(Color.TRANSPARENT);
			setBackgroundResource(R.drawable.touch_panel_test_bg);
			setPaint();
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			/*canvas.drawText(str,width/2,height/2+32,textPaint);

            canvas.drawText(0 + "", 20, 20, textPaint);
            canvas.drawText(1 + "", 540, 20, textPaint);
            canvas.drawText(2 + "", 1060, 20, textPaint);
            canvas.drawText(3 + "", 1060, 480, textPaint);
            canvas.drawText(4 + "", 1060, 960, textPaint);
            canvas.drawText(5 + "", 1060, 1440, textPaint);
            canvas.drawText(6 + "", 1060, 1900, textPaint);
            canvas.drawText(7 + "", 540, 1900, textPaint);
            canvas.drawText(8 + "", 20, 1900, textPaint);
            canvas.drawText(9 + "", 20, 1440, textPaint);
            canvas.drawText(10 + "", 20, 960, textPaint);
            canvas.drawText(11 + "", 20, 480, textPaint);
            canvas.drawText(12 + "", 270, 480, textPaint);
            canvas.drawText(13 + "", 540, 960, textPaint);
            canvas.drawText(14 + "", 810, 1440, textPaint);
            canvas.drawText(15 + "", 270, 1440, textPaint);
            canvas.drawText(16 + "", 810, 480, textPaint);*/
            

			//canvas.drawBitmap(mBitmap,0,0,mBitmapPaint);
			canvas.drawPath(mPath,mPaint);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			float x = event.getX();
			float y = event.getY();
			
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					touch_start(x,y);
                    android.util.Log.e("xsp_TPT", "onTouch down , and x = [" + x +"]; y = [" + y + "]");
					invalidate();
					break;
				case MotionEvent.ACTION_MOVE:
					touch_move(x,y);
                    android.util.Log.e("xsp_TPT", "onTouch move , and x = [" + x +"]; y = [" + y + "]");
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					touch_up(x,y);
                    android.util.Log.e("xsp_TPT", "onTouch up , and x = [" + x +"]; y = [" + y + "]");
					invalidate();
					break;
			}
			return true;
		}

		private void touch_start(float x,float y)
		{
			mPath.reset();
			mPath.moveTo(x,y);
			mX = x;
			mY = y;
		}

		private void touch_move(float x,float y)
		{
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
            android.util.Log.e("xsp_TPT", "dx =[" + dx + "],dy =["+dy+"]");
			if( dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE )
			{
				mPath.quadTo(mX,mY,(x+mX)/2,(y+mY)/2);
				mX = x;
				mY = y;
				checkCurrentXY(x,y);
			}
		}

		private void touch_up(float x,float y)
		{
			mPath.lineTo(mX,mY);
			mCanvas.drawPath(mPath,mPaint);
			mPath.reset();
		}

		protected void clean()
		{
			mBitmap.eraseColor(Color.TRANSPARENT);
			invalidate();
		}

		protected void free()
		{
			if(mBitmap != null)
			{
				mBitmap.recycle();
				mBitmap = null;
			}
		}

		private void setPaint()
		{
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
            mPaint.setColor(Color.RED);
            mPaint.setAlpha(60);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(15);

			textPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.SUBPIXEL_TEXT_FLAG);
			textPaint.setTextSize(32);
			textPaint.setTextAlign(Paint.Align.CENTER);
		}
	}
}
