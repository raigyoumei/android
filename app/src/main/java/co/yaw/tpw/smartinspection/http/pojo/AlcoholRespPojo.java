package co.yaw.tpw.smartinspection.http.pojo;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leixiaoming on 2018/04/04.
 */

public class AlcoholRespPojo extends BasePojo {

    //Isaccess
    public int status;
    public String msg;

    private String serialNo;
    private String fwVersion;
    private String checkTime;
    private int testCount;
    private int usageCount;

    private double alcoholResult;
    private String image;


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

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo( String serialNo ) {
        this.serialNo = serialNo;
    }

    public String getFwVersion() {
        return fwVersion;
    }

    public void setFwVersion( String fwVersion ) {
        this.fwVersion = fwVersion;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount( int testCount ) {
        this.testCount = testCount;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount( int usageCount ) {
        this.usageCount = usageCount;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime( String checkTime ) {
        this.checkTime = checkTime;
    }

    public double getAlcoholResult() {
        return alcoholResult;
    }

    public void setAlcoholResult( double alcoholResult ) {
        this.alcoholResult = alcoholResult;
    }

    public String getImage() {
        return image;
    }

    public void setImage( String image ) {
        this.image = image;
    }

    public static List<Class<?>> getInternalClsTypes() {
        List<Class<?>> internalClsTypes = new ArrayList<Class<?>>();
        return internalClsTypes;
    }


    @Override
    public String toString() {
        return "AlcoholRespPojo{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", fwVersion='" + fwVersion + '\'' +
                ", checkTime='" + checkTime + '\'' +
                ", testCount=" + testCount +
                ", usageCount=" + usageCount +
                ", alcoholResult=" + alcoholResult +
                ", image='" + image + '\'' +
                '}';
    }
}
