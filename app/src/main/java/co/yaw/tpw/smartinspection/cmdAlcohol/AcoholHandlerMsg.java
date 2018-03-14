package co.yaw.tpw.smartinspection.cmdAlcohol;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;

import java.util.List;

import co.yaw.tpw.smartinspection.AlcoholMeasureActivity;
import co.yaw.tpw.smartinspection.bltUtil.BltDeviceUtil;
import co.yaw.tpw.smartinspection.bltUtil.HandlerUtil;
import co.yaw.tpw.smartinspection.camera.CameraCom;


public class AcoholHandlerMsg extends HandlerUtil {

    private final static String TAG = AcoholHandlerMsg.class.getSimpleName();

    private TextView mMsgText = null;
    //private TextView mMsgVersion = null;
    private TextView mUsageCount = null;
    private TextView mArulValue = null;
    //private Button mTestStartBtn = null;

    private Activity mActivity = null;
    private List<String> mDeviceList = null;
    private ArrayAdapter mArrayAdapter = null;
    private CameraCom mCameraView = null;


    public AcoholHandlerMsg(Activity activity) {

        Log.i(TAG, "AcoholHandlerMsg");

        mActivity = activity;

        //mMsgVersion = mActivity.findViewById(R.id.fw_version);
        mUsageCount = mActivity.findViewById(R.id.usge_count);
        mMsgText = mActivity.findViewById(R.id.test_msg);
        mArulValue = mActivity.findViewById(R.id.test_value);
        //mTestStartBtn = mActivity.findViewById(R.id.measure_button);
    }


    public void initDeviceAdapter(List<String> deviceList,
                                 ArrayAdapter adapter ) {
        Log.i(TAG, "initDeviceAdapter");

        mDeviceList = deviceList;
        mArrayAdapter = adapter;
    }


