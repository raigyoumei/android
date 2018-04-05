package co.yaw.tpw.smartinspection.cmdObd;


import android.os.Handler;
import android.util.Log;

import co.yaw.tpw.smartinspection.bltUtil.BltComCmd;
import co.yaw.tpw.smartinspection.bltUtil.HandlerUtil;
import co.yaw.tpw.smartinspection.bltUtil.HexUtil;
import co.yaw.tpw.smartinspection.cmdAlcohol.AcoholCmd;


public class OdbCmd extends BltComCmd {

    private final static String TAG = AcoholCmd.class.getSimpleName();

    // Stops scanning after 5 seconds.
    public static final int MSG_COMMAND_STR = 101;

    private HandlerUtil mHandlerUtil = null;


    public OdbCmd( HandlerUtil handler) {

        Log.d(TAG, "OdbCmd");
        mHandlerUtil = handler;
    }

    @Override
    public void cmdProc( byte[] value) {

        String hexStr = HexUtil.formatHexString(value);

        sendMassage(MSG_COMMAND_STR, hexStr);

    }




    public void sendMassage(int type, String msg) {

        if(msg == null) {
            mHandlerUtil.sendHandler(type);
        }else{
            mHandlerUtil.sendHandler(type, msg);
        }
    }

}
