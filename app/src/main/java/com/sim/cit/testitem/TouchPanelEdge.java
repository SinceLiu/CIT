package com.sim.cit.testitem;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sim.cit.R;
import com.sim.cit.TestActivity;

public class TouchPanelEdge extends TestActivity {

    public static String TAG = "TouchPanelEdge";
    public ArrayList<EdgePoint> mArrayList;
    public int pointRadius = 20;
    String resultString = "Failed";
    public int hightPix = 0, widthPix = 0;
    public float w = 0, h = 0;
    public Context mContext;
    public boolean isPass = false;
    /*private static LinearLayout llPF;
    private static Panel touchView;
    private static Button btnPass;*/
    // If points is too more, it will be hard to touch edge points.
    private final int MAX_POINTS = 20;

    public class EdgePoint {

        int x;
        int y;
        boolean isChecked = false;

        public EdgePoint(int x, int y, boolean isCheck) {

            this.x = x;
            this.y = y;
            this.isChecked = isCheck;
        }

    }

    public ArrayList<EdgePoint> getTestPoint() {

        ArrayList<EdgePoint> list = new ArrayList<EdgePoint>();

        for (int w = pointRadius - 1; w < widthPix; w += pointRadius * 2) {
            for (int h = pointRadius - 1; h < hightPix; h += pointRadius * 2) {

                if (w == pointRadius - 1) {
                    list.add(new EdgePoint(pointRadius, h, false));
                    continue;
                }

                if (w == widthPix - pointRadius - 1) {
                    list.add(new EdgePoint(widthPix - pointRadius, h, false));
                    continue;
                }

                if (h == pointRadius - 1) {
                    list.add(new EdgePoint(w, pointRadius, false));
                    continue;
                }

                if (h == hightPix - pointRadius - 1) {
                    list.add(new EdgePoint(w, hightPix - pointRadius, false));
                    continue;
                }
            }
        }

        return list;

    }

    @Override
    public void finish() {

    //    Utilities.writeCurMessage(this, TAG, resultString);
        super.finish();
    }

    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG,"onCreate()");
        // full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // get panel size
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        hightPix = mDisplayMetrics.heightPixels;
        widthPix = mDisplayMetrics.widthPixels;

        layoutId = 0; //R.layout.touch_paneledge;
        super.onCreate(savedInstanceState);
        init(this);
        //Temporary modification for force close app on test touch panel edge start
        /*if(mContext.getResources().getBoolean(com.android.internal.R.bool.config_showNavigationBar)){
            hightPix += mContext.getResources().getDimensionPixelSize(com.android.internal.R.dimen.navigation_bar_height);
        }*/
        //Temporary modification for force close app on test touch panel edge end
        // It must be common divisor of width and hight
        pointRadius = getRadius(hightPix, widthPix);
        logd(hightPix + " " + widthPix);// 800x480 or 480x320
        /*llPF = (LinearLayout)findViewById(R.id.ll_p_f);
        touchView = (Panel)findViewById(R.id.tsv_touch_paneledge);
        llPF.setVisibility(View.INVISIBLE);
        btnPass = super.btnPass;
        btnPass.setEnabled(false);*/
        setContentView(new Panel(this));
    }

    private void init(Context context) {

        mContext = context;
        resultString = "Failed";
    }

    int getRadius(int hightPix, int widthPix) {

        Log.i(TAG,"getRadius()");
        int h = hightPix / 2;
        int w = widthPix / 2;
        if (w > h)// landscape mode
        {
            int t;
            t = w;
            w = h;
            h = t;
        }
        int radius = -1;
        int minRadius = w / MAX_POINTS;

        for (int i = minRadius; i < w; i++) {
            if (h % i == 0 && w % i == 0) {

                return i;
            }
        }
        //return radius;
        return minRadius;
    }

    public class Panel extends View {

        public static final int TOUCH_TRACE_NUM = 30;
        public static final int PRESSURE = 500;
        private TouchData[] mTouchData = new TouchData[TOUCH_TRACE_NUM];
        private int traceCounter = 0;
        private Paint mPaint = new Paint();

        public class TouchData {

            public float x;
            public float y;
            public float r;
        }

        public Panel(Context context){//, AttributeSet attrs) {
            super(context);
            Log.i(TAG,"Panel()");
            mArrayList = getTestPoint();
            mPaint.setARGB(100, 100, 100, 100);
            for (int i = 0; i < TOUCH_TRACE_NUM; i++) {
                mTouchData[i] = new TouchData();
            }
        }

        private int getNext(int c) {
            Log.i(TAG,"getNext()");
            int temp = c + 1;
            return temp < TOUCH_TRACE_NUM ? temp : 0;
        }

        public void onDraw(Canvas canvas) {
            Log.i(TAG,"onDraw()");
            super.onDraw(canvas);
            mPaint.setColor(Color.LTGRAY);
            mPaint.setTextSize(20);
            canvas.drawText("W: " + w, widthPix / 2 - 20, hightPix / 2 - 10, mPaint);
            canvas.drawText("H: " + h, widthPix / 2 - 20, hightPix / 2 + 10, mPaint);

            mPaint.setColor(Color.RED);
            mPaint.setStrokeWidth(pointRadius);
            for (int i = 0; i < mArrayList.size(); i++) {
                EdgePoint point = mArrayList.get(i);
                mPaint.setColor(Color.RED);
                canvas.drawCircle(point.x, point.y, mPaint.getStrokeWidth(), mPaint);
            }

            for (int i = 0; i < TOUCH_TRACE_NUM; i++) {
                TouchData td = mTouchData[i];
                mPaint.setColor(Color.BLUE);
                if (td.r > 0) {
                    canvas.drawCircle(td.x, td.y, 2, mPaint);
                }
            }
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.i(TAG,"onTouchEvent()");
            final int eventAction = event.getAction();

            w = event.getRawX();
            h = event.getRawY();
            if ((eventAction == MotionEvent.ACTION_MOVE) || (eventAction == MotionEvent.ACTION_UP)) {
                for (int i = 0; i < mArrayList.size(); i++) {
                    EdgePoint point = mArrayList.get(i);
                    if (!point.isChecked
                            && ((w >= (point.x - pointRadius)) && (w <= (point.x + pointRadius)))
                            && ((h >= (point.y - pointRadius)) && (h <= (point.y + pointRadius)))) {
                        mArrayList.remove(i);
                        break;
                    }

                }

                if (mArrayList.isEmpty()) {
                    ((Activity) mContext).setResult(RESULT_OK);
                  //  llPF.setVisibility(View.VISIBLE);
                 //   btnPass.setEnabled(true);
                //    resultString = Utilities.RESULT_PASS;
                    finish();
                }

                TouchData tData = mTouchData[traceCounter];
                tData.x = event.getX();
                tData.y = event.getY();
                tData.r = event.getPressure() * PRESSURE;
                traceCounter = getNext(traceCounter);
                invalidate();

            }
            return true;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK||keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            ((Activity) mContext).setResult(RESULT_CANCELED);
         //   llPF.setVisibility(View.VISIBLE);
         //   btnPass.setEnabled(false);
            finish();
	}
        //if (keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_HOME) {
        if (keyCode==KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void logd(Object d) {
        Log.d(TAG, "" + d);
    }

    void loge(Object e) {
        Log.e(TAG, "" + e);
    }
}
