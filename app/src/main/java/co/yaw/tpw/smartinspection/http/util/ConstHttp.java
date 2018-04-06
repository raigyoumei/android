package co.yaw.tpw.smartinspection.http.util;


/**
 * Created by leixiaoming on 2018/04/04.
 */

public class ConstHttp {

    // StatusCode
    public final static int CONNECTION_SUCCESS = 1;
    public final static int CONNECTION_ERROR = CONNECTION_SUCCESS + 1;

    //request path
    // ログイン認証
    public final static String LOGIN_PATH = "/pioneer/LoginTest";

    // 乗務点呼結果取得
    public final static String GET_CALL_INFO_PATH = "/pioneer/getcallinfo";


    // HTTP response
    public final static int RESP_STATUS_OK = 0;
    public final static int RESP_STATUS_TIMEOUT = RESP_STATUS_OK + 1;
    public final static int RESP_STATUS_NG = RESP_STATUS_TIMEOUT + 1;

}
