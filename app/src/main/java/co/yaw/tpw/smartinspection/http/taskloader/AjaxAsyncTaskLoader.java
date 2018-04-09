package co.yaw.tpw.smartinspection.http.taskloader;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;


public class AjaxAsyncTaskLoader extends BaseTaskLoader<String> {

    private final static String TAG = AjaxAsyncTaskLoader.class.getSimpleName();

    public AjaxAsyncTaskLoader(Context context, Bundle bundle, String pathUrl, HashMap<String, Object> params) {
        //super(context);
        super(context, bundle, pathUrl, params);
    }


    @Override
    public String loadInBackground() {
        try {
            Log.i(TAG, "start Ajax");

            return post(getPathUrl(), getParams());
        } catch (Exception e) {

            Log.e(TAG, "loadInBackground error="+e.toString());
            e.printStackTrace();

            // エラーの場合nullを返す
            return null;
        }
    }
}

