package co.yaw.tpw.smartinspection.camera;


import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;

import java.io.File;
import java.io.OutputStream;
import java.util.Random;

import co.yaw.tpw.smartinspection.bltUtil.HandlerUtil;
import co.yaw.tpw.smartinspection.maskview.CameraMaskView;


public class CameraCom {

    private final static String TAG = CameraCom.class.getSimpleName();

    public static final int MSG_IMAGE_CAPTURE = 2001;

    private CameraMaskView mCameraView = null;
    private CameraKitEventCallback mCamerCallBack = null;
    private Activity mActivity = null;
    private boolean mRandom = false;
    private byte[] mImageData = null;
    private String mImagePath = null;
    private long mRandomNum = 0;
    private boolean mCapImage = false;
    private boolean mStopFlag = false;

    private HandlerUtil mHandlerUtil = null;


    public CameraCom(Activity activity, CameraMaskView cameraView) {

        Log.i(TAG, "cameraCom");

        mActivity = activity;
        mCameraView = cameraView;

        Random random=new Random();
        mRandomNum = random.nextInt(100);
    }

    public void setStopFlag(boolean flag){
        mStopFlag = flag;
    }


    public void initCameraCom( Handler handler) {

        Log.i(TAG, "cameraCom");

        mHandlerUtil = new HandlerUtil(handler);

        mCameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        mCameraView.setMethod(CameraKit.Constants.METHOD_STANDARD);
        mCameraView.setCropOutput(false);
        mCameraView.setPinchToZoom(true);

        mCamerCallBack = new CameraKitEventCallback<CameraKitImage>() {

            @Override
            public void callback(CameraKitImage event) {

                mImageData = new byte[event.getJpeg().length];

                System.arraycopy(event.getJpeg(), 0, mImageData, 0, event.getJpeg().length);

                Bitmap bitmap = event.getBitmap();

                File mediaFile = new File(mImagePath);

                Uri uri = Uri.fromFile(mediaFile);

                try {

                    OutputStream fos = null;

                    if (!mediaFile.exists()) {
                        mediaFile.createNewFile();
                    }

                    fos = mActivity.getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (Exception e) {

                    Log.e(TAG, "Exception=" + e.toString());

                    e.printStackTrace();
                }

                // ギャラリーへスキャンを促す
                MediaScannerConnection.scanFile(
                        mActivity,
                        new String[]{uri.getPath()},
                        new String[]{"image/jpeg"},
                        null
                );

                mHandlerUtil.sendHandler(MSG_IMAGE_CAPTURE, mImageData);

                if (mStopFlag){
                    mCameraView.stop();
                }

            }
        };

    }


    public void cameraStart() {

        Log.i(TAG, "cameraStart");

        mCapImage = false;
        mCameraView.start();
    }


    public void cameraStop() {

        Log.i(TAG, "cameraStop");

        mCapImage = false;
        mCameraView.stop();

    }


    public String captureImageRandom() {

        long time = System.currentTimeMillis();

        if(mCapImage) {
            return null;
        }

        // ランダム撮影、だ撮影しない場合
        if(time% mRandomNum!= 0){
            return null;
        }

        mCapImage = true;
        mImagePath = getImageName();
        if(mImagePath == null){
            return null;
        }

        try{
            mCameraView.captureImage(mCamerCallBack);

        } catch (Exception e){
            Log.e(TAG, "Exception="+e.toString());
        }

        return mImagePath;
    }



    public String captureImageEnd() {

        if(mCapImage) {
            return null;
        }

        mCapImage = true;
        mImagePath = getImageName();
        if(mImagePath == null){
            return null;
        }

        try{
            mCameraView.captureImage(mCamerCallBack);

        } catch (Exception e){
            Log.e(TAG, "Exception="+e.toString());
        }

        return mImagePath;
    }



    public String captureImage() {

        mImagePath = getImageName();
        if(mImagePath == null){
            return null;
        }

        try{
            mCameraView.captureImage(mCamerCallBack);

        } catch (Exception e){
            Log.e(TAG, "Exception="+e.toString());
        }

        return mImagePath;
    }



    private String getImageName(){

        String name = System.currentTimeMillis() + ".jpg";

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        //カメラ画像を保存するディレクトリ
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                Log.e(TAG, "mediaStorageDir.mkdirs error");
                return null;
            }
        }

        String path = mediaStorageDir.getPath() + File.separator + name;

        return path;
    }


}
