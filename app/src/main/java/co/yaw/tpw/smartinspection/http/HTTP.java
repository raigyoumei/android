package co.yaw.tpw.smartinspection.http;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.HashMap;

import co.yaw.tpw.smartinspection.bltUtil.DateUtil;
import co.yaw.tpw.smartinspection.http.tasklistener.GetAlcoholInfoListener;
import co.yaw.tpw.smartinspection.http.tasklistener.GetCallInfoListener;
import co.yaw.tpw.smartinspection.http.tasklistener.LoginListener;
import co.yaw.tpw.smartinspection.http.taskloader.AjaxAsyncTaskLoader;
import co.yaw.tpw.smartinspection.http.taskloader.BaseTaskLoader;
import co.yaw.tpw.smartinspection.http.userInfo.EntryUtil;
import co.yaw.tpw.smartinspection.http.userInfo.UserEntry;
import co.yaw.tpw.smartinspection.http.util.ConstHttp;


/**
 * Created by t.wagatsuma on 2017/06/01.
 * via:http://qiita.com/ux_design_tokyo/items/d07bc5953b7611ce7523
 */
public class HTTP {

    private final static String TAG = HTTP.class.getSimpleName();

    private static int mNotificationNumber = 0; //通知領域の識別番号
    private int mTaskLoaderId; //TaskLoaderの識別番号

    private Activity mActivity;
    private LoaderManager mLoaderManager;
    private AsyncTaskLoader<?> mLoader;
    private Bundle mBundle;

    private HTTP() {
    }

    public HTTP(Activity activity, Bundle bundle) {
        Log.i(TAG, "HTTP");
        this.mBundle = bundle;
        this.mActivity = activity;
        this.mLoaderManager = ((AppCompatActivity) activity).getSupportLoaderManager();
        this.mTaskLoaderId = hashCode();
    }

    public static int createNotificationNumber() {
        mNotificationNumber++;
        return mNotificationNumber;
    }


    public void doLogin(String workerID, String userID, String password) {

        Log.d(TAG, "doLogin");

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("workerID", workerID);
        params.put("userID", userID);
        params.put("password", password);

        String reqPath = ConstHttp.LOGIN_PATH;

        ajax(reqPath, params, new LoginListener(mActivity));
    }


    public void getCallInfo(String checkType) {

        Log.d(TAG, "getCallInfo");

        String reqPath = ConstHttp.GET_CALL_INFO_PATH;
        HashMap<String, String> params = getComReqParm(checkType);

        ajax(reqPath, params, new GetCallInfoListener(mActivity,checkType));
    }



    public void getaAlcohol(String checkType) {

        Log.d(TAG, "getaAlcohol");

        String reqPath = ConstHttp.GET_ALCOHOL_INFO_PATH;
        HashMap<String, String> params = getComReqParm(checkType);

        ajax(reqPath, params, new GetAlcoholInfoListener(mActivity,checkType));
    }




    private void ajax(final String pathUrl, final HashMap<String, String> params, final AjaxListener listener) {

        Log.w(TAG, "ajax");

        mLoaderManager.restartLoader(mTaskLoaderId, null, new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int i, Bundle bundle) {
                Log.i(TAG, "onCreateLoader");
                return getAjaxAsyncTaskLoader(mActivity, mBundle, pathUrl, params);
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String result) {
                Log.i(TAG, "ajax onLoadFinished: path=" + pathUrl + "/result=" + result);
                if (result != null) {
                    listener.doFinished(ConstHttp.CONNECTION_SUCCESS, result);
                } else {
                    listener.doFinished(ConstHttp.CONNECTION_ERROR, null);
                }
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {
                Log.i(TAG, "onLoaderReset");
                listener.doFinished(ConstHttp.CONNECTION_SUCCESS, null);
            }
        });
    }



    private AjaxAsyncTaskLoader getAjaxAsyncTaskLoader( Activity activity,
                                                        Bundle bundle,
                                                        String pathUrl,
                                                        HashMap<String, String> params) {

        AjaxAsyncTaskLoader loader = new AjaxAsyncTaskLoader(activity, bundle, pathUrl, params);
        loader.forceLoad();
        this.mLoader = loader;
        return loader;
    }



    private HashMap<String, String> getComReqParm( String checkType){

        UserEntry su = EntryUtil.getEntry(mActivity);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("workerID", su.getWorkerID());
        params.put("userID", su.getUserID());

        String date = DateUtil.getCustomYMD(DateUtil.Y_M_D);
        String time = DateUtil.getCustomYMD(DateUtil.H_M_S);

        params.put("date", date);
        params.put("time", time);

        if(checkType != null) {
            params.put("checkType", checkType);
        }

        return params;

    }




    public interface AjaxListener {
        public void doFinished(final int err, final String result);
    }


}
