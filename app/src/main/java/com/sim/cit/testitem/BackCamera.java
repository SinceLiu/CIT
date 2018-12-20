package com.sim.cit.testitem;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;
import java.io.File;
import com.sim.cit.CITTestHelper;
import com.sim.cit.R;
import com.sim.cit.TestActivity;
//Modify for CIT optimization by xiasiping 20140730 start
import java.io.IOException;
import java.util.List;
import com.sim.cit.MyXml;
import com.sim.cit.XNode;
import com.sim.cit.XProperty;
import com.sim.cit.XMethod;
import com.sim.cit.MyXmlUtils;
import android.widget.Toast;
import java.lang.reflect.Method;
//Modify for CIT optimization by xiasiping 20140730 end

public class BackCamera extends TestActivity {
    public static final String TAG = "BackCamera";
    public static final String BACKCAMERA = "/sys/devices/fda0c000.qcom,cci/20.qcom,camera/media1";
    private TextView txtMsg;
    //Modify for CIT optimization by xiasiping 20140730 start
    private static MyXml mxml;
    private List<XProperty> xProperties;
    private List<XMethod> xMethods;
    private List<XNode> xNodes;
    private String camera = null;
    //Modify for CIT optimization by xiasiping 20140730 end
    //Modify for show result by custom view by lxx 20180827
    private Button retake;
    private ImageView photo;
    private File file;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutId = R.layout.custom_camera;
        super.onCreate(savedInstanceState);
        //Modify for CIT optimization by xiasiping 20140730 start
        try {
            mxml = new MyXmlUtils().getMxml();
            xProperties = mxml.getXProperties();
            xMethods = mxml.getXMethods();
            xNodes = mxml.getXNodes();
        } catch (Exception e) {
            Log.e(TAG, "xsp_May be config file has error");
        }
        if (xNodes != null) {
            for (XNode xn : xNodes) {
                String name = xn.getName();
                if ("camera".equals(name)) {
                    camera = xn.getValue();
                }
            }
        }
        //Modify for CIT optimization by xiasiping 20140730 end

        if (CITTestHelper.HAS_FLASH_LIGHT) {
//            CommonDrive.flashlightControl("1");
        }

//        txtMsg = (TextView)findViewById(R.id.txt_one);
//        txtMsg.setText(R.string.camera_illustration);

        //Modify for show result by custom view by lxx 20180827
        photo = (ImageView) findViewById(R.id.photo);
        retake = (Button) findViewById(R.id.btn_retake);
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraTest(true);
            }
        });

/*        try {
            Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
//            20121206-modified for test camera by lvhongshan start
//            Intent intent = new Intent("android.media.action.STILL_IMAGE_CAMERA_CIT");//
//            Intent intent = new Intent(this,com.android.camera.Camera.class);//can not make
//            20121206-modified for test camera by lvhongshan start
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("camerasensortype", 0);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d("FlashAndCameraTest", "the camera activity is not exist");
            e.printStackTrace();
        }*/
        String model = SystemProperties.get("ro.product.model", "unknown");
        if (model.contains("XP7700")) {
            //Modify for CIT optimization by xiasiping 20140730 start
            File fileBack = null;
            if (camera != null) {
                Log.e(TAG, "xsp_camera = " + camera);
                fileBack = new File(camera);
            } else {
                Log.e(TAG, "xsp_camera is null ");
                fileBack = new File(BACKCAMERA);
            }
            //Modify for CIT optimization by xiasiping 20140730 end
            //File fileBack = new File(BACKCAMERA);
            if (fileBack.exists()) {
                //Toast.makeText(this, "has file " , 2000).show();
                cameraTest(true);
            } else {
                Toast.makeText(this, getString(R.string.connect_fail), 3000).show();
            }
        } else {
            cameraTest(true);
        }
        //cameraTest(true);

    }

    private void cameraTest(boolean b) {
        Log.i(TAG, "*****cameraTest  BEGIN*****");
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (b) {
                intent.putExtra("aging_test_camera", "rear");
                intent.putExtra("withouthud", true);
                intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
            } else {
                intent.putExtra("aging_test_camera", "front");
                intent.putExtra("withouthud", true);
                intent.putExtra("camerasensortype", 1);
            }

            int cameraId = getCameraId(true);
            Log.i(TAG, "-----cameraTest  BEGIN-----cameraId = " + cameraId);
            intent.putExtra("android.intent.extras.CAMERA_FACING", cameraId);

            //自定义View显示拍照照片，Q9相机预览相片不清晰（压缩过）
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + ".jpg");
            Uri imageUri = Uri.fromFile(file); //sdk23之后用FileProvider
            //指定照片保存路径，image.jpg为一个临时文件，每次拍照后这个图片都会被替换
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("android.intent.extra.quickCapture", true);  //不预览图片
            startActivityForResult(intent, 1);

        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "the camera activity is not exist");
            e.printStackTrace();
        }
        Log.i(TAG, "*****cameraTest  END*****");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bitmap!=null){
            bitmap.recycle();
            bitmap = null;
        }
        if(file.exists()&&file.isFile()){  //删除照片
            file.delete();
        }
        if (CITTestHelper.HAS_FLASH_LIGHT) {
//            CommonDrive.flashlightControl("0");
        }
        //sendBroadcast(new Intent("com.android.camera.stopcamera"));
    }

    private int getCameraId(boolean backCamera) {
        //切换前后摄像头
        int cameraCount = 0;
        CameraInfo cameraInfo = new CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        Log.i(TAG, "----------------------cameraCount = " + cameraCount);
        Log.i(TAG, "---------CAMERA_FACING_BACK = " + Camera.CameraInfo.CAMERA_FACING_BACK
                + "------CAMERA_FACING_FRONT = " + Camera.CameraInfo.CAMERA_FACING_FRONT);
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            Log.i(TAG, "----------------------cameraInfo.facing = " + cameraInfo.facing);
            if (backCamera && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i;
            } else if (!backCamera && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return i;
            }
        }
        return -11111;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("BackCamera", "onResult");
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("BackCamera", "---------" + Uri.fromFile(file));
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if(bitmap!=null){
            if (bitmap.getWidth() > bitmap.getHeight()) {
                photo.setImageBitmap(rotatePhotos(bitmap));
            } else {
                photo.setImageBitmap(bitmap);
            }
        }
    }

    //将图片旋转90°
    public static Bitmap rotatePhotos(Bitmap bitmap) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            try {
                m.setRotate(90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                Bitmap bmp2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                bitmap.recycle();
                bitmap = bmp2;
            } catch (Exception ex) {
                Log.e("BackCamera", "旋转图片创建失败");
            }
        }
        return bitmap;

    }
}
