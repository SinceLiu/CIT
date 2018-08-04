package com.sim.cit.testitem;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.sim.cit.R;

/*
* change by xsp for new TPTest logic
* this code was come from fangjili/Bu3
*/
//===begin add by wangying 2013-08-16===
import android.graphics.RectF;
//===end add by wangying 2013-08-16===


public class TPTest extends Activity {

    private Context mContext;
	final Handler handler = new Handler();
	private static final int LCD_MENU_QUIT = 1;
	private static Paint mPaint;
	private static int screen_X;
	private static int screen_Y;
	public class TouchView extends View {
		boolean[] begin_end = null;
		public int flag;
		public float percent = 0.98f;
		public int times;
		private float[] lines;
		private ArrayList<PointF> graphics;
		private int of = 0;
         final String TAG = "TPTest";
		int m_width = screen_X / 2;
		int m_height = screen_Y / 2;
		int m_middle = screen_X /10;
        float screen = ((float)screen_X)/ screen_Y;
        
        //===begin add by wangying 2013-08-16===
        int iLimitWidth = screen_X / 6;//15;
        int iMiddleX = screen_X / 2;
        int iMiddleY = screen_Y / 2;
        int iQuarterX = screen_X / 4;
        int iQuarterY = screen_Y / 4;
        private float[] fLines_1;
        private float[] fLines_2;
        private float[] fLines_3;
        private float[] fLines_4;
        private ArrayList<RectF> arrayListRectF_1 = new ArrayList<RectF>();
        private ArrayList<RectF> arrayListRectF_2 = new ArrayList<RectF>();
        private ArrayList<RectF> arrayListRectF_3 = new ArrayList<RectF>();
        private ArrayList<Boolean> arrayListbInRect_1 = new ArrayList<Boolean>();
        private ArrayList<Boolean> arrayListbInRect_2 = new ArrayList<Boolean>();
        private ArrayList<Boolean> arrayListbInRect_3 = new ArrayList<Boolean>();
        private ArrayList<Boolean> arrayListbInRect_4 = new ArrayList<Boolean>();
        private boolean bIsFirstSetpPass = false;
        private int iCurrentStep = 1;
        private ArrayList<RectF> arrayListCorner = new ArrayList<RectF>();
        private ArrayList<Boolean> arrayListbInCorner = new ArrayList<Boolean>();
        
        private ArrayList<RectF> arrayListEdge_2 = new ArrayList<RectF>();
        private ArrayList<RectF> arrayListEdge_3 = new ArrayList<RectF>();
        private ArrayList<RectF> arrayListEdge_4 = new ArrayList<RectF>();
        //===end add by wangying 2013-08-16===
        
		private void touch_move(float f, float f1) {
			graphics.add(new PointF(f, f1));
			float f2 = mX;
			float f3 = Math.abs(f - f2);
			float f4 = mY;
			float f5 = Math.abs(f1 - f4);
			if (f3 >= 4F || f5 >= 4F) {
				Path path = mPath;
				float f6 = mX;
				float f7 = mY;
				float f8 = (mX + f) / 2F;
				float f9 = (mY + f1) / 2F;
				path.quadTo(f6, f7, f8, f9);
				mX = f;
				mY = f1;
			}
		}

		private void touch_start(float f, float f1) {
			graphics.add(new PointF(f, f1));

			Log.v(TAG, "touch_start_times=" + times);
			Log.v(TAG, "touch_start_f=" + f + "touch_start_f111=" + f1);
			if(times<0) {
				times=0;
			}
			begin_end[times] = judgePoint(f, f1);

			times++;
			//
			mPath.reset();
			mPath.moveTo(f, f1);
			mX = f;
			mY = f1;
		}

		private void touch_up() {
			Path path = mPath;
			float f = mX;
			float f1 = mY;
			Log.v(TAG, "touch_up_MX=" + f + "touch_up_MY=" + f1);
			Log.v(TAG, "touch_up_times=" + times);
			if (times < 0) {
				times = 0;
			}
			begin_end[times] = judgePoint(f, f1);
			times++;
			if (times > 2) {
				judgePass(begin_end, times);
			}
			path.lineTo(f, f1);
			Canvas canvas = mCanvas;
			Path path1 = mPath;
			Paint paint = TPTest.mPaint;
			canvas.drawPath(path1, paint);
			mPath.reset();
		}
		
		//===begin add by wangying 2013-08-17===	
		private void mouse_move(float f, float f1) {
			graphics.add(new PointF(f, f1));
			float f2 = mX;
			float f3 = Math.abs(f - f2);
			float f4 = mY;
			float f5 = Math.abs(f1 - f4);
			if (f3 >= 4F || f5 >= 4F) {
				Path path = mPath;
				float f6 = mX;
				float f7 = mY;
				float f8 = (mX + f) / 2F;
				float f9 = (mY + f1) / 2F;
				path.quadTo(f6, f7, f8, f9);
				mX = f;
				mY = f1;
			}
		}

