package co.yaw.tpw.smartinspection.http.tasklistener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.yaw.tpw.smartinspection.R;

import co.yaw.tpw.smartinspection.AlcoholMeasureActivity;
import co.yaw.tpw.smartinspection.CallMenuActivity;
import co.yaw.tpw.smartinspection.bltUtil.ConstUtil;
import co.yaw.tpw.smartinspection.dialog.DialogUtils;
import co.yaw.tpw.smartinspection.http.HTTP;
import co.yaw.tpw.smartinspection.http.pojo.AlcoholRespPojo;
import co.yaw.tpw.smartinspection.http.pojo.CallRespPojo;
import co.yaw.tpw.smartinspection.http.util.RespCheckUtil;
import co.yaw.tpw.smartinspection.http.util.TooltipUtil;


/**
 * Created by leixiaoming on 2018/04/03.
 */

public class GetAlcoholInfoListener implements HTTP.AjaxListener {

    private final static String TAG = GetAlcoholInfoListener.class.getSimpleName();

    private Activity mActivity = null;
    private Dialog mDialog = null;
    private String mCallType = null;

    public GetAlcoholInfoListener( Activity activity, String callType) {

        this.mActivity = activity;
        mCallType = callType;

        mDialog = DialogUtils.createLoadingDialog(activity, mActivity.getString(R.string.prcessing_server));
    }


    @Override
    public void doFinished(int err, String result) {

        DialogUtils.closeDialog(mDialog);

        Log.d(TAG, "GetAlcoholInfoListener doFinished result="+result);

        if (result == null) {

//            if(err == ConstHttp.CONNECTION_ERROR){
//                TooltipUtil.showToast(mActivity, mActivity.getString(R.string.network_error));
//                Log.d(TAG, "result is NULL");
//            }

            Log.d(TAG, "doFinished result is null");

            Intent intent = new Intent(mActivity.getApplication(), AlcoholMeasureActivity.class);
            Bundle b = new Bundle();
            b.putString(ConstUtil.FORWARD_KEY, mCallType);
            intent.putExtras(b);
            mActivity.startActivity(intent);

            return;
        }


        try {

            AlcoholRespPojo pojo = RespCheckUtil.getAlcoholRespPojo(result);
            if(pojo == null){

                TooltipUtil.showToast(mActivity, mActivity.getString(R.string.network_error));
                Log.d(TAG, "doFinished pojo is null");

                return;
            }

            Log.d(TAG, pojo.toString());

            if (pojo.getStatus() == 0) {

                Intent intent = new Intent(mActivity.getApplication(), AlcoholMeasureActivity.class);
                Bundle b = new Bundle();
                b.putString(ConstUtil.FORWARD_KEY, mCallType);
                b.putString(ConstUtil.RESP_DATA_KEY, result);

                intent.putExtras(b);
                mActivity.startActivity(intent);

                Log.d(TAG, "doFinished sucess");

            }else{

                TooltipUtil.showToast(mActivity, mActivity.getString(R.string.network_error));
                Log.d(TAG, "doFinished fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
