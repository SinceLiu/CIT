package com.sim.cit.testitem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TouchView extends View {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;

    static float screen_X;
    static float screen_Y;

    private static final int LINE_NUM_H = 9;
    private static final int LINE_NUM_V = 15;

    private RectF mRect1;
    private RectF mRect2;
    private RectF mRect3;
    private RectF mRect4;

    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBitmap = Bitmap.createBitmap((int)screen_X, (int)screen_Y,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        float pointH1 = screen_X / LINE_NUM_H + 1;
        float pointH2 = screen_X / LINE_NUM_H * (LINE_NUM_H / 2);
        float pointH3 = screen_X / LINE_NUM_H * (LINE_NUM_H / 2 + 1) + 1;
        float pointH4 = screen_X / LINE_NUM_H * (LINE_NUM_H - 1);
        float pointV1 = screen_Y / LINE_NUM_V + 1;
        float pointV2 = screen_Y / LINE_NUM_V * (LINE_NUM_V / 2);
        float pointV3 = screen_Y / LINE_NUM_V * (LINE_NUM_V / 2 + 1) + 1;
        float pointV4 = screen_Y / LINE_NUM_V * (LINE_NUM_V - 1);
        mRect1 = new RectF(pointH1, pointV1, pointH2, pointV2);
        mRect2 = new RectF(pointH3, pointV1, pointH4, pointV2);
        mRect3 = new RectF(pointH1, pointV3, pointH2, pointV4);
        mRect4 = new RectF(pointH3, pointV3, pointH4, pointV4);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.WHITE);
        mPaint.setColor(Color.BLACK);
        for (int i = 0; i < LINE_NUM_V; i++) {
            canvas.drawLine(0, screen_Y / LINE_NUM_V * (i + 1), screen_X, screen_Y / LINE_NUM_V * (i + 1), mPaint);
        }
        for (int i = 0; i < LINE_NUM_H; i++) {
            canvas.drawLine(screen_X / LINE_NUM_H * (i + 1), 0, screen_X / LINE_NUM_H * (i + 1), screen_Y, mPaint);
        }
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(mRect1, mPaint);
        canvas.drawRect(mRect2, mPaint);
        canvas.drawRect(mRect3, mPaint);
        canvas.drawRect(mRect4, mPaint);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Modify for add click function in TP test by lvhongshan 20140110 start
        if (MotionEvent.ACTION_MOVE == event.getAction() || MotionEvent.ACTION_DOWN == event.getAction()) {
        //Modify for add click function in TP test by lvhongshan 20140110 end
            float x = event.getX();
            float y = event.getY();
            mPaint.setColor(Color.GREEN);
            RectF rectOfTouch;
            if (mRect1.contains((int)x, (int)y)) {
                rectOfTouch = mRect1;
            } else if (mRect2.contains((int)x, (int)y)) {
                rectOfTouch = mRect2;
            } else if (mRect3.contains((int)x, (int)y)) {
                rectOfTouch = mRect3;
            } else if (mRect4.contains((int)x, (int)y)) {
                rectOfTouch = mRect4;
            } else {
                float left = 0;
                float top = 0;
                float right = 0;
                float bottom = 0;
                for (int i = 0; i < LINE_NUM_H; i++) {
                    if (screen_X / LINE_NUM_H * i < x && screen_X / LINE_NUM_H * (i + 1) > x) {
                        left = screen_X / LINE_NUM_H * i;
                        right = screen_X / LINE_NUM_H * (i + 1) + 1;
                    }
                }
                for (int i = 0; i < LINE_NUM_V; i++) {
                    if (screen_Y / LINE_NUM_V * i < y && screen_Y / LINE_NUM_V * (i + 1) > y) {
                        top = screen_Y / LINE_NUM_V * i;
                        bottom = screen_Y / LINE_NUM_V * (i + 1) + 1;
                    }
                }
                rectOfTouch = new RectF(left, top, right, bottom);
            }
            if (rectOfTouch != null) {
                mCanvas.drawRect(rectOfTouch, mPaint);
            }
            invalidate();
        }
        return true;
    }
}
