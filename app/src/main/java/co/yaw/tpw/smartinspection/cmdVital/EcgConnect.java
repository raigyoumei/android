package co.yaw.tpw.smartinspection.cmdVital;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import co.yaw.tpw.smartinspection.bltUtil.DateUtil;
import co.yaw.tpw.smartinspection.bltUtil.HandlerUtil;

public class EcgConnect {

    private final static String TAG = VitalHandlerMsg.class.getSimpleName();

    private static final String SHAREFILENAME = "NeuroskyUserInfo";

    public static final int MSG_UPDATE_BAD_PACKET = 10001;
    public static final int MSG_UPDATE_STATE = 10002;
    public static final int MSG_UPDATE_BAD_RECODE = 10003;

    public static final String USER_NAME = "user_name";
    public static final String USER_GENDER = "user_gender";
    public static final String USER_AGE = "user_age";
    public static final String USER_HEIGHT = "user_height";
    public static final String USER_WEIGHT = "user_weight";

    private Activity mActivity = null;
    private HandlerUtil mHandlerUtil = null;

    private TgStreamReader mTgStreamReader = null;
    private SharedPreferences mSharePreferences = null;
    private SharedPreferences.Editor mShareEditor = null;
    private BufferedOutputStream mOutputStreamRawData = null;

    private boolean mBltConnectFailed = false;

    public EcgConnect(Activity activity) {
        mActivity = activity;
    }


    public String getTgStreamVersion() {
        return TgStreamReader.getVersion();
    }

    public void initTgStream(){

        TgStreamReader.redirectConsoleLogToDocumentFolder();
        mSharePreferences = mActivity.getSharedPreferences(SHAREFILENAME, Activity.MODE_PRIVATE);
        mShareEditor = mSharePreferences.edit();
    }



    public void setHandler(HandlerUtil handler){
        mHandlerUtil = handler;
    }


    public void connectTgStream(BluetoothDevice device){

        mTgStreamReader = new TgStreamReader(device, mTgStreamHandlerCallback);
        mTgStreamReader.connectAndStart();
        mTgStreamReader.setGetDataTimeOutTime(5);//5s for default
        mTgStreamReader.startLog();
    }


    private TgStreamHandler mTgStreamHandlerCallback = new TgStreamHandler() {

        @Override
        public void onStatesChanged(int connectionStates) {
            // TODO Auto-generated method stub
            Log.d(TAG, "connectionStates change to: " + connectionStates);

            switch (connectionStates) {

                case ConnectionStates.STATE_CONNECTED:

                    mBltConnectFailed = false;
                    //showToast("Connected", Toast.LENGTH_SHORT);

                    break;

                case ConnectionStates.STATE_WORKING:

                    startRecordRawData();
                    //showToast("STATE_WORKING", Toast.LENGTH_SHORT);

                    break;

                case ConnectionStates.STATE_GET_DATA_TIME_OUT:

                    stopRecordRawData();
                    //showToast("Get data timeout", Toast.LENGTH_SHORT);

                    break;

                case ConnectionStates.STATE_STOPPED:
                    stopRecordRawData();
                    //showToast("Stopped", Toast.LENGTH_SHORT);

                    break;

                case ConnectionStates.STATE_DISCONNECTED:
                    //showToast("STATE_DISCONNECTED", Toast.LENGTH_SHORT);

                    break;
                case ConnectionStates.STATE_ERROR:

                    //showToast("STATE_ERROR", Toast.LENGTH_SHORT);

                    break;
                case ConnectionStates.STATE_FAILED:

                    mBltConnectFailed = true;

                    //showToast("Connect failed!", Toast.LENGTH_SHORT);
                    break;
            }


            mHandlerUtil.sendHandler(MSG_UPDATE_STATE, connectionStates);

        }


        @Override
        public void onRecordFail(int a) {
            // TODO Auto-generated method stub
            Log.e(TAG,"onRecordFail: " +a);

            mHandlerUtil.sendHandler(MSG_UPDATE_BAD_RECODE);

        }

        @Override
        public void onChecksumFail(byte[] payload, int length, int checksum) {

            Log.d(TAG,"onChecksumFail");

            mHandlerUtil.sendHandler(MSG_UPDATE_BAD_PACKET);

        }

        @Override
        public void onDataReceived(int datatype, int data, Object obj) {

            Log.d(TAG,"onDataReceived");

            mHandlerUtil.sendHandler(datatype, data, obj);

        }

    };



    public void startRecordRawData(){

        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Log.e(TAG,"SD do not mounted:" + Environment.getExternalStorageState() );
            return ;
        }

        String path = Environment.getExternalStorageDirectory() +"/neurosky/NSDemoApp/RawData/";
        File dir = new File(path);
        if(!dir.exists()) dir.mkdirs();

        String fileName = mSharePreferences.getString(USER_NAME, "User") + "_" + DateUtil.getCustomTime(DateUtil.YMDHMSS) +".txt";

        File outputFile = new File(dir, fileName);
        mOutputStreamRawData = null;

        try {
            mOutputStreamRawData =  new BufferedOutputStream(new FileOutputStream(outputFile),100*1024);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public void stopRecordRawData(){

        if(mOutputStreamRawData == null){
            return;
        }

        try {
            mOutputStreamRawData.flush();
            mOutputStreamRawData.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            mOutputStreamRawData = null;
        }
    }



    public void stopTgStreamReader() {

        if(mBltConnectFailed){
            return;
        }

        if(mTgStreamReader != null){
            mTgStreamReader.stop();
            mTgStreamReader.close();
        }

        mTgStreamReader = null;
    }



    public Map<String, String> getUserProfile(){

        String user_name = mSharePreferences.getString(USER_NAME, "User");
        boolean female = mSharePreferences.getBoolean(USER_GENDER, true);
        int age = mSharePreferences.getInt(USER_AGE, 37);
        int weight = mSharePreferences.getInt(USER_WEIGHT, 65);
        int height = mSharePreferences.getInt(USER_HEIGHT, 173);

        Map<String, String> map = new HashMap<String, String>();
        map.put(USER_NAME, user_name);
        map.put(USER_GENDER, Boolean.toString(female));
        map.put(USER_AGE, String.valueOf(age));
        map.put(USER_WEIGHT, String.valueOf(weight));
        map.put(USER_HEIGHT, String.valueOf(height));

        return map;
    }


    public void saveUserProfile(Map<String, String> map){

        String user_name = map.get(USER_NAME);
        boolean female = Boolean.valueOf(map.get(USER_GENDER));
        int age = Integer.getInteger(map.get(USER_AGE));
        int weight = Integer.getInteger(map.get(USER_WEIGHT));
        int height = Integer.getInteger(map.get(USER_HEIGHT));

        mShareEditor.putString(USER_NAME,user_name);
        mShareEditor.putBoolean(USER_GENDER, female);
        mShareEditor.putInt(USER_AGE, age);
        mShareEditor.putInt(USER_WEIGHT, weight);
        mShareEditor.putInt(USER_HEIGHT, height);
        mShareEditor.commit();
    }


    public void outPutLogData(int data) {

        if(mOutputStreamRawData != null){

            String buf = data + "\n";

            try {
                mOutputStreamRawData.write(buf.getBytes());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
    }

}

