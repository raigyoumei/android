package co.yaw.tpw.smartinspection.http.tasklistener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;

import com.yaw.tpw.smartinspection.R;

import org.json.JSONObject;

import co.yaw.tpw.smartinspection.LoginActivity;
import co.yaw.tpw.smartinspection.MenuActivity;
import co.yaw.tpw.smartinspection.dialog.DialogUtils;
import co.yaw.tpw.smartinspection.http.HTTP;
import co.yaw.tpw.smartinspection.http.pojo.LoginRespPojo;
import co.yaw.tpw.smartinspection.http.userInfo.EntryUtil;
import co.yaw.tpw.smartinspection.http.userInfo.UserEntry;
import co.yaw.tpw.smartinspection.http.util.Json2PojoUtil;
import co.yaw.tpw.smartinspection.http.util.TooltipUtil;


/**
 * Created by leixiaoming on 2018/04/03.
 */

public class LoginListener implements HTTP.AjaxListener {

    private final static String TAG = LoginListener.class.getSimpleName();

    private Activity mActivity = null;
    private Dialog mDialog = null;

    public LoginListener(Activity activity) {

        this.mActivity = activity;

        mDialog = DialogUtils.createLoadingDialog(activity, mActivity.getString(R.string.prcessing_server));
    }


    @Override
    public void doFinished(int err, String result) {

        DialogUtils.closeDialog(mDialog);

        Log.d(TAG, "LoginListener doFinished result="+result);

        if (result == null) {

//            if(err == ConstHttp.CONNECTION_ERROR){
//                TooltipUtil.showToast(mActivity, mActivity.getString(R.string.network_error));
//                Log.d(TAG, "result is NULL");
//            }

            Log.d(TAG, "doFinished result is null");

            Intent intent = new Intent(mActivity.getApplication() , MenuActivity.class);
            mActivity.startActivity(intent);

            return;
        }

        try {

            LoginRespPojo pojo = getRespPojo(result);
            if(pojo == null){

                TooltipUtil.showToast(mActivity, mActivity.getString(R.string.login_error));
                Log.d(TAG, "doFinished pojo is null");

                return;
            }

            String sessionId = pojo.getSessionID();

            if (sessionId != null && !sessionId.isEmpty()) {

                Intent intent = new Intent(mActivity.getApplication() , MenuActivity.class);
                mActivity.startActivity(intent);

                Log.d(TAG, "doFinished sucess");
            } else {

                if (!(mActivity instanceof LoginActivity)) {
                    Intent intent = new Intent(mActivity.getApplication(), LoginActivity.class);
                    mActivity.startActivity(intent);
                }

                TooltipUtil.showToast(mActivity, mActivity.getString(R.string.login_error));
                Log.d(TAG, "doFinished fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // 応答情報解析
    private LoginRespPojo getRespPojo(String result){

        LoginRespPojo pojo = null;

        try{

            JSONObject json = Json2PojoUtil.getJSONObject(result);

            pojo = (LoginRespPojo) Json2PojoUtil.fromJsonToBasePojo(json, LoginRespPojo.class);

            String sessionId = pojo.getSessionID();
            String userID = pojo.getUserID();
            String workerID = pojo.getWorkerID();
            String userName = pojo.getUserName();
            String workerName = pojo.getWorkerName();

            Log.d(TAG, pojo.toString());

            UserEntry su = EntryUtil.getEntry(mActivity);
            su.setSession(sessionId);
            su.setUserID(userID);
            su.setWorkerID(workerID);
            su.setUserName(userName);
            su.setWorkerName(workerName);

            EntryUtil.setEntry(mActivity, su);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        return pojo;
    }




}
