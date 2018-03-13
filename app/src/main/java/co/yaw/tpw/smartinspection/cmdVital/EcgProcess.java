package co.yaw.tpw.smartinspection.cmdVital;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.TextView;

import com.neurosky.ecg_algo.NeuroSkyECG;
import com.neurosky.ecg_algo.NeuroSkyECGCallback;
import com.yaw.tpw.smartinspection.R;

import java.util.HashMap;
import java.util.Map;

import co.yaw.tpw.smartinspection.bltUtil.DateUtil;


public class EcgProcess {

    private final static String TAG = EcgProcess.class.getSimpleName();


    private static final String EVALUATION_LICENSE_KEY = "NaelySlOFSZb75fyRV+aTrDEEUlZ7lx4Q9lBs69sTb/CTJvuocULaV1kYt0JcFVpWFY3kYp0LKF2nA1WaxKAEqQG5aCC31ffnepTmCrWZBHboe7YYt36HLRvmS2qyrQz2ReRqeGKl+tgNl/AK9a2LZ09dB4BChrwoNSjuW3Q9N4=";
    private static int sampleRate = 512;

    public static final String HR = "heart_rate";
    public static final String RHT = "r_heart_rate";
    public static final String STRESS = "stress";
    public static final String MOOD = "mood";
    public static final String SQ = "SQ";
    public static final String TEST_TIME = "test_time";

    private Activity mActivity = null;

    private int mHeartRate = 0;
    private int mRheartRate = 0;
    private int mStress = 0;
    private int mMood = 0;
    private int mSQ = 0;

    private TextView mHeartRateView = null;
    private TextView mRheartRateView = null;
    private TextView mStressView = null;
    private TextView mMoodView = null;
    private TextView mSQView = null;

    private int mRr_count = 0;
    private final int mRr_threshold = 32;

    private NeuroSkyECG mNskECG = null;
    private EcgConnect mEcgConnect = null;



    public EcgProcess(Activity activity) {

        mActivity = activity;

        mHeartRateView = mActivity.findViewById(R.id.test_heart_rate);
        mRheartRateView = mActivity.findViewById(R.id.test_r_heart_rate);
        mStressView = mActivity.findViewById(R.id.test_stress);
        mMoodView = mActivity.findViewById(R.id.test_mood);
        mSQView = mActivity.findViewById(R.id.test_signal_quality);

        mNskECG = new NeuroSkyECG(mActivity, mEcgCallback);
        mNskECG.setupSDKProperty(EVALUATION_LICENSE_KEY, sampleRate,1);

    }


    public EcgConnect getEcgConnect() {
        return mEcgConnect;
    }


    public void setEcgConnect(EcgConnect connect) {
        mEcgConnect = connect;
    }


    public void initNskECG(){

        int outputInterval = 30;
        int outputPoint = 30;
        int stressFeedback = 0;


        mNskECG.resetECGAnalysis();

        mNskECG.resetHeartAge();
        mNskECG.resetStress();

        Map<String, String> map = mEcgConnect.getUserProfile();

        String user_name = map.get(EcgConnect.USER_NAME);
        boolean female = Boolean.valueOf(map.get(EcgConnect.USER_GENDER));
        int age = Integer.parseInt(map.get(EcgConnect.USER_AGE));
        int weight = Integer.parseInt(map.get(EcgConnect.USER_WEIGHT));
        int height = Integer.parseInt(map.get(EcgConnect.USER_HEIGHT));
        String path = "";

        mNskECG.setUserProfile(user_name, female, age, height, weight, path);
        mNskECG.setMoodOutputPoint(outputPoint);
        mNskECG.setHRVOutputInterval(outputInterval);
        mNskECG.setHeartAgeOutputPoint(outputPoint);
        mNskECG.setStressParameters(stressFeedback);
        mNskECG.setStressOutputPoint(outputPoint);

        mRr_count = 0;
    }



