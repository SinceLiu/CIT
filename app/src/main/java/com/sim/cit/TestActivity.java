package com.sim.cit;

//modify for write the auto test results to NV2499 by songguangyu 20140218 start
//import com.qualcomm.qcrilhook.QcRilHook;
//modify for write the auto test results to NV2499 by songguangyu 20140218 end

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


//Modify for CIT optimization by xiasiping 20140730 start
import java.util.List;

import com.sim.cit.MyXml;
import com.sim.cit.XNode;
import com.sim.cit.XProperty;
import com.sim.cit.XMethod;
import com.sim.cit.MyXmlUtils;

import java.io.File;
import java.lang.reflect.Method;
//Modify for CIT optimization by xiasiping 20140730 end


//add by dgy 20170223
import com.readboy.nv.NvJniItems;
import com.readboy.nv.NvItemIds;
import com.readboy.nv.NvInfo;
import com.readboy.util.FileOperation;
import com.readboy.util.ResultWriter;
// add end

public class TestActivity extends Activity {
    private static final String TAG = "CIT_TestActivity";
    protected boolean isAllowBack = false;
    protected int layoutId;
    private Context context;
    private CITTestHelper application;
    private int index;
    boolean temp_autoTest = false;
    private int temp_testMode = CITTestHelper.NOT_TEST;
    protected Button btnPass;
    protected Button btnFail;
    boolean isClickPass = false;
    //add for mark test results by songguangyu 20140217 start
    private int curTestIndex;
    //add for mark test results by songguangyu 20140217 end
    //add for write the auto test results to NV2499 by songguangyu 20140218 start
//        private QcRilHook mQcRilHook;
    //add for write the auto test results to NV2499 by songguangyu 20140218 end
    //Modify for CIT optimization by xiasiping 20140730 start
    private MyXml mxml;

    private List<XProperty> xProperties;
    private List<XMethod> xMethods;
    private List<XNode> xNodes;

