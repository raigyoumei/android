package co.yaw.tpw.smartinspection.bltUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Map;


public class HandlerUtil {

    private final static String TAG = HandlerUtil.class.getSimpleName();

    public final static String INFO = "INFO";
    private Handler mHandler = null;


    public HandlerUtil( Handler handler) {
        Log.i(TAG, "HandlerUtil");

        mHandler = handler;
    }


    public void sendHandler(int type) {

        Log.d(TAG, "sendHandler type:" + type);

        Message msg = new Message();

        msg.what = type;

        mHandler.sendMessage(msg);
    }


    public void sendHandler(int type, byte[] value) {

        String hexStr = HexUtil.formatHexString(value,true);

        Log.d(TAG, "sendHandler type:" + type + "---"+ hexStr);

        Message msg = new Message();

        Bundle ble = new Bundle();
        ble.putByteArray(INFO, value);
        msg.setData(ble);
        msg.what = type;

        mHandler.sendMessage(msg);

    }


    public void sendHandler(int type, int value) {

        Log.d(TAG, "sendHandler type:" + type + "---"+ value);

        Message msg = new Message();

        Bundle ble = new Bundle();
        ble.putInt(INFO, value);
        msg.setData(ble);
        msg.what = type;

        mHandler.sendMessage(msg);

    }



    public void sendHandler(int type, int value, Object object) {

        Log.d(TAG, "sendHandler type:" + type + "---"+ value);

        Message msg = new Message();

        Bundle ble = new Bundle();
        ble.putInt(INFO, value);
        msg.setData(ble);
        msg.what = type;
        msg.obj = object;

        mHandler.sendMessage(msg);

    }



    public void sendHandler(int type, String value) {

        //String hexStr = HexUtil.formatHexString(value,true);

        Log.d(TAG, "sendHandler type:" + type + "---" + value);

        Message msg = new Message();

        if((value != null) && (value.length() > 0)){
            Bundle ble = new Bundle();
            ble.putString(INFO, value);
            msg.setData(ble);
        }

        msg.what = type;
        mHandler.sendMessage(msg);

    }


    public void sendHandler(int type,  Map<String, String> map) {

        //String hexStr = HexUtil.formatHexString(value,true);

        if(map == null){
            Log.e(TAG, "sendHandler map is null");
            return;
        }

        Log.d(TAG, "sendHandler type:" + type + "---"+ map.toString());

        Message msg = new Message();
        Bundle ble = new Bundle();

        for (Map.Entry<String, String> device : map.entrySet()) {
            ble.putString(device.getKey(),device.getValue());
        }

        msg.setData(ble);
        msg.what = type;

        mHandler.sendMessage(msg);

    }

}
