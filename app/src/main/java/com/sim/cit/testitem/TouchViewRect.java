package com.sim.cit.testitem;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;

public class TouchViewRect extends View {
    public static String TAG = "TouchViewRect";
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    public ArrayList<Rects> mArrayList;

    static float screen_X;
    static float screen_Y;

    private static final int LINE_NUM_H = 5;
    private static final int LINE_NUM_V = 9;

    private RectF mRect1;
    private RectF mRect2;
    private RectF mRect3;
    private RectF mRect4;

    public TouchViewRect(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBitmap = Bitmap.createBitmap((int)screen_X, (int)screen_Y,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mArrayList = getTestRect();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.i(TAG,"draw");
        canvas.drawColor(Color.WHITE);
        mPaint.setColor(Color.BLACK);
        for (int i = 0; i < LINE_NUM_V; i++) {
            canvas.drawLine(0, screen_Y / LINE_NUM_V * (i + 1), screen_X, screen_Y / LINE_NUM_V * (i + 1), mPaint);
        }
        for (int i = 0; i < LINE_NUM_H; i++) {
            canvas.drawLine(screen_X / LINE_NUM_H * (i + 1), 0, screen_X / LINE_NUM_H * (i + 1), screen_Y, mPaint);
        }
        //mArrayList = getTestRect();
        mPaint.setColor(Color.WHITE);
        /*canvas.drawRect(mRect1, mPaint);
        canvas.drawRect(mRect2, mPaint);
        canvas.drawRect(mRect3, mPaint);
        canvas.drawRect(mRect4, mPaint);*/
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (MotionEvent.ACTION_MOVE == event.getAction() || MotionEvent.ACTION_DOWN == event.getAction()) {
            Log.i(TAG,"onTouchEvent");
            float x = event.getX();
            float y = event.getY();
            mPaint.setColor(Color.GREEN);
            RectF rectOfTouch = null;
            for (int i = 0; i < mArrayList.size(); i++) {
                Rects rects = mArrayList.get(i);
                if (!rects.isChecked && rects.mRects.contains((int)x, (int)y)) {
                    rectOfTouch = rects.mRects;
                    mArrayList.remove(i);
                    Log.i(TAG,"i = " +i+"mArrayList.size() is "+mArrayList.size());
                    break;
                }
            }
            if (rectOfTouch != null) {
                mCanvas.drawRect(rectOfTouch, mPaint);
                Log.i(TAG,"rectOfTouch != null ");
            }
            invalidate();

            if (mArrayList.isEmpty()) {
                TouchPanel.llPF.setVisibility(View.VISIBLE);
             //   btnPass.setEnabled(true);
            }
        }
        return true;
    }

    public class Rects {

        public RectF mRects;
        boolean isChecked = false;

        public Rects(RectF mRectf, boolean isCheck) {

            this.mRects = mRectf;
            this.isChecked = isCheck;
        }

    }

    public ArrayList<Rects> getTestRect() {

        ArrayList<Rects> list = new ArrayList<Rects>();
        Log.i(TAG,"getTestRect");
        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;
        RectF rectOfTouch;
        for (int i = 0; i < LINE_NUM_H; i++) {
            left = screen_X / LINE_NUM_H * i;
            right = screen_X / LINE_NUM_H * (i + 1) + 1;

            for (int j = 0; j < LINE_NUM_V; j++) {
                top = screen_Y / LINE_NUM_V * j;
                bottom = screen_Y / LINE_NUM_V * (j + 1) + 1;
                rectOfTouch = new RectF(left, top, right, bottom);
                /*mPaint.setColor(Color.WHITE);
                mCanvas.drawRect(rectOfTouch, mPaint);
                mCanvas.drawBitmap(mBitmap, 0, 0, mPaint);*/
                list.add(new Rects(rectOfTouch, false));
            }
        }

        return list;

    }
}