		private void mouse_start(float f, float f1) {
			graphics.add(new PointF(f, f1));

			Log.v(TAG, "touch_start_times=" + times);
			Log.v(TAG, "touch_start_f=" + f + "touch_start_f111=" + f1);
	
			mPath.reset();
			mPath.moveTo(f, f1);
			mX = f;
			mY = f1;
		}

		private void mouse_up() {
			Path path = mPath;
			float f = mX;
			float f1 = mY;
			Log.v(TAG, "touch_up_MX=" + f + "touch_up_MY=" + f1);
			Log.v(TAG, "touch_up_times=" + times);
		
			
//			if (iCurrentStep <= 4)
//			{
				if (judgeStep1())
				{
					//edit by cjr 2017.5.8
//					iCurrentStep ++;
					new AlertDialog.Builder(getContext()).setTitle(R.string.tp_pass)
						.setPositiveButton(R.string.tp_confirm, 
								new DialogInterface.OnClickListener(){
									public void onClick(DialogInterface dialog, int whichButton)
									{
										Bundle b = new Bundle();
										Intent intent = new Intent();
										b.putInt("test_result", 1);
										intent.putExtras(b);
										setResult(RESULT_OK, intent);
										finish();
									}
						}).setCancelable(false).show();
					Intent tp_intent = new Intent();
					tp_intent.setAction("ACTION_TP_PASS");
					mContext.sendBroadcast(tp_intent);
				}
//			}

			path.lineTo(f, f1);

			/*Canvas canvas = mCanvas;
			Path path1 = mPath;
			Paint paint = TPTest.mPaint;
			canvas.drawPath(path1, paint);
			mPath.reset();*/
		}
		//===end add by wangying 2013-08-17===

		protected void onDraw(Canvas canvas) {
			canvas.drawColor(-1);

			Paint pt = new Paint();
			pt.setARGB(255, 0, 0, 0);
			canvas.drawText(getResources().getString(R.string.touchname), 80,
					20, pt);
			// fengxuanyang
			lines = new float[] { m_middle, 0f, m_width, m_height - m_middle,
					screen_X - m_middle, 0f, m_width, m_height - m_middle,

					m_middle, 0f, 0f, m_middle,

					0f, m_middle, m_width - m_middle, m_height, 0f,
					screen_Y - m_middle, m_width - m_middle, m_height,

					0f, screen_Y - m_middle, m_middle, screen_Y,

					m_middle, screen_Y, m_width, m_height + m_middle,
					screen_X - m_middle, screen_Y, m_width,
					m_height + m_middle,

					screen_X - m_middle, screen_Y, screen_X,
					screen_Y - m_middle,

					screen_X, screen_Y - m_middle, m_width + m_middle,
					m_height, screen_X, m_middle, m_width + m_middle, m_height,

					screen_X, m_middle, screen_X - m_middle, 0f

			};
			
			//===begin add by wangying 2013-08-16===
			fLines_1 = new float[] { 0f, iLimitWidth, screen_X, iLimitWidth,
	        		
					0f, screen_Y - iLimitWidth, screen_X, screen_Y - iLimitWidth,
					
					iLimitWidth, 0f, iLimitWidth, screen_Y,
					
					screen_X - iLimitWidth, 0f, screen_X - iLimitWidth, screen_Y
			};
			
			fLines_2 = new float[] { 0f, iQuarterY - (iLimitWidth / 2), screen_X, iQuarterY - (iLimitWidth / 2),
	        		
					0f, iQuarterY + (iLimitWidth / 2), screen_X, iQuarterY + (iLimitWidth / 2),
					
					0f, iMiddleY - (iLimitWidth / 2), screen_X, iMiddleY - (iLimitWidth / 2),
					
					0f, iMiddleY + (iLimitWidth / 2), screen_X, iMiddleY + (iLimitWidth / 2),
					
					0f, screen_Y - (iQuarterY + (iLimitWidth / 2)), screen_X, screen_Y - (iQuarterY + (iLimitWidth / 2)),
					
					0f, screen_Y - (iQuarterY - (iLimitWidth / 2)), screen_X, screen_Y - (iQuarterY - (iLimitWidth / 2))
			};
			
			fLines_3 = new float[] { iQuarterX - (iLimitWidth / 2), 0f, iQuarterX - (iLimitWidth / 2), screen_Y,
								
								iQuarterX + (iLimitWidth / 2), 0f, iQuarterX + (iLimitWidth / 2), screen_Y,
								
								iMiddleX - (iLimitWidth / 2), 0f, iMiddleX - (iLimitWidth / 2), screen_Y,
								
								iMiddleX + (iLimitWidth / 2), 0f, iMiddleX + (iLimitWidth / 2), screen_Y,
								
								screen_X - (iQuarterX + (iLimitWidth / 2)), 0f, screen_X - (iQuarterX + (iLimitWidth / 2)), screen_Y,
								
								screen_X - (iQuarterX - (iLimitWidth / 2)), 0f, screen_X - (iQuarterX - (iLimitWidth / 2)), screen_Y
								
						};
			
			fLines_4 = new float[] { 0f, iLimitWidth, screen_X - iLimitWidth, screen_Y,
					iLimitWidth, 0f, screen_X, screen_Y - iLimitWidth,
					screen_X - iLimitWidth, 0f, 0f,  screen_Y - iLimitWidth,
					screen_X, iLimitWidth, iLimitWidth, screen_Y
			};
	        //===end add by wangying 2013-08-16===
	        
		//	canvas.drawLines(lines, pt);//delete by wangying 2013-08-16
			//===begin add by wangying 2013-08-16===
			/*
			if (bIsFirstSetpPass)
			{
				canvas.drawLines(fLines_1, pt);
				canvas.drawLines(fLines_2, pt);
			}
			else
			{
				canvas.drawLines(fLines_1, pt);
			}  */  
			switch (iCurrentStep)
			{
			case 1:
				canvas.drawLines(fLines_1, pt);
				break;
				
			case 2:
				canvas.drawLines(fLines_2, pt);
				break;
				
			case 3:
				canvas.drawLines(fLines_3, pt);
				break;
				
			case 4:
				canvas.drawLines(fLines_4, pt);
				break;
			}
	        //===end add by wangying 2013-08-16===
			// Path ph1 = new Path();
			// ph1.moveTo(0f, 10f);
			// ph1.lineTo(150f, 150f);
			// ph1.lineTo(0f, 300f);
			// ph1.close();
			// canvas.drawPath(ph1, pt);
			// end
			Point p = new Point();
			Bitmap bitmap = mBitmap;
			Paint paint = mBitmapPaint;
			canvas.drawBitmap(bitmap, 0F, 0F, paint);
			Path path = mPath;
			Paint paint1 = TPTest.mPaint;
			canvas.drawPath(path, paint1);
		}

