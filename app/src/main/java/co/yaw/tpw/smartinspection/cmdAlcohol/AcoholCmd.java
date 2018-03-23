package co.yaw.tpw.smartinspection.cmdAlcohol;


import android.os.Handler;
import android.util.Log;

import co.yaw.tpw.smartinspection.bltUtil.BltComCmd;
import co.yaw.tpw.smartinspection.bltUtil.HandlerUtil;
import co.yaw.tpw.smartinspection.bltUtil.HexUtil;


public class AcoholCmd extends BltComCmd {

    private final static String TAG = AcoholCmd.class.getSimpleName();

    // Stops scanning after 10 seconds.
    public static final int MSG_COMMAND_STR = 101;
    public static final int MSG_MSG_INF = 102;
    public static final int MSG_COMMAND_MSG_VERSION = 103;

    public static final int MSG_COMMAND_TEST_WAITE = 104;
    public static final int MSG_COMMAND_WARNNING_START_TEST_PRESS = 105;
    public static final int MSG_COMMAND_VALUE_TEST_AL = 106;
    public static final int MSG_COMMAND_VALUE_TEST_START = 107;
    public static final int MSG_COMMAND_VALUE_TEST_END = 108;

    public static final int MSG_COMMAND_WARNNING_TEST_END = 109;

    public static final int MSG_COMMAND_WARNNING_NO_BREATH = 110;

    public static final int MSG_COMMAND_MSG_TEST = 111;

    public static final int MSG_COMMAND_MSG_TEST_COUNT = 112;

    public static final int MSG_COMMAND_WARNNING_TEST_START_AHL = 113;

    public static final int MSG_COMMAND_VOLTAGE_OK = 114;
    public static final int MSG_COMMAND_VOLTAGE_LOW = 115;

    public static final int MSG_COMMAND_WARNNING_COUNT_OVER = 116;
    public static final int MSG_COMMAND_WARNNING_BLOWING = 117;
    public static final int MSG_COMMAND_WARNNING_IC_READ = 118;

    public static final int MSG_COMMAND_TEST_END = 119;
    public static final int MSG_COMMAND_TEST_STANDARD = 120;

    public static final int MSG_COMMAND_POWER_OFF = 121;

    private HandlerUtil mHandlerUtil = null;
    private boolean mHowWarn = false;

    private int mAltTestSTLow = 15;
    private int mAltTestSTHigh = 25;


    public AcoholCmd( HandlerUtil handler) {
        Log.d(TAG, "BaseAcoholCmd");

        mHandlerUtil = handler;

    }

    @Override
    public void cmdProc( byte[] value) {

        String hexStr = HexUtil.formatHexString(value);

        mHandlerUtil.sendHandler(MSG_COMMAND_STR, hexStr);

        //
        String cmd = hexStr.substring(0, 6).toUpperCase();
        String data = hexStr.substring(6, hexStr.length() -2);

        Log.d(TAG, "cmd "+ cmd +"--"+ data);

        switch(cmd){
            case "EBE415": // Firmware Ver 0A
                acoholCmdProcEBE415(value);
                break;

            case "EAE511": // 加熱待つ時間
                acoholCmdProcEAE511(value);
                break;

            case "EAE51D": // 準備期間、気圧検知
                acoholCmdProcEAE51D(value);
                break;

            case "EAE510": // アルコール検測値
                acoholCmdProcEAE510(value);
                break;

            case "EAE512": // 最終の検測値
                acoholCmdProcEAE512(value);
                break;

            case "EAE519": // 加熱完了、検測開始
                acoholCmdProcEAE519(value);
                break;

            case "EAE51B": // 検測開始した後、気体が入らない場合に警告
                acoholCmdProcEAE51B(value);
                break;


            case "EAE516": // 設備使用した次数
                acoholCmdProcEAE516(value);
                break;


            case "EAE513": // 準備期間、アルコール検知の場合の警告
                acoholCmdProcEAE513(value);
                break;


            case "EAE517": // 加熱開始の前、電圧表示
                acoholCmdProcEAE517(value);
                break;


            case "EAE51E": // 警告提示
                acoholCmdProcEAE51E(value);
                break;

            case "EAE51A": // 検測終了
                acoholCmdProcEAE51A(value);
                break;

            case "EAE518": // 判断標準の範囲
                acoholCmdProcEAE518(value);
                break;

            case "EAE51F": // POWER OFF
                acoholCmdProcEAE51F(value);
                break;

            default:
                break;
        }

    }


    // Firmware Ver 0A
    private void acoholCmdProcEBE415(byte[] value) {

        String hexStr = HexUtil.formatHexString(value);

        if(value.length < 10){

            Log.e(TAG, "acoholCmdProcEBE415 error ="+hexStr);
            return;
        }

        String data = hexStr.substring(6, hexStr.length() -2);

        sendMassage(MSG_COMMAND_MSG_VERSION,data);

    }


    // 加熱待つ時間
    private void acoholCmdProcEAE511(byte[] value) {

        String waiteTime = null;

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE511 error ="+HexUtil.formatHexString(value));
            return;
        }

