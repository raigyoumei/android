package co.yaw.tpw.smartinspection.bltUtil;


import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;


public class BltCntReceiverUtil extends BroadcastReceiver {

    private final static String TAG = BltCntReceiverUtil.class.getSimpleName();

    private String mPAIR_PWD = null;
    private HandlerUtil mHandlerUtil = null;


    public BltCntReceiverUtil(HandlerUtil handler, String pwd) {
        Log.i(TAG, "BltCntReceiverUtil");

        mHandlerUtil = handler;

        mPAIR_PWD = pwd;

    }


    @Override
    public void onReceive( Context context, Intent intent) {

        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String action = intent.getAction();

        if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {

            try {

                Log.d(TAG, "BluetoothDevice ACTION_PAIRING_REQUEST call");

                //BltBandUtil.setPairingConfirmation(device.getClass(), device, true);
                abortBroadcast();

                BltBandUtil.setPin(device.getClass(), device, mPAIR_PWD);
                BltBandUtil.createBond(device.getClass(), device);
                BltBandUtil.cancelPairingUserInput(device.getClass(), device);

                //mHandlerUtil.sendHandler(BltDeviceUtil.MSG_DEVACE_PAIR_END);

            } catch (Exception e) {
                Log.d(TAG, "Exception e="+e.toString());
                //e.printStackTrace();
            }
        } else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){

            Log.i(TAG, "BluetoothDevice BluetoothDevice action="+action);

            switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDING:
                    Log.i(TAG, "BluetoothDevice.BOND_BONDING");

                    mHandlerUtil.sendHandler(BltDeviceUtil.MSG_DEVACE_PAIR_START);

                    break;
                case BluetoothDevice.BOND_BONDED:
                    Log.i(TAG, "BluetoothDevice.BOND_BONDED");

                    //mHandlerUtil.sendHandler(BltDeviceUtil.MSG_DEVACE_PAIR_END);

                    break;
                case BluetoothDevice.BOND_NONE:

                    Log.i(TAG, "BluetoothDevice.BOND_NONE");

                    mHandlerUtil.sendHandler(BltDeviceUtil.MSG_DEVACE_PAIR_END);

//                    try {
//                        BltBandUtil.createBond(device.getClass(), device);
//                    }catch (Exception e) {
//                        Log.e(TAG, "Exception="+e.toString());
//                        e.printStackTrace();
//                    }
                    break;
                default:
                    break;
            }
        }
    }
}
