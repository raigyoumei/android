package co.yaw.tpw.smartinspection.cmdVital;


import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.BodyDataType;
import com.yaw.tpw.smartinspection.R;

import java.util.List;

import co.yaw.tpw.smartinspection.bltUtil.BltDeviceUtil;
import co.yaw.tpw.smartinspection.bltUtil.HandlerUtil;


public class VitalHandlerMsg extends HandlerUtil {

    private final static String TAG = VitalHandlerMsg.class.getSimpleName();

    private TextView mMsgText = null;
    private Button mTestStartBtn = null;

    private Activity mActivity = null;
    private List<String> mDeviceList = null;
    private ArrayAdapter mArrayAdapter = null;

    private int mPoorSignal = 0;
    private int  mPressCount = 0;

    private EcgProcess mEcgProcess = null;


    public VitalHandlerMsg(Activity activity) {

        Log.i(TAG, "VitalHandlerMsg");

        mActivity = activity;


        mMsgText = mActivity.findViewById(R.id.test_msg);

        mTestStartBtn = mActivity.findViewById(R.id.measure_button);

    }

    public void setEcgProcess(EcgProcess process) {
        mEcgProcess = process;
    }


    public void initDeviceAdapter(List<String> deviceList, ArrayAdapter adapter ) {
        Log.i(TAG, "initDeviceAdapter");

        mDeviceList = deviceList;
        mArrayAdapter = adapter;
    }


    @Override
    public void handleMessage(Message msg){

        Bundle ble = msg.getData();

        Object value = null;
        Button startBtn = mTestStartBtn;

        Log.d(TAG, "mHandler msg.what="+msg.what);

        switch(msg.what){

            case BltDeviceUtil.MSG_SCAN_START:

                mMsgText.setText(mActivity.getString(R.string.vital_test_msg_scan_start));

                if(mDeviceList != null) {
                    String flag = mDeviceList.get(0);
                    mDeviceList.clear();
                    mDeviceList.add(flag);
                    mArrayAdapter.notifyDataSetChanged();
                }

                break;

            case BltDeviceUtil.MSG_DEVACE_SCAN_END:

                if(mTestStartBtn.isEnabled()) {
                    mMsgText.setText(mActivity.getString(R.string.vital_test_msg_scan_end));
                }

                break;

            case BltDeviceUtil.MSG_DEVACE_FIND:
                value = ble.getString(HandlerUtil.INFO);

                mDeviceList.add((String)value);
                mArrayAdapter.notifyDataSetChanged();

                break;

            case BltDeviceUtil.MSG_DEVACE_PAIR_START:

                mMsgText.setText(mActivity.getString(R.string.vital_test_msg_blt_pair));
                startBtn.setEnabled(false);

                break;

            case BltDeviceUtil.MSG_DEVACE_PAIR_END:

                mMsgText.setText(mActivity.getString(R.string.vital_test_msg_blt_pair_end));

                startBtn.setEnabled(true);

                break;

            case EcgConnect.MSG_UPDATE_STATE:

                value = ble.getInt(HandlerUtil.INFO);
                connectState((int)value);

                Log.d(TAG, "MSG_UPDATE_STATE="+(int)value);

                break;

            case EcgConnect.MSG_UPDATE_BAD_PACKET:

                Log.d(TAG, "MSG_UPDATE_BAD_PACKET");

                //mMsgText.setText("MSG_UPDATE_BAD_PACKET");

                break;

            case EcgConnect.MSG_UPDATE_BAD_RECODE:

                Log.d(TAG, "MSG_UPDATE_BAD_RECODE");

                //mMsgText.setText("MSG_UPDATE_BAD_RECODE");

                break;


            case BodyDataType.CODE_RAW:

                Log.d(TAG, "mPoorSignal:" + mPoorSignal);

                if(mPoorSignal == 0){
                    return;
                }

                value = ble.getInt(HandlerUtil.INFO);

                mEcgProcess.requestECGAnalysis((int)value, mPoorSignal);
                mEcgProcess.getEcgConnect().outPutLogData((int)value);

                break;

            case BodyDataType.CODE_POOR_SIGNAL:

                value = ble.getInt(HandlerUtil.INFO);
                mPoorSignal = (int)value;

                Log.d(TAG, "CODE_POOR_SIGNAL:" + mPoorSignal);

                if(mPoorSignal != 0 && mPoorSignal != 200){
                    Log.d(TAG, "mPoorSignal:" + mPoorSignal);
                }

                if(mPressCount > 1){
                    mEcgProcess.getEcgConnect().stopTgStreamReader();
                }

                if(mPoorSignal > 0) {
                    mPressCount = 0;
                }else{
                    mPressCount++;
                }

                if(mPressCount == 0){
                    if(mPoorSignal == 0){
                        mEcgProcess.getEcgConnect().stopTgStreamReader();
                    }
                }

                break;

            case BodyDataType.CODE_HEATRATE:

                value = ble.getInt(HandlerUtil.INFO);
                Log.d(TAG, "CODE_HEATRATE="+ value);

                break;

            default:

                Log.d(TAG, "other msg.what="+ msg.what);
                //showToast("other msg.what="+msg.what, Toast.LENGTH_SHORT);

                break;

        }
    }


    private void connectState(int status){

        Log.d(TAG,"connectState=" + status);

        switch (status) {
            case ConnectionStates.STATE_CONNECTED:

                //sensor.start();
                //showToast("Connected", Toast.LENGTH_SHORT);
                mPoorSignal = 0;
                mPressCount = 0;

                //mMsgText.setText(mActivity.getString(R.string.starting));

                mTestStartBtn.setEnabled(false);

                break;
            case ConnectionStates.STATE_WORKING:

                mMsgText.setText(mActivity.getString(R.string.vital_test_proc));
                mTestStartBtn.setEnabled(false);

                break;
            case ConnectionStates.STATE_GET_DATA_TIME_OUT:

                //mMsgText.setText("STATE_GET_DATA_TIME_OUT");

                if(!mTestStartBtn.isEnabled()) {
                    mMsgText.setText(mActivity.getString(R.string.vital_test_end_ng));
                    mTestStartBtn.setEnabled(true);
                }

                break;
            case ConnectionStates.STATE_STOPPED:

                //mMsgText.setText("STATE_STOPPED");

                //startBtn.setEnabled(true);

                //showToast("Stopped", Toast.LENGTH_SHORT);

                break;
            case ConnectionStates.STATE_DISCONNECTED:
                //showToast("STATE_DISCONNECTED", Toast.LENGTH_SHORT);
                //mMsgText.setText("STATE_DISCONNECTED");

                if(mTestStartBtn.isEnabled()){
                    break;
                }

                //mMsgText.setText(mActivity.getString(R.string.test_end));
                if(mPressCount > 1){
                    mMsgText.setText(mActivity.getString(R.string.vital_test_end_ng));
                }else {
                    mMsgText.setText(mActivity.getString(R.string.vital_test_end_ok));
                }

                mTestStartBtn.setEnabled(true);

                mEcgProcess.getVitalTestValue();


                break;
            case ConnectionStates.STATE_ERROR:

                //mMsgText.setText("STATE_ERROR");
                //showToast("STATE_ERROR", Toast.LENGTH_SHORT);

                //mMsgText.setText("STATE_ERROR");

                //startBtn.setEnabled(true);

                break;
            case ConnectionStates.STATE_FAILED:

                if(!mTestStartBtn.isEnabled()) {
                    mMsgText.setText(mActivity.getText(R.string.vital_test_end_ng));
                    mTestStartBtn.setEnabled(true);
                }

                //showToast("Connect failed!", Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }

    }


}