        int cnt = value[3]&0x00FF - 0x80;

        Log.d(TAG, "acoholCmdProcEAE511 cnt="+cnt);

              /*
        ・アプリ側で表示必要
        ・アルコール準備の時間
        ・取得範囲00～54
          例：
          Count:00～10 →  5秒（待ち）
          Count:10～20 →  4秒（待ち）
          Count:20～30 →  3秒（待ち）
          Count:30～40 →  2秒（待ち）
          Count:40～54 →  1秒（待ち）
         */

        if(cnt < 10){
            waiteTime = "5";
        }else if (cnt < 20) {
            waiteTime = "4";
        }
        else if (cnt < 30) {
            waiteTime = "3";
        }
        else if (cnt < 40) {
            waiteTime = "2";
        }
        else{
            waiteTime = "1";
        }

        sendMassage(MSG_COMMAND_TEST_WAITE,waiteTime);

    }



    // 準備期間、気圧検知
    private void acoholCmdProcEAE51D(byte[] value) {

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE51D error ="+HexUtil.formatHexString(value));
            return;
        }

        int cnt = value[3] & 0x00FF - 0x80;
        int alcohol_Data = value[4] & 0x00FF;
        int p_Data = value[5] & 0x00FF;

        Log.d(TAG, "acoholCmdProcEAE51D cnt=" + cnt + " Alcohol_Data:" + alcohol_Data + " P_Data:" + p_Data);

        /*・アプリ側で表示必要
         加熱中の気圧検測値（P_Data）は0以外の時、警告必要
         */
        if (p_Data > 0){
            sendMassage(MSG_COMMAND_WARNNING_START_TEST_PRESS,p_Data + "");
        }

    }


    // アルコール検測値
    private void acoholCmdProcEAE510(byte[] value) {

        if(value.length < 20){

            Log.e(TAG, "acoholCmdProcEAE510 error ="+HexUtil.formatHexString(value));
            return;
        }

        /*
        BTモジュールは"0x0A"のデータを送信できない、
        数値は"0x80"から送信
        例：0x80　→1、0x81　→2
         */
        int cnt1 = value[3] & 0x00FF - 0x80;
        int cnt2 = value[11] & 0x00FF - 0x80;

        /*
        「0x8A」(実の値は0x0A（dec :10))の場合
         */
        int alcohol_Data1 = value[4] & 0x007F;
        int alcohol_Data2 = value[12] & 0x007F;

        int p_Data_1 = value[5] & 0x007F;
        int p_Data_2 = value[13] & 0x007F;

        Log.d(TAG, "acoholCmdProcEAE510 cnt1=" + cnt1 + " alcohol_Data1:" + alcohol_Data1 + " p_Data_1:" + p_Data_1);
        Log.d(TAG, "acoholCmdProcEAE510 cnt2=" + cnt2 + " alcohol_Data2:" + alcohol_Data2 + " p_Data_2:" + p_Data_2);


        //気圧はp_Data <＝ 10  -> 0になる
        int p_Data = p_Data_1 > p_Data_1?p_Data_2:p_Data_1;
        if(p_Data <= 10){
            sendMassage(MSG_COMMAND_WARNNING_NO_BREATH,null);
        }else{
            sendMassage(MSG_COMMAND_MSG_TEST,null);
        }

        /*・アプリ側で表示必要
        アルコールは　alcohol_Data　<＝ 5  -> 0　になる
        */
        int alcohol = alcohol_Data1 > alcohol_Data2?alcohol_Data1:alcohol_Data2;
        alcohol = alcohol <= 5 ? 0:alcohol;
        String alcoholStr = String.format("%.2f", alcohol/100.0).toString();

        sendMassage(MSG_COMMAND_VALUE_TEST_AL, alcoholStr);

    }

    // 最終のアルコール検測のMAX値
    private void acoholCmdProcEAE512(byte[] value) {

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE512 error ="+HexUtil.formatHexString(value));
            return;
        }

        int cnt = value[3] & 0x00FF - 0x80;
        int alcohol = value[4] & 0x007F;

        Log.d(TAG, "acoholCmdProcEAE512 cnt=" + cnt + " alcohol:" + alcohol );

        /*・アプリ側で表示必要
        アルコールは　alcohol_Data　<＝ 5  -> 0　になる
        */
        alcohol = alcohol <= 5 ? 0:alcohol;
        String alcoholStr = String.format("%.2f", alcohol/100.0).toString();

        sendMassage(MSG_COMMAND_VALUE_TEST_AL, alcoholStr);

        // 15以下は青   15~25は黄色  25以上は赤
        Log.d(TAG, "mAltTestSTLow=" + mAltTestSTLow + " mAltTestSTHigh=" + mAltTestSTHigh );

        String flag = null;
        if(alcohol < mAltTestSTLow){
            flag = "G";
        } else if(alcohol <= mAltTestSTHigh){
            flag = "Y";
        }else{
            flag = "R";
        }

        sendMassage(MSG_COMMAND_VALUE_TEST_END,flag);

    }



    // 加熱完了、検測開始
    private void acoholCmdProcEAE519(byte[] value) {

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE519 error ="+HexUtil.formatHexString(value));
            return;
        }

        sendMassage(MSG_COMMAND_VALUE_TEST_START, null);
    }


    // 検測開始した後、異常終了の警告
    private void acoholCmdProcEAE51B(byte[] value) {

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE51B error ="+HexUtil.formatHexString(value));
            return;
        }

        sendMassage(MSG_COMMAND_WARNNING_TEST_END, null);
        mHowWarn = false;
    }


    // 設備使用した次数
    private void acoholCmdProcEAE516(byte[] value) {

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE516 error ="+HexUtil.formatHexString(value));
            return;
        }

        int cntH = value[3] & 0x007F;
        cntH = cntH<<8;

        int cntL = value[4] & 0x00FF;

        int cnt = cntH + cntL;

        Log.d(TAG, "acoholCmdProcEAE516 value[3] & 0x007F <<8 ="+cntH);
        Log.d(TAG, "acoholCmdProcEAE516 value[4] ="+cntL);

        Log.d(TAG, "acoholCmdProcEAE516 cnt ="+cnt);

        sendMassage(MSG_COMMAND_MSG_TEST_COUNT, cnt+"");
    }


    // 準備期間、アルコール検知の場合の警告
    private void acoholCmdProcEAE513(byte[] value) {

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE513 error ="+HexUtil.formatHexString(value));
            return;
        }

        int cnt = value[3] & 0x00FF;
        cnt = cnt - 0x80;

        sendMassage(MSG_COMMAND_WARNNING_TEST_START_AHL, cnt+"");

    }


    //　加熱開始の前、電圧の表示
    private void acoholCmdProcEAE517(byte[] value) {

        int msgType = MSG_COMMAND_VOLTAGE_OK;

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE517 error ="+HexUtil.formatHexString(value));
            return;
        }

        int flag = value[3] & 0x00FF;
        int voltage = value[4] & 0x00FF;

        Log.d(TAG, "acoholCmdProcEAE517 flag ="+flag +" voltage="+voltage);
        //Flag_for_Voltage : 0 正常 , 1:Voltage_Low , 2:Voltage_High
        //1.0V~1.1V
