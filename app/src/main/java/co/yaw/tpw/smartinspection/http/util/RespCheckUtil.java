package co.yaw.tpw.smartinspection.http.util;

import android.util.Log;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.yaw.tpw.smartinspection.http.pojo.CallRespPojo;

/**
 * Created by leixiaoming on 2018/04/04.
 */

public class RespCheckUtil {

    private final static String anyChar = "[\\s\\S]*";
    public final static String NEED_RE_LOGIN = "NEED_RE_LOGIN";

    private static boolean isMatch(String regEx, String response) {
        Pattern pattern = Pattern.compile(regEx);
        if(response != null) {
            Matcher matcher = pattern.matcher(response);
            return matcher.matches();
        }
        return false;
    }

    public static boolean isSessionTImeOut(String response) {
        String regEx = anyChar + "success" + anyChar + "false" + anyChar + "authentication_error" + anyChar + "true" + anyChar;
        return isMatch(regEx, response);
    }

    public static boolean isBinary(String response) {
        String regEx = anyChar + "application/octet-stream" + anyChar;
        return isMatch(regEx, response);
    }

    public static boolean isJson(String response) {
        String regEx = anyChar + "application/json" + anyChar;
        return isMatch(regEx, response);
    }

    public static boolean isNeedRelogin(String result) {
        if (result != null && RespCheckUtil.NEED_RE_LOGIN.equals(result)) {
            return true;
        }
        return false;
    }




    // 応答情報解析
    public static CallRespPojo getCallRespPojo(String result){

        CallRespPojo pojo = null;

        if(result == null){
            return pojo;
        }

        try{

            JSONObject json = Json2PojoUtil.getJSONObject(result);

            pojo = (CallRespPojo) Json2PojoUtil.fromJsonToBasePojo(json, CallRespPojo.class);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        return pojo;
    }




}
