package co.yaw.tpw.smartinspection.http.taskloader;


import android.content.Context;
import android.os.Bundle;

import java.util.HashMap;

import co.yaw.tpw.smartinspection.http.util.RespCheckUtil;

/**
 * Created by leixiaoming on 2018/04/03.
 */

public class SaveAlcoholInfoTaskLoader extends BaseTaskLoader<String> {

    private final static String TAG = SaveAlcoholInfoTaskLoader.class.getSimpleName();
    private String mPathUrl = null;
    HashMap<String, Object> mParams = null;


    public SaveAlcoholInfoTaskLoader(Context context, Bundle bundle, String pathUrl, HashMap<String, Object> params) {
        super(context, bundle, pathUrl, params);

        mPathUrl = pathUrl;
        mParams = params;

    }

    @Override
    public String loadInBackground() {

        String responseData = okHttpMtil(mPathUrl, mParams);

        if (RespCheckUtil.isSessionTImeOut(responseData)) {
            reLogin(getContext());
            responseData = okHttpMtil(mPathUrl, mParams);
        }

        return responseData;
    }



}
