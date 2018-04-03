package co.yaw.tpw.smartinspection.http;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.HashMap;

import co.yaw.tpw.smartinspection.http.tasklistener.LoginListener;
import co.yaw.tpw.smartinspection.http.taskloader.AjaxAsyncTaskLoader;


/**
 * Created by t.wagatsuma on 2017/06/01.
 * via:http://qiita.com/ux_design_tokyo/items/d07bc5953b7611ce7523
 */
public class HTTP {
    // StatusCode
    public final static int CONNECTION_SUCCESS = 1;
    public final static int CONNECTION_ERROR = CONNECTION_SUCCESS + 1;
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

        String reqPath = "/pioneer/LoginTest";

        ajax(reqPath, params, new LoginListener(mActivity));
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
                    listener.doFinished(CONNECTION_SUCCESS, result);
                } else {
                    listener.doFinished(CONNECTION_ERROR, null);
                }
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {
                Log.i(TAG, "onLoaderReset");
                listener.doFinished(CONNECTION_SUCCESS, null);
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




    public interface AjaxListener {
        public void doFinished(final int err, final String result);
    }


}