		protected void onSizeChanged(int i, int j, int k, int l) {
			super.onSizeChanged(i, j, k, l);
		}

		// fengxuanyang
		private boolean judgePoint(float f1, float f2) {
			// int m_width = screen_X / 2;
			// int m_height = screen_Y / 2;
			// int m_middle = 20;
			Log.v(TAG,"screen_x" + screen_X + "screen_Y="
					+ screen_Y);
			if (((0f <= f1 & f1 <= m_middle) | (screen_X- m_middle<= f1 & f1 <= screen_X))
					& ((0f <= f2 & f2 <= m_middle) | (screen_Y-m_middle <= f2 & f2 <= screen_Y))) {

				return true;
			}
			// Toast.makeText(this,
			// getResources().getString(R.string.touch_waring), 1).show();
			else {
				times -= 2;

			}
			Toast.makeText(this.getContext(), R.string.touch_waring, 0).show();
			refreshDrawableState();
			return false;
		}

		private void judgePass(boolean[] bs, int j) {
			Log.v(TAG, "screen="+screen);
			Log.v(TAG, "bs=BS.LENGTH=" + bs.length);
			times = 0;
			for (int i = 0; i < bs.length; i++) {
				if (!bs[i]) {
					Toast.makeText(this.getContext(), R.string.fail, 0).show();
					return;
				}
			}
			// if(graphics.size()>0){
			// // canvas.drawPoint(graphics.get(of).x, graphics.get(of).y,
			// paint);
			// of+=1;
			// if(of<graphics.size()){
			// if(of==graphics.size()-1){
			// }
			// invalidate();
			// }
			// }
			int mall = 0;
			for (int i = 0; i < graphics.size(); i++) {
				float mx = graphics.get(i).x;
				float my = graphics.get(i).y;

//				if (((-m_middle*3 <= (3 * mx - 2 * my)) & ((3 * mx - 2 * my) <= m_middle*3))
//						| ((3*(screen_X-m_middle) <= (3 * mx + 2 * my)) & ((3 * mx + 2 * my) <= 3*(screen_X+m_middle)))) {
//					mall++;
//				}
				if (((-m_middle <= (mx - screen*my)) & ((mx - screen*my) <= m_middle))
						| (((screen_X-m_middle) <= (mx + screen*my)) & ((mx + screen*my) <= (screen_X+m_middle)))) {
					mall++;
				}
				
//				if (((-m_middle*3 <= (3 * my - 2 * mx)) & ((3 * my - 2 * mx) <= m_middle*3))
//						| ((3*(screen_X-m_middle) <= (3 * my + 2 * mx)) & ((3 * my + 2 * mx) <= 3*(screen_X+m_middle)))) {
//					mall++;
//				}

			}
			Log.v(TAG, "graphics.size()==" + graphics.size());
			Log.v(TAG, "mall=" + mall + "pervent"
					+ ((float) mall / ((float) graphics.size())) * 100 + "%");
			if (((float) mall / ((float) graphics.size())) * 100 > 80) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getContext()).setTitle(R.string.test_item_TP);
				builder.setMessage(R.string.tp_success);
				builder.setPositiveButton(R.string.alert_dialog_ok,
						new TPDilogListener()).create().show();
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {

						touchview.postInvalidate();
					}
				}).start();

				Toast.makeText(this.getContext(), R.string.tp_fail, 0).show();
				
			}

		}
		
		//===begin add by wangying 2013-08-17===
		private boolean judgeFirstStep()
		{
			//judge the line was in the rect
			boolean bResult = true;
			boolean bValidGraphics = false;
			for (int i = 0; i < arrayListRectF_1.size(); i ++)
			{
				bResult = true;
				for (int j = 0; j < graphics.size(); j ++)
				{
					if (!arrayListRectF_1.get(i).contains(graphics.get(j).x, graphics.get(j).y))
					{
						bResult = false;
						break;
					}
				}
				if (bResult)
				{
					arrayListbInRect_1.set(i, bResult);	
					bValidGraphics = true;
				}		
			}
			if (!bValidGraphics)
			{
				Toast.makeText(this.getContext(), R.string.tp_enough, 0).show();
			}
			//judge the all the rect was Activation
			for (int i = 0; i < arrayListbInRect_1.size(); i ++)
			{
				if (!arrayListbInRect_1.get(i))
				{
					return false;
				}
			}
			return true;
		}
		
		private boolean judgeSecondStep()
		{
			//judge the line was in the rect
			boolean bResult = true;
			boolean bValidGraphics = false;
			for (int i = 0; i < arrayListRectF_2.size(); i ++)
			{
				bResult = true;
				for (int j = 0; j < graphics.size(); j ++)
				{
					if (!arrayListRectF_2.get(i).contains(graphics.get(j).x, graphics.get(j).y))
					{
						bResult = false;
						break;
					}
				}
				if (bResult)
				{
					arrayListbInRect_2.set(i, bResult);	
					bValidGraphics = true;
				}		
			}
			if (!bValidGraphics)
			{
				if (!isGraphicsInDiagonal())
				{
					Toast.makeText(this.getContext(), R.string.tp_enough, 0).show();
				}	
			}
			//judge the all the rect was Activation
			for (int i = 0; i < arrayListbInRect_2.size(); i ++)
			{
				if (!arrayListbInRect_2.get(i))
				{
					return false;
				}
			}
			return true;
		}
		
		private boolean isGraphicsInDiagonal()
		{
			int mall = 0;
			//===
			boolean bResult = true;
			int iBeginCorner = 0;
			int iEndCorner = 0;
			int iCurrentRect = -1;
			
			for (int i = 0; i < arrayListCorner.size(); i ++)
			{
				if (arrayListCorner.get(i).contains(graphics.get(0).x, graphics.get(0).y))
				{
					iBeginCorner = (i+1);
					Log.v(TAG, "=== wangying iBeginCorner=" + iBeginCorner);
				}
				if (arrayListCorner.get(i).contains(graphics.get(graphics.size() - 1).x, graphics.get(graphics.size() - 1).y))
				{
					iEndCorner = (i+1);
					Log.v(TAG, "=== wangying iEndCorner=" + iEndCorner);
				}
			}
			if (iBeginCorner == 1)
			{
				if (iEndCorner == 3)
				{
					iCurrentRect = 0;
				}
			}
			else if (iBeginCorner == 2)
			{
				if (iEndCorner == 4)
				{
					iCurrentRect = 1;
				}
			}
			else if (iBeginCorner == 3)
			{
				if (iEndCorner == 1)
				{
					iCurrentRect = 0;
				}
			}
			else if (iBeginCorner == 4)
			{
				if (iEndCorner == 2)
				{
					iCurrentRect = 1;
				}
			}
			
			if (iCurrentRect == -1)
			{
				Toast.makeText(this.getContext(), R.string.tp_enough, 0).show();
				return false;
			}
			//===
			if (iCurrentRect == 0)
			{
				for (int i = 0; i < graphics.size(); i++) 
				{
					float mx = graphics.get(i).x;
					float my = graphics.get(i).y;
	
					if (!((-iLimitWidth/2 <= (mx - screen*my)) && ((mx - screen*my) <= iLimitWidth/2))) 
					{
						//mall++;
						bResult = false;
						Toast.makeText(this.getContext(), R.string.tp_effective_tips, 0).show();
						break;
					}
				}
				Log.v(TAG, "graphics.size()==" + graphics.size());
				Log.v(TAG, "mall=" + mall + "pervent"
						+ ((float) mall / ((float) graphics.size())) * 100 + "%");
				//if (((float) mall / ((float) graphics.size())) * 100 > 80) 
				if (bResult)
				{
					arrayListbInRect_4.set(0, true);	
					//return true;
				} 
			}
			else if (iCurrentRect == 1)
			{
				mall = 0;
				for (int i = 0; i < graphics.size(); i++) 
				{
					float mx = graphics.get(i).x;
					float my = graphics.get(i).y;
	
					if (!(((screen_X-iLimitWidth/2) <= (mx + screen*my)) && ((mx + screen*my) <= (screen_X+iLimitWidth/2)))) 
					{
						//mall++;
						bResult = false;
						Toast.makeText(this.getContext(), R.string.tp_effective_tips, 0).show();
						break;
					}
				}
				Log.v(TAG, "graphics.size()==" + graphics.size());
				Log.v(TAG, "mall=" + mall + "pervent"
						+ ((float) mall / ((float) graphics.size())) * 100 + "%");
				//if (((float) mall / ((float) graphics.size())) * 100 > 80) 
				if (bResult)
				{
					arrayListbInRect_4.set(1, true);	
				//	return true;
				} 
			}
			
			for (int i = 0; i < arrayListbInRect_4.size(); i ++)
			{
				if (!arrayListbInRect_4.get(i))
				{
					return false;
				}
			}
			return true;
		}
		
		private boolean judgeStep()
		{
			boolean bResult = false;
			switch (iCurrentStep)
			{
			case 1:
				bResult = judgeStep1();
				break;
				
			case 2:
				bResult = judgeStep2();
				break;
				
			case 3:
				bResult = judgeStep3();
				break;
				
			case 4:
				bResult = judgeStep4();
				break;
			}
			return bResult;
		}
		
		private boolean judgeStep1()
		{
			boolean bResult = true;
			int iBeginCorner = 0;
			int iEndCorner = 0;
			int iCurrentRect = -1;
			
			for (int i = 0; i < arrayListCorner.size(); i ++)
			{
				if (arrayListCorner.get(i).contains(graphics.get(0).x, graphics.get(0).y))
				{
					iBeginCorner = (i+1);
					Log.v(TAG, "=== wangying iBeginCorner=" + iBeginCorner);
				}
				if (arrayListCorner.get(i).contains(graphics.get(graphics.size() - 1).x, graphics.get(graphics.size() - 1).y))
				{
					iEndCorner = (i+1);
					Log.v(TAG, "=== wangying iEndCorner=" + iEndCorner);
				}
			}
			if (iBeginCorner == 1)
			{
				if (iEndCorner == 2)
				{
					iCurrentRect = 0;
				}
				else if (iEndCorner == 4)
				{
					iCurrentRect = 3;
				}
			}
			else if (iBeginCorner == 2)
			{
				if (iEndCorner == 1)
				{
					iCurrentRect = 0;
				}
				else if (iEndCorner == 3)
				{
					iCurrentRect = 1;
				}
			}
			else if (iBeginCorner == 3)
			{
				if (iEndCorner == 2)
				{
					iCurrentRect = 1;
				}
				else if (iEndCorner == 4)
				{
					iCurrentRect = 2;
				}
			}
			else if (iBeginCorner == 4)
			{
				if (iEndCorner == 3)
				{
					iCurrentRect = 2;
				}
				else if (iEndCorner == 1)
				{
					iCurrentRect = 3;
				}
			}
			
			if (iCurrentRect == -1)
			{
				Toast.makeText(this.getContext(), R.string.tp_enough, 0).show();
				return false;
			}
			
			for (int i = 0; i < graphics.size(); i ++)
			{
				if (!arrayListRectF_1.get(iCurrentRect).contains(graphics.get(i).x, graphics.get(i).y))
				{
					bResult = false;
					Toast.makeText(this.getContext(), R.string.tp_effective_tips, 0).show();
					break;
				}
			}
			if (bResult)
			{
				arrayListbInRect_1.set(iCurrentRect, true);
			}
			
			for (int i = 0; i < arrayListbInRect_1.size(); i ++)
			{
				if (!arrayListbInRect_1.get(i))
				{
					return false;
				}
			}
			return true;
		
		}
		
		private boolean judgeStep2()
		{
			boolean bResult = true;
			int iBeginEdge = 0;
			int iEndEdge = 0;
			int iCurrentRect = -1;
			
			for (int i = 0; i < arrayListEdge_2.size(); i ++)
			{
				if (arrayListEdge_2.get(i).contains(graphics.get(0).x, graphics.get(0).y))
				{
					iBeginEdge = (i+1);
					Log.v(TAG, "=== wangying iBeginEdge=" + iBeginEdge);
				}
				if (arrayListEdge_2.get(i).contains(graphics.get(graphics.size() - 1).x, graphics.get(graphics.size() - 1).y))
				{
					iEndEdge = (i+1);
					Log.v(TAG, "=== wangying iEndEdge=" + iEndEdge);
				}
			}
			if (iBeginEdge == 1)
			{
				if (iEndEdge == 2)
				{
					iCurrentRect = 0;
				}
			}
			else if (iBeginEdge == 2)
			{
				if (iEndEdge == 1)
				{
					iCurrentRect = 0;
				}
			}
			else if (iBeginEdge == 3)
			{
				if (iEndEdge == 4)
				{
					iCurrentRect = 1;
				}
			}
			else if (iBeginEdge == 4)
			{
				if (iEndEdge == 3)
				{
					iCurrentRect = 1;
				}
			}
			else if (iBeginEdge == 5)
			{
				if (iEndEdge == 6)
				{
					iCurrentRect = 2;
				}
			}
			else if (iBeginEdge == 6)
			{
				if (iEndEdge == 5)
				{
					iCurrentRect = 2;
				}
			}
			
			if (iCurrentRect == -1)
			{
				Toast.makeText(this.getContext(), R.string.tp_enough, 0).show();
				return false;
			}
			
			for (int i = 0; i < graphics.size(); i ++)
			{
				if (!arrayListRectF_2.get(iCurrentRect).contains(graphics.get(i).x, graphics.get(i).y))
				{
					bResult = false;
					Toast.makeText(this.getContext(), R.string.tp_effective_tips, 0).show();
					break;
				}
			}
			if (bResult)
			{
				arrayListbInRect_2.set(iCurrentRect, true);
			}
			
			for (int i = 0; i < arrayListbInRect_2.size(); i ++)
			{
				if (!arrayListbInRect_2.get(i))
				{
					return false;
				}
			}
			return true;
			//=====================
			/*boolean bResult = true;
			boolean bValidGraphics = false;
			for (int i = 0; i < arrayListRectF_2.size(); i ++)
			{
				bResult = true;
				for (int j = 0; j < graphics.size(); j ++)
				{
					if (!arrayListRectF_2.get(i).contains(graphics.get(j).x, graphics.get(j).y))
					{
						bResult = false;
						break;
					}
				}
				if (bResult)
				{
					arrayListbInRect_2.set(i, bResult);	
					bValidGraphics = true;
				}		
			}
			if (!bValidGraphics)
			{
				Toast.makeText(this.getContext(), R.string.tp_effective_tips, 0).show();
			}
			for (int i = 0; i < arrayListbInRect_2.size(); i ++)
			{
				if (!arrayListbInRect_2.get(i))
				{
					return false;
				}
			}
			return true;
			*/
		}
		
		private boolean judgeStep3()
		{
			boolean bResult = true;
			int iBeginEdge = 0;
			int iEndEdge = 0;
			int iCurrentRect = -1;
			
			for (int i = 0; i < arrayListEdge_3.size(); i ++)
			{
				if (arrayListEdge_3.get(i).contains(graphics.get(0).x, graphics.get(0).y))
				{
					iBeginEdge = (i+1);
					Log.v(TAG, "=== wangying iBeginEdge=" + iBeginEdge);
				}
				if (arrayListEdge_3.get(i).contains(graphics.get(graphics.size() - 1).x, graphics.get(graphics.size() - 1).y))
				{
					iEndEdge = (i+1);
					Log.v(TAG, "=== wangying iEndEdge=" + iEndEdge);
				}
			}
			if (iBeginEdge == 1)
			{
				if (iEndEdge == 2)
				{
					iCurrentRect = 0;
				}
			}
			else if (iBeginEdge == 2)
			{
				if (iEndEdge == 1)
				{
					iCurrentRect = 0;
				}
			}
			else if (iBeginEdge == 3)
			{
				if (iEndEdge == 4)
				{
					iCurrentRect = 1;
				}
			}
			else if (iBeginEdge == 4)
			{
				if (iEndEdge == 3)
				{
					iCurrentRect = 1;
				}
			}
			else if (iBeginEdge == 5)
			{
				if (iEndEdge == 6)
				{
					iCurrentRect = 2;
				}
			}
			else if (iBeginEdge == 6)
			{
				if (iEndEdge == 5)
				{
					iCurrentRect = 2;
				}
			}
			
			if (iCurrentRect == -1)
			{
				Toast.makeText(this.getContext(), R.string.tp_enough, 0).show();
				return false;
			}
			
			for (int i = 0; i < graphics.size(); i ++)
			{
				if (!arrayListRectF_3.get(iCurrentRect).contains(graphics.get(i).x, graphics.get(i).y))
				{
					bResult = false;
					Toast.makeText(this.getContext(), R.string.tp_effective_tips, 0).show();
					break;
				}
			}
			if (bResult)
			{
				arrayListbInRect_3.set(iCurrentRect, true);
			}
			
			for (int i = 0; i < arrayListbInRect_3.size(); i ++)
			{
				if (!arrayListbInRect_3.get(i))
				{
					return false;
				}
			}
			return true;
			//=====================
			/*boolean bResult = true;
			boolean bValidGraphics = false;
			for (int i = 0; i < arrayListRectF_3.size(); i ++)
			{
				bResult = true;
				for (int j = 0; j < graphics.size(); j ++)
				{
					if (!arrayListRectF_3.get(i).contains(graphics.get(j).x, graphics.get(j).y))
					{
						bResult = false;
						break;
					}
				}
				if (bResult)
				{
					arrayListbInRect_3.set(i, bResult);	
					bValidGraphics = true;
				}		
			}
			if (!bValidGraphics)
			{
				Toast.makeText(this.getContext(), R.string.tp_effective_tips, 0).show();
			}
			for (int i = 0; i < arrayListbInRect_3.size(); i ++)
			{
				if (!arrayListbInRect_3.get(i))
				{
					return false;
				}
			}
			return true;
			*/
		}
		
		private boolean judgeStep4()
		{
			if (isGraphicsInDiagonal())
			{
				new AlertDialog.Builder(getContext()).setTitle(R.string.tp_pass)
											.setPositiveButton(R.string.tp_confirm, 
										new DialogInterface.OnClickListener()
											{
												public void onClick(DialogInterface dialog, int whichButton)
												{
													Bundle b = new Bundle();
													Intent intent = new Intent();
													b.putInt("test_result", 1);
													intent.putExtras(b);
													setResult(RESULT_OK, intent);
													finish();
												}
											}).setCancelable(false).show();
                Intent tp_intent = new Intent();
                tp_intent.setAction("ACTION_TP_PASS");
                mContext.sendBroadcast(tp_intent);
				return true;
			}
			return false;
		}
		//===end add by wangying 2013-08-17===
		
		private class TPDilogListener implements
		android.content.DialogInterface.OnClickListener {

	public void onClick(DialogInterface dialoginterface, int i) {
		Bundle b = new Bundle();
		Intent intent = new Intent();
		b.putInt("test_result", 1);
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}
}
		public boolean onTouchEvent(MotionEvent motionevent) {
			float f;
			float f1;
			f = motionevent.getX();
			f1 = motionevent.getY();
			int action = motionevent.getAction();
			switch (action) {
			// return true;
			case MotionEvent.ACTION_DOWN:
				/*delete by wangying 2013-08-17
				if (times == 0) {
					graphics = new ArrayList<PointF>();
				}
				*/

				graphics = new ArrayList<PointF>();//add by wangying 2013-08-17
				recoverArrayListCorner();
				//touch_start(f, f1);//delete by wangying 2013-08-17
				mouse_start(f, f1);//add by wangying 2013-08-17
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				//touch_move(f, f1);//delete by wangying 2013-08-17
				mouse_move(f, f1);//add by wangying 2013-08-17
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				//touch_up();//delete by wangying 2013-08-17
				mouse_up();//add by wangying 2013-08-17
				invalidate();
				break;

			}
			return true;
		}

		private static final float TOUCH_TOLERANCE = 4F;
		private Bitmap mBitmap;
		private Paint mBitmapPaint;
		private Canvas mCanvas;
		private Path mPath;
		private float mX;
		private float mY;
		
		//===begin add by wangying 2013-08-17===
		private void initRects()
		{
			arrayListRectF_1.add(new RectF(0f, 0f, screen_X, iLimitWidth));
			arrayListRectF_1.add(new RectF(screen_X - iLimitWidth, 0f, screen_X, screen_Y));
			arrayListRectF_1.add(new RectF(0f, screen_Y - iLimitWidth, screen_X, screen_Y));
			arrayListRectF_1.add(new RectF(0f, 0f, iLimitWidth, screen_Y));
			
			arrayListRectF_2.add(new RectF(0f, iQuarterY - (iLimitWidth / 2), screen_X, iQuarterY + (iLimitWidth / 2)));
			arrayListRectF_2.add(new RectF(0f, iMiddleY - (iLimitWidth / 2), screen_X, iMiddleY + (iLimitWidth / 2)));
			arrayListRectF_2.add(new RectF(0f, screen_Y - (iQuarterY + (iLimitWidth / 2)), screen_X, screen_Y - (iQuarterY - (iLimitWidth / 2))));
			/*
			arrayListRectF_2.add(new RectF(iQuarterX - (iLimitWidth / 2), 0f, iQuarterX + (iLimitWidth / 2), screen_Y));
			arrayListRectF_2.add(new RectF(iMiddleX - (iLimitWidth / 2), 0f, iMiddleX + (iLimitWidth / 2), screen_Y));
			arrayListRectF_2.add(new RectF(screen_X - (iQuarterX + (iLimitWidth / 2)), 0f, screen_X, screen_Y));
			*/
			arrayListRectF_3.add(new RectF(iQuarterX - (iLimitWidth / 2), 0f, iQuarterX + (iLimitWidth / 2), screen_Y));
			arrayListRectF_3.add(new RectF(iMiddleX - (iLimitWidth / 2), 0f, iMiddleX + (iLimitWidth / 2), screen_Y));
			arrayListRectF_3.add(new RectF(screen_X - (iQuarterX + (iLimitWidth / 2)), 0f, screen_X, screen_Y));
			
			arrayListCorner.add(new RectF(0f, 0f, iLimitWidth, iLimitWidth));
			arrayListCorner.add(new RectF(screen_X - iLimitWidth, 0f, screen_X, iLimitWidth));
			arrayListCorner.add(new RectF(screen_X - iLimitWidth, screen_Y - iLimitWidth, screen_X, screen_Y));
			arrayListCorner.add(new RectF(0f, screen_Y - iLimitWidth, iLimitWidth, screen_Y));
			
			arrayListEdge_2.add(new RectF(0f, iQuarterY - (iLimitWidth / 2), iLimitWidth, iQuarterY + (iLimitWidth / 2)));
			arrayListEdge_2.add(new RectF(screen_X - iLimitWidth, iQuarterY - (iLimitWidth / 2), screen_X, iQuarterY + (iLimitWidth / 2)));
			arrayListEdge_2.add(new RectF(0f, iMiddleY - (iLimitWidth / 2), iLimitWidth, iMiddleY + (iLimitWidth / 2)));
			arrayListEdge_2.add(new RectF(screen_X - iLimitWidth, iMiddleY - (iLimitWidth / 2), screen_X, iMiddleY + (iLimitWidth / 2)));
			arrayListEdge_2.add(new RectF(0f, screen_Y - (iQuarterY + (iLimitWidth / 2)), iLimitWidth, screen_Y - (iQuarterY - (iLimitWidth / 2))));
			arrayListEdge_2.add(new RectF(screen_X - iLimitWidth, screen_Y - (iQuarterY + (iLimitWidth / 2)), screen_X, screen_Y - (iQuarterY - (iLimitWidth / 2))));
			
			arrayListEdge_3.add(new RectF(iQuarterX - (iLimitWidth / 2), 0f, iQuarterX + (iLimitWidth / 2), iLimitWidth));
			arrayListEdge_3.add(new RectF(iQuarterX - (iLimitWidth / 2), screen_Y - iLimitWidth, iQuarterX + (iLimitWidth / 2), screen_Y));
			arrayListEdge_3.add(new RectF(iMiddleX - (iLimitWidth / 2), 0f, iMiddleX + (iLimitWidth / 2), iLimitWidth));
			arrayListEdge_3.add(new RectF(iMiddleX - (iLimitWidth / 2), screen_Y - iLimitWidth, iMiddleX + (iLimitWidth / 2), screen_Y));
			arrayListEdge_3.add(new RectF(screen_X - (iQuarterX + (iLimitWidth / 2)), 0f, screen_X - (iQuarterX - (iLimitWidth / 2)), iLimitWidth));
			arrayListEdge_3.add(new RectF(screen_X - (iQuarterX + (iLimitWidth / 2)), screen_Y - iLimitWidth, screen_X - (iQuarterX - (iLimitWidth / 2)), screen_Y));
	
			
			boolean defaultValue = false;
			for (int i = 0; i < arrayListRectF_1.size(); i ++)
			{
				arrayListbInRect_1.add(defaultValue);
			}
			for (int i = 0; i < (arrayListRectF_2.size()); i ++)
			{
				arrayListbInRect_2.add(defaultValue);
			}
			for (int i = 0; i < (arrayListRectF_3.size()); i ++)
			{
				arrayListbInRect_3.add(defaultValue);
			}
			for (int i = 0; i < 2; i ++)
			{
				arrayListbInRect_4.add(defaultValue);
			}
			for (int i = 0; i < (arrayListCorner.size()); i ++)
			{
				arrayListbInCorner.add(defaultValue);
			}
		}
		
		private void recoverArrayListCorner()
		{
			arrayListCorner.clear();
			arrayListCorner.add(new RectF(0f, 0f, iLimitWidth, iLimitWidth));
			arrayListCorner.add(new RectF(screen_X - iLimitWidth, 0f, screen_X, iLimitWidth));
			arrayListCorner.add(new RectF(screen_X - iLimitWidth, screen_Y - iLimitWidth, screen_X, screen_Y));
			arrayListCorner.add(new RectF(0f, screen_Y - iLimitWidth, iLimitWidth, screen_Y));
		}
		//===end add by wangying 2013-08-17===

		public TouchView(Context context) {

			super(context);
			begin_end = new boolean[4];
			int i = TPTest.screen_X;
			int j = TPTest.screen_Y;
			android.graphics.Bitmap.Config config = android.graphics.Bitmap.Config.ARGB_8888;
			Bitmap bitmap = Bitmap.createBitmap(i, j, config);
			mBitmap = bitmap;
			Bitmap bitmap1 = mBitmap;
			Canvas canvas = new Canvas(bitmap1);
			mCanvas = canvas;
			Path path = new Path();
			mPath = path;
			Paint paint = new Paint(4);
			mBitmapPaint = paint;
			//===begin add by wangying 2013-08-17===
			initRects();
			//===end add by wangying 2013-08-17===
		}

	}//class TouchView end
	
	private void getScreenMetries() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screen_X = displaymetrics.widthPixels;
		screen_Y = displaymetrics.heightPixels;
		Log.v("TPTest", "widthPixels=" + displaymetrics.widthPixels
				+ "heightPixels" + displaymetrics.heightPixels);
	}

	private void setFullscreen() {
		requestWindowFeature(1);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	}

	private TouchView touchview;

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setFullscreen();
		getScreenMetries();
		touchview = new TouchView(this);
		setContentView(touchview);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xffff0000);
		Paint paint = mPaint;
		android.graphics.Paint.Style style = android.graphics.Paint.Style.STROKE;
		paint.setStyle(style);
		Paint paint1 = mPaint;
		android.graphics.Paint.Join join = android.graphics.Paint.Join.ROUND;
		paint1.setStrokeJoin(join);
		Paint paint2 = mPaint;
		android.graphics.Paint.Cap cap = android.graphics.Paint.Cap.ROUND;
		paint2.setStrokeCap(cap);
		mPaint.setStrokeWidth(1F);
        mContext = getApplicationContext();
	}
}