    private NeuroSkyECGCallback mEcgCallback = new NeuroSkyECGCallback() {
        @Override
        public void neuroskyECGDataReceived(int key, Object data) {

            mActivity.runOnUiThread(new EcgData(key, data) {

                @Override
                public void run() {

                    Log.d(TAG, "neuroskyECGDataReceived key=" + this.getKey() +" value="+(int)this.getData());

                    switch (this.getKey()) {
                        case NeuroSkyECG.MSG_ECG_SMOOTHED_WAVE:
                            //updateWaveView((Integer) data);
                            break;

                        case NeuroSkyECG.MSG_ECG_HEART_RATE:

                            mHeartRateView.setText(String.valueOf(this.getData()));
                            mHeartRate = (int)this.getData();

                            break;

                        case NeuroSkyECG.MSG_ECG_R2R_COUNT:
                            //tv_heartbeat.setText(String.valueOf(this.getData()));
                            break;

                        case NeuroSkyECG.MSG_ECG_R2R_INTERVAL:
                            mRr_count++;
                            //tv_rrinterval.setText(String.valueOf(this.getData()));

                            Log.d(TAG, "mHeartRate=" + mHeartRate +"  mRheartRate="+mRheartRate +" mRr_count="+mRr_count);

                            if(mRr_count >= mRr_threshold){

                                if(Math.abs(mHeartRate - mRheartRate) <= 10){
                                    mEcgConnect.stopTgStreamReader();
                                }

                                mStressView.setText(String.valueOf(mNskECG.getStress()));
                                mStress = mNskECG.getStress();

                                //tv_heartage.setText(String.valueOf(mNskECG.getHeartAge()));
                                mMoodView.setText(String.valueOf(mNskECG.getMood()));
                                mMood = mNskECG.getMood();
                            }

                            break;

                        case NeuroSkyECG.MSG_ECG_OVERALL_SIGNAL_QUALITY:
                            //tv_osq.setText(String.valueOf(this.getData()));
                            break;

                        case NeuroSkyECG.MSG_ECG_HRV:
                            //tv_hrv.setText(String.valueOf(this.getData()));
                            break;

                        case NeuroSkyECG.MSG_ECG_RPEAK_DETECTED:
                            //if((Integer)this.getData() == 1) {
                            //    tv_rpeak.setText(String.valueOf(this.getData()));
                            //}

                            break;

                        case NeuroSkyECG.MSG_ECG_ROBUST_HEART_RATE:

                            mRheartRateView.setText(String.valueOf(this.getData()));
                            mRheartRate = (int)this.getData();

                            break;

                        case NeuroSkyECG.MSG_ECG_SIGNAL_QUALITY:
                            mSQView.setText(String.valueOf(this.getData()));
                            mSQ = (int)this.getData();

                            break;

                    }
                }

            });
        }


        @Override
        public void ecgException(int exceptionCode) {
            mActivity.runOnUiThread(new EcgExceptionMessages(exceptionCode));
        }
    };



    public void requestECGAnalysis(int data, int poorSignal) {
        mNskECG.requestECGAnalysis(data, poorSignal);
    }



    // VIEW 初期化
    public void initView() {

        TextView heartRate = mActivity.findViewById(R.id.test_heart_rate);
        TextView bheartRate = mActivity.findViewById(R.id.test_r_heart_rate);
        TextView stressVal = mActivity.findViewById(R.id.test_stress);
        TextView moodVal = mActivity.findViewById(R.id.test_mood);
        TextView signalQuality = mActivity.findViewById(R.id.test_signal_quality);

        String defaultVal = mActivity.getResources().getString(R.string.vital_test_default_value);
        heartRate.setText(defaultVal);
        bheartRate.setText(defaultVal);
        stressVal.setText(defaultVal);
        moodVal.setText(defaultVal);
        signalQuality.setText(defaultVal);

        TextView msgText = mActivity.findViewById(R.id.test_msg);
        msgText.setText("");
    }



    public Map<String, String> getVitalTestValue() {
        Map<String, String> map = new HashMap<String, String>();

        map.put(HR, mHeartRate+"");
        map.put(RHT, mRheartRate+"");
        map.put(STRESS, mStress+"");
        map.put(MOOD, mMood+"");
        map.put(SQ, mSQ+"");

        String time = DateUtil.getCustomTime(DateUtil.YMDHMSS);
        map.put(TEST_TIME, time);

        Log.d(TAG, "getVitalTestValue mHeartRate=" + mHeartRate);
        Log.d(TAG, "getVitalTestValue mRheartRate=" + mRheartRate);
        Log.d(TAG, "getVitalTestValue mStress=" + mStress);
        Log.d(TAG, "getVitalTestValue mMood=" + mMood);
        Log.d(TAG, "getVitalTestValue mSQ=" + mSQ);
        Log.d(TAG, "getVitalTestValue time=" + time);

        return map;
    }



}

