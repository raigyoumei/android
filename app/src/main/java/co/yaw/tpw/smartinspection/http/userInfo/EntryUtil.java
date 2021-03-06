package co.yaw.tpw.smartinspection.http.userInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;


/**
 * Created by leixiaoming on 2018/04/03.
 */

public class EntryUtil {

    private final static String TAG = EntryUtil.class.getSimpleName();

    private final static String SHAREDNAME = "smtSptSession";

    public final static String SESSION = "session";
    public final static String WORKERID = "workerID";
    public final static String USERID = "userID";
    public final static String PASSWORD = "password";
    public final static String USERNAME = "userName";
    public final static String WORKERNAME = "workerName";

    public static void setEntry(Context context, UserEntry su) {
        SharedPreferences prefs = context.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if(su == null) {
            editor.putString(SESSION, null);
            editor.putString(USERID, null);
            editor.putString(PASSWORD, null);
            editor.putString(WORKERID, null);
            editor.putString(USERNAME, null);
            editor.putString(WORKERNAME, null);
        } else {

            if(su.getSession() != null) {
                editor.putString(SESSION, su.getSession());
            }

            if(su.getUserID() != null) {
                editor.putString(USERID, su.getUserID());
            }

            if(su.getPassword() != null) {
                editor.putString(PASSWORD, su.getPassword());
            }

            if(su.getWorkerID() != null) {
                editor.putString(WORKERID, su.getWorkerID());
            }

            if(su.getUserName() != null) {
                editor.putString(USERNAME, su.getUserName());
            }

            if(su.getWorkerName() != null) {
                editor.putString(WORKERNAME, su.getWorkerName());
            }

        }
        editor.commit();
    }


    public static boolean EntryIsEmpty(Context context) {
        UserEntry su = getEntry(context);

        if (su != null) {
            String session = su.getSession();
            String user = su.getUserID();
            if (session != null && !session.equals("") && user != null && !user.equals("")) {
                return false;
            }
        }
        return true;
    }


    public static UserEntry getEntry(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHAREDNAME, Context.MODE_PRIVATE);
        String Session = prefs.getString(SESSION, "");
        String UserID = prefs.getString(USERID, "");
        String password = prefs.getString(PASSWORD, "");
        String workerID = prefs.getString(WORKERID, "");
        String userName = prefs.getString(USERNAME, "");
        String workerName = prefs.getString(WORKERNAME, "");

        UserEntry su = new UserEntry();
        su.setSession(Session);
        su.setUserID(UserID);
        su.setPassword(password);
        su.setWorkerID(workerID);
        su.setUserName(userName);
        su.setWorkerName(workerName);

        return su;
    }


    public static Bundle getBundle(Context context) {

        Bundle bundle = new Bundle();

        UserEntry su = getEntry(context);
        bundle.putString(SESSION, su.getSession());
        bundle.putString(USERID, su.getUserID());
        bundle.putString(PASSWORD, su.getPassword());
        bundle.putString(WORKERID, su.getWorkerID());
        bundle.putString(USERNAME, su.getUserName());
        bundle.putString(WORKERNAME, su.getWorkerName());

        return bundle;
    }


    public static boolean IsEntryNull(Bundle bundle) {
        String session = bundle.getString(SESSION);
        String user = bundle.getString(USERID);
        if(session == null || user == null || session.equals("") || user.equals("")) {
            return true;
        }
        return false;
    }




}
