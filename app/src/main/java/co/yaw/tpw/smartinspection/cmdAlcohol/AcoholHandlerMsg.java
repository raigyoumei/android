package co.yaw.tpw.smartinspection.cmdAlcohol;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;

import java.util.List;
import java.util.Map;

import co.yaw.tpw.smartinspection.AlcoholMeasureActivity;
import co.yaw.tpw.smartinspection.bltUtil.BltDeviceUtil;
import co.yaw.tpw.smartinspection.bltUtil.DateUtil;
import co.yaw.tpw.smartinspection.bltUtil.HandlerUtil;
import co.yaw.tpw.smartinspection.camera.CameraCom;


public class AcoholHandlerMsg extends HandlerUtil {

    private final static String TAG = AcoholHandlerMsg.class.getSimpleName();

    public static final String FW_VERSION = "fw_version";
    public static final String USAGE_COUNT = "usage_count";
    public static final String VOLTAGE_VALUE = "voltage_value";
    public static final String ACOHOL_VALUE = "acohol_value";
    public static final String TEST_TIME = "test_time";


    private String mFwVersion = null;
    private String mUsageVal = null;
    private String mVoltageVal = null;
    private String mAcoholVal = null;
    private byte[] mImageVal = null;

    private TextView mMsgTextView = null;
    //private TextView mMsgVersionView = null;
    private TextView mUsageCountView = null;
    private TextView mArulValueView = null;
    //private Button mTestStartBtn = null;

    private Activity mActivity = null;
    private List<String> mDeviceList = null;
    private ArrayAdapter mArrayAdapter = null;
    private CameraCom mCameraView = null;

    private int mcrewBef = 0;
    private boolean mCheckEndFlag = false;


    public AcoholHandlerMsg(Activity activity) {

        Log.d(TAG, "AcoholHandlerMsg");

        mActivity = activity;
        mCheckEndFlag = false;

        //mMsgVersionView = mActivity.findViewById(R.id.fw_version);
        mUsageCountView = mActivity.findViewById(R.id.usge_count);
        mMsgTextView = mActivity.findViewById(R.id.test_msg);
        mArulValueView = mActivity.findViewById(R.id.test_value);
        //mTestStartBtn = mActivity.findViewById(R.id.measure_button);
    }


    public void initDeviceAdapter(List<String> deviceList,
                                 ArrayAdapter adapter ) {
        Log.d(TAG, "initDeviceAdapter");

        mDeviceList = deviceList;
        mArrayAdapter = adapter;
    }


    public void setCameraView(CameraCom cameraCom) {

        Log.d(TAG, "setCameraView");

        mCameraView = cameraCom;
        mCameraView.initCameraCom(this);
    }


    public void setMcrewInfo(int flag){
        mcrewBef = flag;
    }


    @Override
    public void handleMessage(Message msg){

        Bundle ble = msg.getData();
        Object value = null;

        Log.d(TAG, "handleMessage msg.what=" + msg.what);

        switch(msg.what){
            case AcoholCmd.MSG_COMMAND_STR:
                value = ble.getString(HandlerUtil.INFO);
                //CommandInfoStr((String)value);

                break;

            case AcoholCmd.MSG_MSG_INF:
                value = ble.getString(HandlerUtil.INFO);

                mMsgTextView.setText((String) value);

                break;

            case AcoholCmd.MSG_COMMAND_MSG_VERSION:

                mFwVersion = ble.getString(HandlerUtil.INFO);

                value = String.format(mActivity.getString(R.string.alcohol_test_FwVersion), mFwVersion);

                //if(mMsgVersion != null) {
                //    mMsgVersion.setText((String) value);
                //}
                break;

            case AcoholCmd.MSG_COMMAND_MSG_TEST_COUNT:

                mUsageVal = ble.getString(HandlerUtil.INFO);

                value = String.format(mActivity.getString(R.string.alcohol_test_usage_count), mUsageVal);

                mUsageCountView.setText((String) value);

                break;

            case AcoholCmd.MSG_COMMAND_VOLTAGE_OK:

                mVoltageVal = ble.getString(HandlerUtil.INFO);
                value = String.format(mActivity.getString(R.string.alcohol_test_voltage_ok), mVoltageVal);

                //if(mMsgVersion != null) {
                //    mMsgVersion.append("\n" + value);
                //}
                break;

            case AcoholCmd.MSG_COMMAND_VOLTAGE_LOW:

                mVoltageVal = ble.getString(HandlerUtil.INFO);

                value = String.format(mActivity.getString(R.string.alcohol_test_voltage_low), mVoltageVal);

                //if(mMsgVersion != null) {
                //    mMsgVersion.append("\n" + value);
                //}

                break;

            case AcoholCmd.MSG_COMMAND_TEST_WAITE:
                value = ble.getString(HandlerUtil.INFO);
                value = String.format(mActivity.getString(R.string.alcohol_test_reserve), (String)value);

                mMsgTextView.setText((String) value);

                mCameraView.cameraStart();

                break;

            case AcoholCmd.MSG_COMMAND_VALUE_TEST_AL:

                mAcoholVal = ble.getString(HandlerUtil.INFO);

                mArulValueView.setText(mAcoholVal);
                double alcohol = Double.parseDouble(mAcoholVal);

                if(alcohol < AcoholCmd.AltTestSTLow/100){
                    mArulValueView.setTextColor(mActivity.getResources().getColor(R.color.colorGreen));
                } else if(alcohol <= AcoholCmd.AltTestSTHigh/100){
                    mArulValueView.setTextColor(mActivity.getResources().getColor(R.color.colorYellow));
                }else{
                    mArulValueView.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));
                }