    //Modify for CIT optimization by xiasiping 20140730 end
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Modify for CIT optimization by xiasiping 20140730 start
        try {
            mxml = new MyXmlUtils().getMxml();

            xProperties = mxml.getXProperties();
            xMethods = mxml.getXMethods();
            xNodes = mxml.getXNodes();
        } catch (Exception e) {
            Log.e(TAG, "xsp_May be config file has error");
            Toast.makeText(this, "get xml resources fail", 3000).show();
        }
        //Modify for CIT optimization by xiasiping 20140730 end

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = getApplicationContext();
        application = (CITTestHelper) getApplication();
        temp_autoTest = application.isAutoTest();
        temp_testMode = application.getTestMode();
        if (temp_autoTest) {
            index = getIntent().getIntExtra(CITTestHelper.EXTRA_KEY_AUTO_TEST_INDEX, -1);
        }
        //add for mark test results by songguangyu 20140217 start
        curTestIndex = getIntent().getIntExtra("cutTestIndex", -1);
        //add for mark test results by songguangyu 20140217 end
        //20121209 modified for keyPadHome&flashLight autotest by lvhongshan start
        if (layoutId != 0) {
            setContentView(layoutId);
            setPassAndFail();
        }
        //20121209 modified for keyPadHome&flashLight autotest by lvhongshan end
        //add for write the auto test results to NV2499 by songguangyu 20140218 start
//                mQcRilHook = new QcRilHook(this);
        //add for write the auto test results to NV2499 by songguangyu 20140218 end
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isAllowBack) {
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU
                || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_SEARCH
                || keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isAllowBack) {
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU
                || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_SEARCH
                || keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void rbRecord(boolean pass) {
        if (curTestIndex != -1) {
//			Log.i("adsd","testAct curTestIndex = " + curTestIndex);
            int testmod = -1;
            switch (application.getTestMode()) {
                case CITTestHelper.PCB_CIT1:
                    testmod = curTestIndex;
                    break;
                case CITTestHelper.PCB_CIT2:
                    testmod = 20 + curTestIndex;
                    break;
                case CITTestHelper.PCB_CIT3:
                    testmod = 30 + curTestIndex;
                    break;
                case CITTestHelper.COMPLETE_CIT1:
                    testmod = 50 + curTestIndex;
                    break;
                case CITTestHelper.COMPLETE_CIT2:
                    testmod = 70 + curTestIndex;
                    break;
                case CITTestHelper.COMPLETE_CIT3:
                    testmod = 80 + curTestIndex;
                    break;
                case CITTestHelper.SUBPCB_CIT1:
                case CITTestHelper.SUBPCB_CIT2:
                    testmod = 40 + curTestIndex;
                    break;

                default:
                    testmod = -1;
                    break;
            }
            if (testmod != -1) {
                CITTestHelper.fileResult[testmod] = pass ? 'T' : 'F';
                Log.i("adsd", String.copyValueOf(CITTestHelper.fileResult));
                ResultWriter.write(String.copyValueOf(CITTestHelper.fileResult));
            }
        }
    }

    protected void setPassAndFail() {
        btnPass = (Button) findViewById(R.id.btn_pass);
        if (null == btnPass) {
            Log.i("lvhongshan_TestActivity", "setPassandFail_btnPass is null");
            Log.i("lvhongshan_TestActivity", "setPassandFail_R.id.btnPass is" + R.id.btn_pass);
        } else {
            Log.i("lvhongshan_TestActivity", "setPassandFail_btnPass is not null");
            Log.i("lvhongshan_TestActivity", "setPassandFail_R.id.btnPass is" + R.id.btn_pass);
        }

        btnFail = (Button) findViewById(R.id.btn_fail);

        btnPass.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                rbRecord(true);
                btnFail.setEnabled(false);
                Log.i(TAG, "pass");
                if (isClickPass) {
                    return;
                }
                isClickPass = true;
                if (temp_autoTest) {
                    index++;
                    if (index >= application.getAutoTestListSize()) {
                        record(80);
                        application.setAutoTest(false);
                    } else {
                        application.startTestActivity(TestActivity.this, index);
                    }
                }
                //add for mark test results by songguangyu 20140217 start
                Log.i(TAG, "--------------typeVersion = " + getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_VERSION));
                Log.i(TAG, "--------------typeVersion = " + getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_SPEAKER_PINKNOISE_TEST));
                Log.i(TAG, "--------------typeVersion = " + getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_RECEIVER_PINKNOISE_TEST));
                if (CITTestHelper.EXTRA_TEST_TYPE_VERSION.equals(
                        getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_VERSION))) {
                    application.setVersionTestResult(CITTestHelper.TEST_RESULT_PASS);
                } else {
                    setTestResult(CITTestHelper.TEST_RESULT_PASS);
                }
                if (CITTestHelper.EXTRA_TEST_TYPE_SPEAKER_PINKNOISE_TEST.equals(
                        getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_SPEAKER_PINKNOISE_TEST))) {
                    application.setSpeakerPinknoiseTestResult(CITTestHelper.TEST_RESULT_PASS);
                } else if (CITTestHelper.EXTRA_TEST_TYPE_RECEIVER_PINKNOISE_TEST.equals(
                        getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_RECEIVER_PINKNOISE_TEST))) {
                    application.setReceiverPinknoiseTestResult(CITTestHelper.TEST_RESULT_PASS);
                } else {
                    setTestResult(CITTestHelper.TEST_RESULT_PASS);
                }
                //add for mark test results by songguangyu 20140217 end
                finish();
            }
        });
        btnFail.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                rbRecord(false);
                btnPass.setEnabled(false);
                Log.i(TAG, "fail");
                backKeyOp(TestActivity.this);
            }
        });
    }

    //add for mark test results by songguangyu 20140217 start
    protected void setTestResult(int testResult) {
        switch (testResult) {
            case CITTestHelper.TEST_RESULT_PASS:
                try {
                    application.getTestResultMaps().get(application.getTestMode())
                            .get(curTestIndex).setTestResult(CITTestHelper.TEST_RESULT_PASS);
                    Log.i(TAG, "---------------CITTestHelper.TEST_RESULT_PASS = " + CITTestHelper.TEST_RESULT_PASS);
                } catch (Exception e) {
                    Log.e(TAG, "setTestResult(CITTestHelper.TEST_RESULT_PASS) catch exception");
                    if (e != null) {
                        Log.e(TAG, "" + e);
                    }
                }
                break;
            case CITTestHelper.TEST_RESULT_FAIL:
                try {
                    application.getTestResultMaps().get(application.getTestMode())
                            .get(curTestIndex).setTestResult(CITTestHelper.TEST_RESULT_FAIL);
                } catch (Exception e) {
                    Log.e(TAG, "setTestResult(CITTestHelper.TEST_RESULT_FAIL) catch exception");
                    if (e != null) {
                        Log.e(TAG, "" + e);
                    }
                }
                break;
        }
    }