//        if(voltage <= 110){
//            msgType = MSG_COMMAND_VOLTAGE_LOW;
//        }

        if(flag == 1){
            msgType = MSG_COMMAND_VOLTAGE_LOW;
        }

        String voltageStr = String.format("%.2f", voltage/100.0).toString();

        sendMassage(msgType, voltageStr);

        if(flag == 1) {
            mHowWarn = true;
        }else {
            mHowWarn = false;
        }
    }


    //　警告提示
    private void acoholCmdProcEAE51E(byte[] value) {

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE51E error ="+HexUtil.formatHexString(value));
            return;
        }

        int flag = value[3] & 0x00FF;

        Log.d(TAG, "acoholCmdProcEAE51E flag ="+flag);

        /*
        ※0X10　→　吹き方式不正
        ※0X11　→　設備は2000回で使用
        ※0X12　→　IC読み込み不正
        ※0X13～1F　→　未使用
         */
        if(flag == 0X10){
            sendMassage(MSG_COMMAND_WARNNING_BLOWING, null);
        }
        else if(flag == 0X11){
            sendMassage(MSG_COMMAND_WARNNING_COUNT_OVER, null);
        }
        else if(flag == 0X12){
            sendMassage(MSG_COMMAND_WARNNING_IC_READ, null);
        }else{}

    }


    //　検測終了コマンド
    private void acoholCmdProcEAE51A(byte[] value) {

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE51A error ="+HexUtil.formatHexString(value));
            return;
        }

        sendMassage(MSG_COMMAND_TEST_END, null);
        mHowWarn = false;
    }


    //　判断標準範囲のコマンド
    private void acoholCmdProcEAE518(byte[] value) {

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE518 error ="+HexUtil.formatHexString(value));
            return;
        }

        mAltTestSTLow = value[3] == 0x8A ? 0x0A:value[3] & 0x00FF;
        mAltTestSTHigh = value[4] == 0x8A ? 0x0A:value[4] & 0x00FF;

        Log.d(TAG, "acoholCmdProcEAE518 low="+mAltTestSTLow +" High="+mAltTestSTHigh);

        //sendMassage(MSG_COMMAND_TEST_STANDARD, null);
        mHowWarn = false;
    }


    //　POWER OFFコマンド
    private void acoholCmdProcEAE51F(byte[] value) {

        if(value.length < 11){

            Log.e(TAG, "acoholCmdProcEAE51F error ="+HexUtil.formatHexString(value));
            return;
        }

        sendMassage(MSG_COMMAND_POWER_OFF, null);
        mHowWarn = false;

    }


    public void sendMassage(int type, String msg) {

        if(mHowWarn){
            Log.e(TAG, "Low warnning type="+type);
            return;
        }

        if(msg == null) {
            mHandlerUtil.sendHandler(type);
        }else{
            //Log.d(TAG, "AcoholCmdProc");
            mHandlerUtil.sendHandler(type, msg);
        }
    }

}
