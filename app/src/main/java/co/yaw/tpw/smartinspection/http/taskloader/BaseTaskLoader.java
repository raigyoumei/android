package co.yaw.tpw.smartinspection.http.taskloader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;

import co.yaw.tpw.smartinspection.bltUtil.AppConfigUtil;
import co.yaw.tpw.smartinspection.http.pojo.LoginRespPojo;
import co.yaw.tpw.smartinspection.http.userInfo.EntryUtil;
import co.yaw.tpw.smartinspection.http.userInfo.UserEntry;
import co.yaw.tpw.smartinspection.http.util.ConstHttp;
import co.yaw.tpw.smartinspection.http.util.Json2PojoUtil;
import co.yaw.tpw.smartinspection.http.util.RespCheckUtil;


public abstract class BaseTaskLoader<T> extends AsyncTaskLoader<T> {

    private final static String TAG = BaseTaskLoader.class.getSimpleName();

    private T mResult = null;
    private boolean mIsStarted = false;

    private String mSession = null;
    private String mUserID = null;
    private String mWorkerID = null;
    private String mPathUrl = null;

    private HashMap<String, String> mParams = null;


    public BaseTaskLoader(Context context, Bundle bundle, String pathUrl, HashMap<String, String> params) {

        super(context);

        mSession = bundle.getString(EntryUtil.SESSION);
        mWorkerID = bundle.getString(EntryUtil.WORKERID);
        mUserID = bundle.getString(EntryUtil.USERID);

        mPathUrl = pathUrl;
        mParams = params;
    }


    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
            return;
        }
        if (!mIsStarted || takeContentChanged()) {
            forceLoad();
        }
    }


    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        mIsStarted = true;
    }


    @Override
    public void deliverResult(T data) {
        mResult = data;
        super.deliverResult(data);
    }


    public String getSession() {
        return mSession;
    }

    public void setSession(String session) {
        mSession = session;
    }

    public String getUserID() { return mUserID;}

    public String getWorkerID() {return mWorkerID;}

    public String getPathUrl() {
        return mPathUrl;
    }

    public HashMap<String, String> getParams() {
        return mParams;
    }


    public HttpURLConnection createHttpURLConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String cookieStr = "sessionID=" + getSession()
                + "; " + "workerID=" + getWorkerID()
                + "; " + "userID=" + getUserID();

        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty("Cookie", cookieStr);
        return connection;
    }



    protected String post(String pathUrl, HashMap<String, String> params) {

        String responseData = postBody(pathUrl, params);

        if (RespCheckUtil.isSessionTImeOut(responseData)) {
            reLogin(getContext());
            responseData = postBody(pathUrl, params);
        }

        return responseData;
    }


    private String postBody(String pathUrl, HashMap<String, String> params) {
        try {
            Log.i(TAG, "start http post");

            String urltmp = AppConfigUtil.getServerUrl() + pathUrl;
            URL url = new URL(urltmp);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Referer", url.toString());

            String cookieStr = "sessionID=" + getSession()
                    + "; " + "workerID=" + getWorkerID()
                    + "; " + "userID=" + getUserID();

            con.setRequestProperty("Cookie", cookieStr);
            con.setRequestMethod("POST");
            //con.setRequestProperty("Charset", "UTF-8");

            con.setRequestProperty("User-Agent", "smartinspection "+AppConfigUtil.getVersion());

            con.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            //con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            //con.addRequestProperty("X-Requested-With", "XMLHttpRequest");

            con.setInstanceFollowRedirects(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            if (params != null) {

                String parameter = "";
                ObjectMapper mapper = new ObjectMapper();
                parameter = mapper.writeValueAsString(params);

//                for (String key : params.keySet()) {
//                    Object v = params.get(key);
//                    if (v != null) {
//                        parameter += key + "=" + params.get(key) + "&";
//                    }
//                }

                Log.d(TAG, "postBody parameter="+parameter);

                PrintStream ps = new PrintStream(con.getOutputStream());
                ps.print(parameter);
                ps.close();
            }

            con.getResponseMessage();

            int statusCode = con.getResponseCode();

            String responseData = "";
            InputStream stream = con.getInputStream();
            StringBuffer sb = new StringBuffer();
            String line = "";

            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            stream.close();
            responseData = sb.toString();

            Log.d(TAG, "postBody response=" + responseData);
            con.disconnect();

            return responseData;

        } catch (Exception e) {

            Log.e(TAG,"Exception=" + e.toString());

            e.printStackTrace();

            // エラーの場合nullを返す
            return null;
        }
    }

    protected void reLogin(Context context) {

        UserEntry su = EntryUtil.getEntry(context);

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("workerID", su.getWorkerID());
        params.put("userID", su.getUserID());
        params.put("password", su.getPassword());

        String result = postBody(ConstHttp.LOGIN_PATH, params);

        if (result == null) {
            return;
        }

        try {

            JSONObject json = Json2PojoUtil.getJSONObject(result);

            LoginRespPojo pojo = (LoginRespPojo) Json2PojoUtil.fromJsonToBasePojo(json, LoginRespPojo.class);

            String sessionId = pojo.getSessionID();
            String userID = pojo.getUserID();
            String workerID = pojo.getWorkerID();
            String userName = pojo.getUserName();
            String workerName = pojo.getWorkerName();

            Log.d(TAG, "sessionId="+sessionId);
            Log.d(TAG, "userID="+userID);
            Log.d(TAG, "workerID="+workerID);
            Log.d(TAG, "userName="+userName);
            Log.d(TAG, "workerName="+workerName);

            su.setSession(sessionId);
            su.setUserID(userID);
            su.setWorkerID(workerID);
            su.setUserName(userName);

            mSession = sessionId;

            EntryUtil.setEntry(context, su);

        } catch (Exception e) {
            Log.e(TAG, "Exception="+e.toString());
            e.printStackTrace();
        }
    }
}
