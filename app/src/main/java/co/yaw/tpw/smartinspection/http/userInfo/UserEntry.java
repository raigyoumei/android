package co.yaw.tpw.smartinspection.http.userInfo;

/**
 * Created by leixiaoming on 2018/04/03.
 */

public class UserEntry {
    private String session;
    private String userID;
    private String workerID;
    private String password;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWorkerID() {
        return workerID;
    }

    public void setWorkerID(String workerID) {
        this.workerID = workerID;
    }

}