    public void setCameraView(CameraCom cameraCom) {

        Log.i(TAG, "setCameraView");

        mCameraView = cameraCom;
        mCameraView.initCameraCom(this);
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

                mMsgText.setText((String) value);

                break;

            case AcoholCmd.MSG_COMMAND_MSG_VERSION:
                value = ble.getString(HandlerUtil.INFO);
                value = String.format(mActivity.getString(R.string.alcohol_test_FwVersion), (String)value);

                //if(mMsgVersion != null) {
                //    mMsgVersion.setText((String) value);
                //}
                break;

            case AcoholCmd.MSG_COMMAND_TEST_WAITE:
                value = ble.getString(HandlerUtil.INFO);
                value = String.format(mActivity.getString(R.string.alcohol_test_reserve), (String)value);

                mMsgText.setText((String) value);

                mCameraView.cameraStart();

                break;

            case AcoholCmd.MSG_COMMAND_VALUE_TEST_AL:
                value = ble.getString(HandlerUtil.INFO);

                mArulValue.setText((String) value);

                mCameraView.captureImageRandom();

                break;

            case AcoholCmd.MSG_COMMAND_VALUE_TEST_START:

                mCameraView.cameraStart();

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_start));

                break;

            case AcoholCmd.MSG_COMMAND_VALUE_TEST_END:

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_end_ok));

                mCameraView.captureImageRandomEnd();

                break;

            case AcoholCmd.MSG_COMMAND_WARNNING_NO_BREATH:

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_start));

                break;

            case AcoholCmd.MSG_COMMAND_WARNNING_TEST_END:

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_end_no_breath));

                mCameraView.cameraStop();

                break;

            case AcoholCmd.MSG_COMMAND_MSG_TEST:

                mCameraView.cameraStart();

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_testing));

                break;

            case AcoholCmd.MSG_COMMAND_MSG_TEST_COUNT:

                value = ble.getString(HandlerUtil.INFO);
                value = String.format(mActivity.getString(R.string.alcohol_test_usage_count), (String)value);

                if(mUsageCount != null) {
                    mUsageCount.setText((String) value);
                }

                break;

            case AcoholCmd.MSG_COMMAND_WARNNING_TEST_START_AHL:

                //value = ble.getString(HandlerUtil.INFO);
                //value = String.format(mActivity.getString(R.string.test_count), (String)value);

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_warnning_start_alcohol));

                break;

            case AcoholCmd.MSG_COMMAND_VOLTAGE_OK:

                value = ble.getString(HandlerUtil.INFO);
                value = String.format(mActivity.getString(R.string.alcohol_test_voltage_ok), (String)value);

                //if(mMsgVersion != null) {
                //    mMsgVersion.append("\n" + value);
                //}
                break;


            case AcoholCmd.MSG_COMMAND_VOLTAGE_LOW:

                value = ble.getString(HandlerUtil.INFO);
                value = String.format(mActivity.getString(R.string.alcohol_test_voltage_low), (String)value);

                //if(mMsgVersion != null) {
                //    mMsgVersion.append("\n" + value);
                //}

                break;

            case AcoholCmd.MSG_COMMAND_WARNNING_BLOWING:

                //value = ble.getString(HandlerUtil.INFO);
                //value = String.format(mActivity.getString(R.string.test_before_voltage_low), (String)value);

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_warnning_blowing));

                break;


            case AcoholCmd.MSG_COMMAND_WARNNING_COUNT_OVER:

                //value = ble.getString(HandlerUtil.INFO);
                //value = String.format(mActivity.getString(R.string.test_before_voltage_low), (String)value);

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_warnning_usage_over));

                break;

            case AcoholCmd.MSG_COMMAND_WARNNING_IC_READ:

                //value = ble.getString(HandlerUtil.INFO);
                //value = String.format(mActivity.getString(R.string.test_before_voltage_low), (String)value);

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_warnning_ic_read));

                break;

            case AcoholCmd.MSG_COMMAND_TEST_END:

                //mEndTime = System.currentTimeMillis();
                //TextView testTime = (TextView) findViewById(R.id.testTime);

                //String time = ((mEndTime - mStartTime)/1000)+"";
                //time = String.format(mActivity.getString(R.string.test_time), (String)time);

                //testTime.setText(time);

                //mCameraView.captureImage(mCamerCallBack);

                break;


            case AcoholCmd.MSG_COMMAND_POWER_OFF:

                //mBltDeviceUtil.initBluetoothGatt();
                //mStartTime = 0;

//                if(mTestStartBtn != null) {
//                    mTestStartBtn.setEnabled(true);
//                }

                //mCameraView.cameraStop();

                if(mDeviceList != null) {
                    String flag = mDeviceList.get(0);
                    mDeviceList.clear();
                    mDeviceList.add(flag);
                    mArrayAdapter.notifyDataSetChanged();
                }

                // blt接続を切り
                ((AlcoholMeasureActivity)mActivity).getBltDeviceUtil().initBluetoothGatt();

                //mCameraView.cameraStop();

                break;


            case BltDeviceUtil.MSG_SCAN_START:
                //mStartTime = System.currentTimeMillis();

                //value = ble.getString(HandlerUtil.INFO);

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_scan_start));

                if(mDeviceList != null) {
                    String flag = mDeviceList.get(0);
                    mDeviceList.clear();
                    mDeviceList.add(flag);
                    mArrayAdapter.notifyDataSetChanged();
                }

                break;

            case BltDeviceUtil.MSG_DEVACE_SCAN_END:
                //mStartTime = System.currentTimeMillis();

                //value = ble.getString(HandlerUtil.INFO);

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_scan_end));

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

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_connect));

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

                mMsgText.setText(mActivity.getString(R.string.alcohol_test_connect_ng));

//                if(mTestStartBtn != null) {
//                    mTestStartBtn.setEnabled(true);
//                }

                break;


            //撮影した写真データ
            case CameraCom.MSG_IMAGE_CAPTURE:

                Log.d(TAG, "CameraCom.MSG_IMAGE_CAPTURE msg.what=" + msg.what);

                byte []image = ble.getByteArray(HandlerUtil.INFO);

                break;

            default:
                break;

        }
    }

}