//add for mark test results by songguangyu 20140217 end

    //20121209 added for setting autoTest of KeyPadHome by lvhongshan start
    protected void autoTestNextItem(boolean isPass) {
        if (isPass) {
            Log.i(TAG, "pass");
            if (isClickPass) {
                return;
            }
            isClickPass = true;
            if (temp_autoTest) {
                index++;
                if (index >= application.getAutoTestListSize()) {
                    record(80);
                    application.setAutoTest(false);
                } else {
                    application.startTestActivity(TestActivity.this, index);
                }
            }
            finish();
        } else {
            Log.i(TAG, "fail");
            backKeyOp(TestActivity.this);
        }

    }

    //20121209 added for setting autoTest of KeyPadHome by lvhongshan end

    public void record(int result) {
        //Modify for CIT optimization by xiasiping 20140730 start
        String method_write = null;
        if (xMethods != null) {
            for (XMethod xm : xMethods) {
                String name = xm.getName();
                if ("qcrilhook_factory_write2499".equals(name)) {
                    method_write = xm.getValue();
                }
            }
        }
        //Modify for CIT optimization by xiasiping 20140730 end
        //QcRilHook mQcRilHook = new QcRilHook();
        //modify for write the auto test results to NV2499 by songguangyu 20140218 start
        switch (application.getTestMode()) {
            case CITTestHelper.PCB_CIT1:
                //Modify for CIT optimization by xiasiping 20140730 start
                try {
//                            if (method_write != null) {
//                                Log.e(TAG,"xsp_method_write = " + method_write);
//                                Object[] mArguments = new Object[]{19,result};
//                                Method mmw = mQcRilHook.getClass().getMethod(method_write, new Class[] {int.class,int.class});
//                                mmw.invoke(mQcRilHook,mArguments);
//                            } else {
//                                Log.e(TAG,"xsp_method_write is null ");
//                                mQcRilHook.writeNvFactoryData(19,result);
//                            }
                    byte[] data = application.getNv2499Data();
                    data[19 - 1] = (byte) result;
                    NvJniItems.getInstance().writeNv2499(data);
                } catch (Exception e) {
                    Log.e(TAG, "~~~~~~Exception" + e);
                    //mQcRilHook.writeNvFactoryData(19,result);
                    Toast.makeText(TestActivity.this, getString(R.string.invoke_method_fail), 3000).show();
                }
                //mQcRilHook.writeNvFactoryData(19,result);
                //Modify for CIT optimization by xiasiping 20140730 end
                Log.i(TAG, "record result pcb cit 1");
                break;
            case CITTestHelper.PCB_CIT2:
                //Modify for CIT optimization by xiasiping 20140730 start
                try {
//                            if (method_write != null) {
//                                Log.e(TAG,"xsp_method_write = " + method_write);
//                                Object[] mArguments = new Object[]{18,result};
//                                Method mmw = mQcRilHook.getClass().getMethod(method_write, new Class[] {int.class,int.class});
//                                mmw.invoke(mQcRilHook,mArguments);
//                            } else {
//                                Log.e(TAG,"xsp_method_write is null ");
//                                mQcRilHook.writeNvFactoryData(18,result);
//                            }
                    byte[] data = application.getNv2499Data();
                    data[18 - 1] = (byte) result;
                    NvJniItems.getInstance().writeNv2499(data);
                } catch (Exception e) {
                    Log.e(TAG, "~~~~~~Exception" + e);
                    //mQcRilHook.writeNvFactoryData(18,result);
                    Toast.makeText(TestActivity.this, getString(R.string.invoke_method_fail), 3000).show();
                }
                //mQcRilHook.writeNvFactoryData(18,result);
                //Modify for CIT optimization by xiasiping 20140730 end
                Log.i(TAG, "record result pcb cit 2");
                break;
            case CITTestHelper.PCB_CIT3:
                //Modify for CIT optimization by xiasiping 20140730 start
                try {
//                            if (method_write != null) {
//                                Log.e(TAG,"xsp_method_write = " + method_write);
//                                Object[] mArguments = new Object[]{17,result};
//                                Method mmw = mQcRilHook.getClass().getMethod(method_write, new Class[] {int.class,int.class});
//                                mmw.invoke(mQcRilHook,mArguments);
//                            } else {
//                                Log.e(TAG,"xsp_method_write is null ");
//                                mQcRilHook.writeNvFactoryData(17,result);
//                            }
                    byte[] data = application.getNv2499Data();
                    data[17 - 1] = (byte) result;
                    NvJniItems.getInstance().writeNv2499(data);
                } catch (Exception e) {
                    Log.e(TAG, "~~~~~~Exception" + e);
                    //mQcRilHook.writeNvFactoryData(17,result);
                    Toast.makeText(TestActivity.this, getString(R.string.invoke_method_fail), 3000).show();
                }
                //mQcRilHook.writeNvFactoryData(17,result);
                //Modify for CIT optimization by xiasiping 20140730 end
                Log.i(TAG, "record result pcb cit 3");
                break;
            case CITTestHelper.COMPLETE_CIT1:
                //Modify for CIT optimization by xiasiping 20140730 start
                try {
//                            if (method_write != null) {
//                                Log.e(TAG,"xsp_method_write = " + method_write);
//                                Object[] mArguments = new Object[]{9,result};
//                                Method mmw = mQcRilHook.getClass().getMethod(method_write, new Class[] {int.class,int.class});
//                                mmw.invoke(mQcRilHook,mArguments);
//                            } else {
//                                Log.e(TAG,"xsp_method_write is null ");
//                                mQcRilHook.writeNvFactoryData(9,result);
//                            }
                    byte[] data = application.getNv2499Data();
                    data[9 - 1] = (byte) result;
                    NvJniItems.getInstance().writeNv2499(data);
                } catch (Exception e) {
                    Log.e(TAG, "~~~~~~Exception" + e);
                    //mQcRilHook.writeNvFactoryData(9,result);
                    Toast.makeText(TestActivity.this, getString(R.string.invoke_method_fail), 3000).show();
                }
                //mQcRilHook.writeNvFactoryData(9,result);
                //Modify for CIT optimization by xiasiping 20140730 end
                Log.i(TAG, "record result complete cit 1");
                break;
            case CITTestHelper.COMPLETE_CIT2:
                //Modify for CIT optimization by xiasiping 20140730 start
                try {
//                            if (method_write != null) {
//                                Log.e(TAG,"xsp_method_write = " + method_write);
//                                Object[] mArguments = new Object[]{8,result};
//                                Method mmw = mQcRilHook.getClass().getMethod(method_write, new Class[] {int.class,int.class});
//                                mmw.invoke(mQcRilHook,mArguments);
//                            } else {
//                                Log.e(TAG,"xsp_method_write is null ");
//                                mQcRilHook.writeNvFactoryData(8,result);
//                            }
                    byte[] data = application.getNv2499Data();
                    data[8 - 1] = (byte) result;
                    NvJniItems.getInstance().writeNv2499(data);
                } catch (Exception e) {
                    Log.e(TAG, "~~~~~~Exception" + e);
                    //mQcRilHook.writeNvFactoryData(8,result);
                    Toast.makeText(TestActivity.this, getString(R.string.invoke_method_fail), 3000).show();
                }
                //mQcRilHook.writeNvFactoryData(8,result);
                //Modify for CIT optimization by xiasiping 20140730 end
                Log.i(TAG, "record result complete cit 2");
                break;
            case CITTestHelper.COMPLETE_CIT3:
                //Modify for CIT optimization by xiasiping 20140730 start
                try {
//                            if (method_write != null) {
//                                Log.e(TAG,"xsp_method_write = " + method_write);
//                                Object[] mArguments = new Object[]{7,result};
//                                Method mmw = mQcRilHook.getClass().getMethod(method_write, new Class[] {int.class,int.class});
//                                mmw.invoke(mQcRilHook,mArguments);
//                            } else {
//                                Log.e(TAG,"xsp_method_write is null ");
//                                mQcRilHook.writeNvFactoryData(7,result);
//                            }
                    byte[] data = application.getNv2499Data();
                    data[7 - 1] = (byte) result;
                    NvJniItems.getInstance().writeNv2499(data);
                } catch (Exception e) {
                    Log.e(TAG, "~~~~~~Exception" + e);
                    //mQcRilHook.writeNvFactoryData(7,result);
                    Toast.makeText(TestActivity.this, getString(R.string.invoke_method_fail), 3000).show();
                }
                //mQcRilHook.writeNvFactoryData(7,result);
                //Modify for CIT optimization by xiasiping 20140730 end
                Log.i(TAG, "record result complete cit 3");
                break;
            //modify for write the auto test results to NV2499 by songguangyu 20140218 end
            default:
                break;
        }
        if (result == 80) {
            Toast.makeText(TestActivity.this, getString(R.string.test_info_1) + index + getString(R.string.test_info_2), 3000).show();
            Log.i(TAG, "record result success");

        }
    }

    public void backKeyOp(final Activity activity) {
        if (temp_autoTest) {
            new AlertDialog.Builder(activity).setTitle(
                    R.string.alert_title).setMessage(
                    R.string.alert_content).setPositiveButton(
                    R.string.alert_dialog_ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            record(70);
                            application.setAutoTest(false);
                            //add for mark test results by songguangyu 20140217 start
                            if (CITTestHelper.EXTRA_TEST_TYPE_VERSION.equals(
                                    getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_VERSION))) {
                                application.setVersionTestResult(CITTestHelper.TEST_RESULT_FAIL);
                            } else if (CITTestHelper.EXTRA_TEST_TYPE_SPEAKER_PINKNOISE_TEST.equals(
                                    getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_SPEAKER_PINKNOISE_TEST))) {
                                application.setSpeakerPinknoiseTestResult(CITTestHelper.TEST_RESULT_FAIL);
                            } else if (CITTestHelper.EXTRA_TEST_TYPE_RECEIVER_PINKNOISE_TEST.equals(
                                    getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_RECEIVER_PINKNOISE_TEST))) {
                                application.setReceiverPinknoiseTestResult(CITTestHelper.TEST_RESULT_FAIL);
                            } else {
                                setTestResult(CITTestHelper.TEST_RESULT_FAIL);
                            }
                            //add for mark test results by songguangyu 20140217 end
                            activity.finish();
                        }
                    }).setNegativeButton(R.string.alert_dialog_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                        }
                    }).create().show();

        } else {
            //add for mark test results by songguangyu 20140217 start
            if (CITTestHelper.EXTRA_TEST_TYPE_VERSION.equals(
                    getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_VERSION))) {
                application.setVersionTestResult(CITTestHelper.TEST_RESULT_FAIL);
            } else if (CITTestHelper.EXTRA_TEST_TYPE_SPEAKER_PINKNOISE_TEST.equals(
                    getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_SPEAKER_PINKNOISE_TEST))) {
                application.setSpeakerPinknoiseTestResult(CITTestHelper.TEST_RESULT_FAIL);
            } else if (CITTestHelper.EXTRA_TEST_TYPE_RECEIVER_PINKNOISE_TEST.equals(
                    getIntent().getStringExtra(CITTestHelper.EXTRA_TEST_TYPE_RECEIVER_PINKNOISE_TEST))) {
                application.setReceiverPinknoiseTestResult(CITTestHelper.TEST_RESULT_FAIL);
            } else {
                setTestResult(CITTestHelper.TEST_RESULT_FAIL);
            }
            //add for mark test results by songguangyu 20140217 end
            finish();
            Log.i("RecordResultHelper", "fail");
        }
    }

    @Override
    public void finish() {
        Log.i(TAG, "finish() start");
//        if (mQcRilHook != null) {
//            mQcRilHook.dispose();
//
//        }
        super.finish();
    }
}
