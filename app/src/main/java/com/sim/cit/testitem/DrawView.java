package com.sim.cit.testitem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class DrawView extends SurfaceView implements
        SurfaceHolder.Callback, Runnable {
    public DrawView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initView();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        initView();
    }

    // SurfaceHolder实例
    private SurfaceHolder mSurfaceHolder;
    // Canvas对象
    private Canvas mCanvas;
    // 控制子线程是否运行
    private boolean startDraw;
    // Path实例
    private Path mPath = new Path();
    // Paint实例
    private Paint mpaint = new Paint();


    private void initView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        // 设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        // 设置常亮
        this.setKeepScreenOn(true);

    }

    @Override
    public void run() {
        // 如果不停止就一直绘制
        while (startDraw) {
            // 绘制
            draw();
        }
    }

    /*
     * 创建
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("DrawView", "SurfaceCreated");
        startDraw = true;
        new Thread(this).start();
    }

    /*
     * 改变
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.e("DrawView", "SurfaceChanged");
    }

    /*
     * 销毁
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("DrawView", "SurfaceDestroyed");
        startDraw = false;
    }

    private void draw() {
        Log.e("DrawView","draw()");
        try {
            Log.e("DrawView","lockCanvas()");
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.WHITE);
            mpaint.setStyle(Paint.Style.STROKE);

            mpaint.setStrokeWidth(10);
            mpaint.setColor(Color.BLACK);
            mCanvas.drawPath(mPath, mpaint);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 对画布内容进行提交
            if (mCanvas != null) {
                try{
                    Log.e("DrawView","unlockCanvasAndPost()");
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();    //获取手指移动的x坐标
        int y = (int) event.getY();    //获取手指移动的y坐标
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mPath.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:

                mPath.lineTo(x, y);
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    // 重置画布
    public void reset() {
        mPath.reset();
    }

    public void exit() {
        startDraw = false;
    }

}  