                mCameraView.captureImageRandom();

                break;

            case AcoholCmd.MSG_COMMAND_VALUE_TEST_START:

                mCameraView.cameraStart();

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_start));

                break;

            case AcoholCmd.MSG_COMMAND_VALUE_TEST_END:

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_end_ok));

                mCameraView.captureImageRandomEnd();

                mCheckEndFlag = true;

                break;

            case AcoholCmd.MSG_COMMAND_WARNNING_NO_BREATH:

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_start));

                break;

            case AcoholCmd.MSG_COMMAND_WARNNING_TEST_END:

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_end_no_breath));

                mCameraView.cameraStop();

                break;

            case AcoholCmd.MSG_COMMAND_MSG_TEST:

                mCameraView.cameraStart();

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_testing));

                break;

            case AcoholCmd.MSG_COMMAND_WARNNING_START_TEST_PRESS:

                //value = ble.getString(HandlerUtil.INFO);
                //value = String.format(mActivity.getString(R.string.test_count), (String)value);

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_warnning_start_press));

                break;


            case AcoholCmd.MSG_COMMAND_WARNNING_TEST_START_AHL:

                //value = ble.getString(HandlerUtil.INFO);
                //value = String.format(mActivity.getString(R.string.test_count), (String)value);

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_warnning_start_alcohol));

                break;


            case AcoholCmd.MSG_COMMAND_WARNNING_BLOWING:

                //value = ble.getString(HandlerUtil.INFO);
                //value = String.format(mActivity.getString(R.string.test_before_voltage_low), (String)value);

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_warnning_blowing));

                break;


            case AcoholCmd.MSG_COMMAND_WARNNING_COUNT_OVER:

                //value = ble.getString(HandlerUtil.INFO);
                //value = String.format(mActivity.getString(R.string.test_before_voltage_low), (String)value);

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_warnning_usage_over));

                break;

            case AcoholCmd.MSG_COMMAND_WARNNING_IC_READ:

                //value = ble.getString(HandlerUtil.INFO);
                //value = String.format(mActivity.getString(R.string.test_before_voltage_low), (String)value);

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_warnning_ic_read));

                break;

            case AcoholCmd.MSG_COMMAND_TEST_END:

                //mEndTime = System.currentTimeMillis();
                //TextView testTime = (TextView) findViewById(R.id.testTime);

                //String time = ((mEndTime - mStartTime)/1000)+"";
                //time = String.format(mActivity.getString(R.string.test_time), (String)time);

                //testTime.setText(time);

                //mCameraView.captureImage(mCamerCallBack);

                saveAcoholTestInfo();

                break;


            case AcoholCmd.MSG_COMMAND_POWER_OFF:

                //mBltDeviceUtil.initBluetoothGatt();
                //mStartTime = 0;

