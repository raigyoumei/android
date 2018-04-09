package co.yaw.tpw.smartinspection.http.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leixiaoming on 2018/04/04.
 */

public class BasePojo {

    //Isaccess
    public int status;
    public String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus( int status ) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg( String msg ) {
        this.msg = msg;
    }


    @Override
    public String toString() {
        return "BasePojo{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }


    public static List<Class<?>> getInternalClsTypes() {
        List<Class<?>> internalClsTypes = new ArrayList<Class<?>>();
        return internalClsTypes;
    }

}
