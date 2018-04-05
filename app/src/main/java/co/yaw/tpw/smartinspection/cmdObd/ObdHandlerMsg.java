package co.yaw.tpw.smartinspection.cmdObd;


import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;

import java.util.List;


import co.yaw.tpw.smartinspection.bltUtil.BltDeviceUtil;
import co.yaw.tpw.smartinspection.bltUtil.HandlerUtil;

public class ObdHandlerMsg extends HandlerUtil {

    private final static String TAG = ObdHandlerMsg.class.getSimpleName();

    private TextView mMsgTextView = null;

    private Activity mActivity = null;
    private List<String> mDeviceList = null;
    private ArrayAdapter mArrayAdapter = null;


    public ObdHandlerMsg(Activity activity) {

        Log.d(TAG, "ObdHandlerMsg");

        mActivity = activity;

        mMsgTextView = mActivity.findViewById(R.id.test_msg);

    }


    public void initDeviceAdapter(List<String> deviceList, ArrayAdapter adapter ) {

        Log.d(TAG, "initDeviceAdapter");

        mDeviceList = deviceList;
        mArrayAdapter = adapter;
    }


    @Override
    public void handleMessage(Message msg){

        Bundle ble = msg.getData();
        Object value = null;

        Log.d(TAG, "handleMessage msg.what=" + msg.what);

        switch(msg.what){
            case OdbCmd.MSG_COMMAND_STR:
                value = ble.getString(HandlerUtil.INFO);
                //CommandInfoStr((String)value);

                mMsgTextView.setText((String)value);

                break;

            case BltDeviceUtil.MSG_SCAN_START:
                //mStartTime = System.currentTimeMillis();

                //value = ble.getString(HandlerUtil.INFO);

                mMsgTextView.setText(mActivity.getString(R.string.car_test_scan_start));

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

                mMsgTextView.setText(mActivity.getString(R.string.car_test_scan_end));

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

                mMsgTextView.setText(mActivity.getString(R.string.car_test_connect));

                break;

            case BltDeviceUtil.MSG_DEVACE_CONNECT:
                //value = ble.getString(HandlerUtil.INFO);
                //mMsgText.setText((String)value);

                //mDeviceList.add((String)value);
                //mArrayAdapter.notifyDataSetChanged();

//                if(mTestStartBtn != null) {
//                    mTestStartBtn.setEnabled(false);
//                }

                mMsgTextView.setText(mActivity.getString(R.string.car_test_start));

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

                mMsgTextView.setText(mActivity.getString(R.string.car_test_connect_ng));

//                if(mTestStartBtn != null) {
//                    mTestStartBtn.setEnabled(true);
//                }

                break;

            default:
                break;

        }
    }

}
