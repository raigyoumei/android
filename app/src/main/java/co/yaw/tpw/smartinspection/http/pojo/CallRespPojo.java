package co.yaw.tpw.smartinspection.http.pojo;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by leixiaoming on 2018/04/04.
 */

public class CallRespPojo extends BasePojo {

    //Isaccess
    public int status;
    public String msg;


    private String checkDate;
    private String checkTime;
    private int testCount;

    private int alcoholResult;
    private int healthSelfResult;
    private int vitalResult;
    private int vitalSelfResult;
    private int callType;
    private int admit;
    private String admitTime;


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

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate( String checkDate ) {
        this.checkDate = checkDate;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime( String checkTime ) {
        this.checkTime = checkTime;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount( int testCount ) {
        this.testCount = testCount;
    }

    public int getAlcoholResult() {
        return alcoholResult;
    }

    public void setAlcoholResult( int alcoholResult ) {
        this.alcoholResult = alcoholResult;
    }

    public int getHealthSelfResult() {
        return healthSelfResult;
    }

    public void setHealthSelfResult( int healthSelfResult ) {
        this.healthSelfResult = healthSelfResult;
    }

    public int getVitalResult() {
        return vitalResult;
    }

    public void setVitalResult( int vitalResult ) {
        this.vitalResult = vitalResult;
    }

    public int getVitalSelfResult() {
        return vitalSelfResult;
    }

    public void setVitalSelfResult( int vitalSelfResult ) {
        this.vitalSelfResult = vitalSelfResult;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType( int callType ) {
        this.callType = callType;
    }

    public int getAdmit() {
        return admit;
    }

    public void setAdmit( int admit ) {
        this.admit = admit;
    }

    public String getAdmitTime() {
        return admitTime;
    }

    public void setAdmitTime( String admitTime ) {
        this.admitTime = admitTime;
    }

    public static List<Class<?>> getInternalClsTypes() {
        List<Class<?>> internalClsTypes = new ArrayList<Class<?>>();
        return internalClsTypes;
    }


    @Override
    public String toString() {
        return "CallRespPojo{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", checkDate='" + checkDate + '\'' +
                ", checkTime='" + checkTime + '\'' +
                ", testCount=" + testCount +
                ", alcoholResult=" + alcoholResult +
                ", healthSelfResult=" + healthSelfResult +
                ", vitalResult=" + vitalResult +
                ", vitalSelfResult=" + vitalSelfResult +
                ", callType=" + callType +
                ", admit=" + admit +
                ", admitTime='" + admitTime + '\'' +
                '}';
    }


}