//                if(mTestStartBtn != null) {
//                    mTestStartBtn.setEnabled(true);
//                }

                //mCameraView.cameraStop();

                if(mDeviceList != null) {
                    String iniStr = mDeviceList.get(0);
                    mDeviceList.clear();
                    mDeviceList.add(iniStr);
                    mArrayAdapter.notifyDataSetChanged();
                }

                // blt接続を切り
                ((AlcoholMeasureActivity)mActivity).getBltDeviceUtil().initBluetoothGatt();

                //mCameraView.cameraStop();

                break;


            case BltDeviceUtil.MSG_SCAN_START:
                //mStartTime = System.currentTimeMillis();

                //value = ble.getString(HandlerUtil.INFO);

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_scan_start));

                if(mDeviceList != null) {
                    String iniStr = mDeviceList.get(0);
                    mDeviceList.clear();
                    mDeviceList.add(iniStr);
                    mArrayAdapter.notifyDataSetChanged();
                }

                break;

            case BltDeviceUtil.MSG_DEVACE_SCAN_END:
                //mStartTime = System.currentTimeMillis();

                //value = ble.getString(HandlerUtil.INFO);

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_scan_end));

                break;

            case BltDeviceUtil.MSG_DEVACE_FIND:
                value = ble.getString(HandlerUtil.INFO);
                //mMsgText.setText((String)value);

                if(mDeviceList != null) {
                    mDeviceList.add((String) value);
                    mArrayAdapter.notifyDataSetChanged();
                }

                break;

            case BltDeviceUtil.MSG_DEVACE_CONNECTING:

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_connect));

                initTestInfo();

                break;

            case BltDeviceUtil.MSG_DEVACE_CONNECT:
                //value = ble.getString(HandlerUtil.INFO);
                //mMsgText.setText((String)value);

                //mDeviceList.add((String)value);
                //mArrayAdapter.notifyDataSetChanged();

//                if(mTestStartBtn != null) {
//                    mTestStartBtn.setEnabled(false);
//                }

                break;

            case BltDeviceUtil.MSG_DEVACE_DISCONNECT:
                //value = ble.getString(HandlerUtil.INFO);
                //mMsgText.setText((String)value);

                //mDeviceList.add((String)value);
                //mArrayAdapter.notifyDataSetChanged();

                //mMsgText.setText(mActivity.getString(R.string.test_connect_ng));

//                if(mTestStartBtn != null) {
//                    mTestStartBtn.setEnabled(true);
//                }

                break;


            case BltDeviceUtil.MSG_DEVACE_CONNECT_NG:
                //value = ble.getString(HandlerUtil.INFO);

                mMsgTextView.setText(mActivity.getString(R.string.alcohol_test_connect_ng));

//                if(mTestStartBtn != null) {
//                    mTestStartBtn.setEnabled(true);
//                }

                break;


            //撮影した写真データ
            case CameraCom.MSG_IMAGE_CAPTURE:

                Log.d(TAG, "CameraCom.MSG_IMAGE_CAPTURE msg.what=" + msg.what);

                mImageVal = ble.getByteArray(HandlerUtil.INFO);

                break;

            default:
                break;

        }
    }


    public void saveAcoholTestInfo(){

        String time = DateUtil.getCustomTime(DateUtil.YMDHMSS);

        Log.d(TAG, "saveAcoholTestInfo mFwVersion=" + mFwVersion);
        Log.d(TAG, "saveAcoholTestInfo mUsageVal=" + mUsageVal);
        Log.d(TAG, "saveAcoholTestInfo mVoltageVal=" + mVoltageVal);
        Log.d(TAG, "saveAcoholTestInfo mAcoholVal=" + mAcoholVal);
        Log.d(TAG, "saveAcoholTestInfo mImageVal=" + mImageVal);
        Log.d(TAG, "saveAcoholTestInfo mcrewBef=" + mcrewBef);

        Log.d(TAG, "saveAcoholTestInfo time=" + time);


        // ログイン認証情報を取得処理
        //////////////////////////
        //////////////////////////


        //サーバーに保存する処理
        /////////////////////////
        /////////////////////////
        /////////////////////////

    }


    public void initTestInfo(){

        mFwVersion = null;
        mUsageVal = null;
        mVoltageVal = null;
        mAcoholVal = null;
        mImageVal = null;
    }


    public boolean isChecked(){
        return mCheckEndFlag;
    }
}
