package co.yaw.tpw.smartinspection.http.pojo;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by leixiaoming on 2018/04/04.
 */

public class LoginRespPojo extends BasePojo {

    private String sessionID;
    private String workerID;
    private String userID;

    private String userName;
    private String workerName;

    //Isaccess
    private boolean status;
    private String msg;


    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID( String sessionID ) {
        this.sessionID = sessionID;
    }

    public String getWorkerID() {
        return workerID;
    }

    public void setWorkerID( String workerID ) {
        this.workerID = workerID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID( String userID ) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName( String workerName ) {
        this.workerName = workerName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus( boolean status ) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg( String msg ) {
        this.msg = msg;
    }


    public static List<Class<?>> getInternalClsTypes() {
        List<Class<?>> internalClsTypes = new ArrayList<Class<?>>();
        return internalClsTypes;
    }


}
