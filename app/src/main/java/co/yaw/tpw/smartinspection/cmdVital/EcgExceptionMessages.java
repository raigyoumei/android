package co.yaw.tpw.smartinspection.cmdVital;


import android.util.Log;

import com.neurosky.ecg_algo.NeuroSkyECGCallback;



public class EcgExceptionMessages implements Runnable {

    private final static String TAG = EcgExceptionMessages.class.getSimpleName();

    int exceptionCode = 0;

    public EcgExceptionMessages(int code) {
        exceptionCode = code;
    }


    @Override
    public void run() {
        switch (exceptionCode) {
            case NeuroSkyECGCallback.ECG_MISS_SHARED_LIBRARY:
                Log.w(TAG,"ECG_MISS_SHARED_LIBRARY" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_SDK_HAS_NOT_BEEN_INITIALIZED:
                Log.w(TAG,"ECG_SDK_HAS_NOT_BEEN_INITIALIZED" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_USER_PROFILE_HAS_NOT_BEEN_SET_UP:
                Log.w(TAG,"ECG_USER_PROFILE_HAS_NOT_BEEN_SET_UP" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_USER_PROFILE_CORRUPTED_DATA:
                Log.w(TAG,"ECG_USER_PROFILE_CORRUPTED_DATA" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_USER_PROFILE_EMPTY_FILE:
                Log.w(TAG,"ECG_USER_PROFILE_EMPTY_FILE" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_INVALID_INPUT_AGE:
                Log.w(TAG,"ECG_INVALID_INPUT_AGE" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_INVALID_INPUT_NAME:
                Log.w(TAG,"ECG_INVALID_INPUT_NAME" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_INVALID_INPUT_HEIGHT:
                Log.w(TAG,"ECG_INVALID_INPUT_HEIGHT" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_INVALID_INPUT_WEIGHT:
                Log.w(TAG,"ECG_INVALID_INPUT_WEIGHT" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_INVALID_INPUT_PATH:
                Log.w(TAG,"ECG_INVALID_INPUT_PATH" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_INVALID_CREATE_FILE:
                Log.w(TAG,"ECG_INVALID_CREATE_FILE" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_INSUFFICIENT_DATA:
                Log.w(TAG,"ECG_INSUFFICIENT_DATA" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_INVALID_INPUT_SAMPLE_RATE:
                Log.w(TAG,"ECG_INVALID_INPUT_SAMPLE_RATE" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_INVALID_INPUT_LICENSE:
                Log.w(TAG,"ECG_INVALID_INPUT_LICENSE" + "\n");
                break;
            case NeuroSkyECGCallback.ECG_EXCEPTION_LICENSE_EXPIRED:
                Log.w(TAG, "ECG_EXCEPTION_LICENSE_EXPIRED");
                break;
            default:
                break;
        }
    }

}
