package com.sim.cit.testitem;

import java.io.FileOutputStream;
import java.io.IOException;

import com.sim.cit.R;
import com.sim.cit.TestActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
//Modify for CIT optimization by xiasiping 20140730 start
import java.util.List;
import com.sim.cit.MyXml;
import com.sim.cit.XNode;
import com.sim.cit.XProperty;
import com.sim.cit.XMethod;
import com.sim.cit.MyXmlUtils;
import android.widget.Toast;
import java.lang.reflect.Method;
//Modify for CIT optimization by xiasiping 20140730 end

public class FlashLight extends TestActivity {	  
	        
    public static final boolean HasFlashlightFile = true;
    private static Context mContext;
    String TAG = "Flashlight";
    Camera mycam = null;
    Parameters camerPara = null;
    private TextView tv_hint;
    private Button btnPass;
    final byte[] LIGHTE_ON = { '1', '0', '0' };
    final byte[] LIGHTE_OFF = { '0' };
    //private static final String FLASHLIGHT_NODE = "/sys/class/leds/flashlight/brightness";
    //Modify Flashlight from flash_0 to flash_torch by xiasiping 20140516 start
    //private static final String FLASHLIGHT_NODE = "/sys/class/leds/led:flash_0/brightness";
    private static final String FLASHLIGHT_NODE = "/sys/class/leds/torch-light0/brightness";
    //Modify Flashlight from flash_0 to flash_torch by xiasiping 20140516 end
    public static final String CURRENT_FILE_NAME = "CurrentMessage.txt";

    //Modify for CIT optimization by xiasiping 20140730 start
    private static MyXml mxml;
    private List<XProperty> xProperties;
    private List<XMethod> xMethods;
    private List<XNode> xNodes;
    private String flashlight_node = null;
    //Modify for CIT optimization by xiasiping 20140730 end
    @Override
    public void finish() {

        super.finish();
    }

    @Override
    protected void onDestroy() {

        if (!HasFlashlightFile) {
            if (null != mycam) {
                camerPara.setFlashMode(Parameters.FLASH_MODE_OFF);
                mycam.setParameters(camerPara);
                mycam.release();
            }else {
                Log.i(TAG,"cam is null");
            }
        } else {

            FileOutputStream flashlight;
            try {

                flashlight = new FileOutputStream(FLASHLIGHT_NODE);
                flashlight.write(LIGHTE_OFF);
                flashlight.close();

            } catch (Exception e) {
                loge(e);
            }
        }

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Modify for CIT optimization by xiasiping 20140730 start
        try {
            mxml = new MyXmlUtils().getMxml();
            xProperties = mxml.getXProperties();
            xMethods = mxml.getXMethods();
            xNodes = mxml.getXNodes();
        }catch (Exception e) {
            Log.e(TAG,"xsp_May be config file has error");
        }
        if (xNodes != null) {
            for (XNode xn : xNodes) {
                String name = xn.getName();
                if ("flashlight".equals(name)) {
                    flashlight_node = xn.getValue();
                }
            }
        }
        //Modify for CIT optimization by xiasiping 20140730 end

    	layoutId=R.layout.flashlight;
    	super.onCreate(savedInstanceState);
        mContext = this;
        tv_hint = (TextView)findViewById(R.id.flash_hint);
        btnPass = super.btnPass;
        btnPass.setEnabled(false);
        if (!HasFlashlightFile) {
            mycam = Camera.open();
            if (null != mycam) {
                camerPara = mycam.getParameters();
                camerPara.setFlashMode(Parameters.FLASH_MODE_TORCH);
                mycam.setParameters(camerPara);
                btnPass.setEnabled(true);
            } else {
                Log.i(TAG,"cam is null");
            }
        } else {
            //Modify Flashlight from flash_0 to flash_torch by xiasiping 20140516 
            //FileOutputStream flashlight;
            final FileOutputStream flashlight;
            try {

                //flashlight = new FileOutputStream("/sys/class/leds/flashlight/brightness");
                //flashlight = new FileOutputStream("/sys/class/leds/led:flash_0/brightness");
                //Modify for CIT optimization by xiasiping 20140730 start
                if (flashlight_node != null) {
                    Log.e(TAG,"xsp_flashlight_node = " + flashlight_node);
                    flashlight = new FileOutputStream(flashlight_node);
                } else {
                    Log.e(TAG,"xsp_flashlight_node is null ");
                    flashlight = new FileOutputStream(FLASHLIGHT_NODE);
                }


                //flashlight = new FileOutputStream("/sys/class/leds/led:flash_torch/brightness");
                //Modify for CIT optimization by xiasiping 20140730 end
                flashlight.write(LIGHTE_OFF);
                Thread.sleep(1000);
                new Thread(){
                    public void run(){
                        try{
                            flashlight.write(LIGHTE_ON);
                            flashlight.close();
                        } catch (Exception e) {
                            loge(e);
                        }
                    }
                }.start();
                //flashlight.write(LIGHTE_ON);
                //flashlight.close();
                //Modify Flashlight from flash_0 to flash_torch by xiasiping 20140516 end
                btnPass.setEnabled(true);
            } catch (Exception e) {
                loge(e);
            }

        }
    //    showDialog(FlashLight.this);

    }

    void showDialog(final FlashLight fl) {

        new AlertDialog.Builder(fl).setTitle(getString(R.string.flashlight_confirm))
                .setPositiveButton(getString(R.string.flashlight_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        setResult(RESULT_OK);
                      //  Utilities.writeCurMessage(mContext, TAG, "Pass");
                        writeCurMessage(mContext, TAG, "Pass");
                     //   finish();
                    }
                }).setNegativeButton(getString(R.string.flashlight_no), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        setResult(RESULT_CANCELED);
                     //   Utilities.writeCurMessage(mContext, TAG, "Failed");
                        writeCurMessage(mContext, TAG, "Failed");
                   //     finish();
                    }
                }).show();
    }

    public void writeCurMessage(Context context, String Tag, String result) {

        String msg = "[" + Tag + "] " + result;
        FileOutputStream mFileOutputStream = null;
        try {
            mFileOutputStream = new FileOutputStream(context.getFilesDir() + "/" + CURRENT_FILE_NAME, false);
            byte[] buffer = msg.getBytes();
            mFileOutputStream.write(buffer, 0, buffer.length);
            mFileOutputStream.flush();
        } catch (Exception e) {
            loge(e);
            e.printStackTrace();
        } finally {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                loge(e);
                e.printStackTrace();
            }
        }
        logd("Writed result=" + result);

    }   

    void logd(Object d) {

        Log.d(TAG, "" + d);
    }

    void loge(Object e) {

        Log.e(TAG, "" + e);
    }

}
