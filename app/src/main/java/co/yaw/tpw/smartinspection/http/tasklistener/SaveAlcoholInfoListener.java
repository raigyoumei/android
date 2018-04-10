package co.yaw.tpw.smartinspection.http.tasklistener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.yaw.tpw.smartinspection.R;

import co.yaw.tpw.smartinspection.AlcoholMeasureActivity;
import co.yaw.tpw.smartinspection.bltUtil.ConstUtil;
import co.yaw.tpw.smartinspection.dialog.DialogUtils;
import co.yaw.tpw.smartinspection.http.HTTP;
import co.yaw.tpw.smartinspection.http.pojo.AlcoholRespPojo;
import co.yaw.tpw.smartinspection.http.pojo.BasePojo;
import co.yaw.tpw.smartinspection.http.util.RespCheckUtil;
import co.yaw.tpw.smartinspection.http.util.TooltipUtil;


/**
 * Created by leixiaoming on 2018/04/03.
 */

public class SaveAlcoholInfoListener implements HTTP.AjaxListener {

    private final static String TAG = SaveAlcoholInfoListener.class.getSimpleName();

    private Activity mActivity = null;
    private Dialog mDialog = null;
    private int mCallType = 0;

    public SaveAlcoholInfoListener( Activity activity, int callType) {

        this.mActivity = activity;
        mCallType = callType;

        mDialog = DialogUtils.createLoadingDialog(activity, mActivity.getString(R.string.alcohol_measure_save));
    }


    @Override
    public void doFinished(int err, String result) {

        DialogUtils.closeDialog(mDialog);

        Log.d(TAG, "SaveAlcoholInfoListener doFinished result="+result);

        if (result == null) {

//            if(err == ConstHttp.CONNECTION_ERROR){
//                TooltipUtil.showToast(mActivity, mActivity.getString(R.string.network_error));
//                Log.d(TAG, "result is NULL");
//            }

            Log.d(TAG, "doFinished result is null");

            return;
        }

        try {

            BasePojo pojo = RespCheckUtil.getBasePojo(result);
            if((pojo == null) || (pojo.getStatus() != 0)){

                TooltipUtil.showToast(mActivity, mActivity.getString(R.string.network_error));
                Log.d(TAG, "doFinished pojo is null");

                return;
            }

            Log.d(TAG, pojo.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
