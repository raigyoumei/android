package co.yaw.tpw.smartinspection.bltUtil;


import com.yaw.tpw.smartinspection.BuildConfig;

/**
 * Created by leixiaoming on 2018/04/02.
 */

public class AppConfigUtil {

    public static String getServerUrl() {
        return BuildConfig.SERVER_URL;
    }

    public static String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getVersion() {
        String version = AppConfigUtil.getVersionName();
        version = version.substring(version.lastIndexOf(":")+1).trim();
        return version;
    }
}

