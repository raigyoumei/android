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

        Intent intent = new Intent(mActivity.getApplication() , MenuActivity.class);
        mActivity.startActivity(intent);


//        if (result != null) {
//            Class pojoClsType = LoginResponsePojo.class;
//            try {
//                JSONObject json = Json2PojoUtil.getJSONObject(result);
//
//                LoginResponsePojo pojo = (LoginResponsePojo) Json2PojoUtil.fromJsonToBasePojo(json, pojoClsType);
//                String sessionId = pojo.getSession_id();
//                String userID = pojo.getUser_id();
//                String userName = pojo.getUser_name();
//
//                NBoxSUEntry su = NBoxUtil.getNNBoxSU(mActivity);
//                su.setSession(sessionId);
//                su.setUser(userID);
//                su.setUserName(userName);
//
//                NBoxUtil.setNBoxSU(mActivity, su);
//
//                if (sessionId != null && !sessionId.isEmpty()) {
//                    Intent intent = new Intent(mActivity.getApplication(), MainActivity.class);
//                    mActivity.startActivity(intent);
//                    Log.d(TAG, "login sucess");
//                } else {
//                    if (mActivity instanceof LoginActivity) {
//
//                    } else {
//                        Intent intent = new Intent(mActivity.getApplication(), LoginActivity.class);
//                        mActivity.startActivity(intent);
//                    }
//                    TooltipUtil.showToast(mActivity, mActivity.getString(R.string.login_error));
//                    Log.d(TAG, "login fail");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            Log.d(TAG, result.toString());
//        } else if(err == GIGAPOD.CONNECTION_ERROR){
//            TooltipUtil.showToast(mActivity, mActivity.getString(R.string.network_error));
//            Log.d(TAG, "result is NULL");
//        }
    }
}
