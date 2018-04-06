package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;

import co.yaw.tpw.smartinspection.bltUtil.ConstUtil;
import co.yaw.tpw.smartinspection.http.pojo.CallRespPojo;
import co.yaw.tpw.smartinspection.http.util.RespCheckUtil;

import static android.util.Log.d;

public class CallMenuActivity extends AppCompatActivity {

    private final static String TAG = CallMenuActivity.class.getSimpleName();

    private final Context context = this;
    private Button mMenuBtn = null;

    private Button mAltCheckBtn = null;
    private Button mHealthSelfBtn = null;
    private Button mHeartCheckBtn = null;
    private Button mHeartSelfBtn = null;
    private Button mMethodFaceBtn = null;
    private Button mMethodPhoneBtn = null;

    private String mForward = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_menu);

        final Bundle b = getIntent().getExtras();

        //ボタンaction押下動作設定
        initButtonAction();

        // 画面表示内容初期化
        initView(b);

    }


    //ボタンaction押下動作設定
    private void initButtonAction(){

        // アルコール検測
        mAltCheckBtn = findViewById(R.id.alcohol_check_button);

        if(mAltCheckBtn != null){
            mAltCheckBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context , AlcoholMeasureActivity.class);
                    Bundle b = new Bundle();
                    b.putString(ConstUtil.FORWARD_KEY, mForward);
                    intent.putExtras(b);

                    startActivity(intent);
                }
            });
        }

        //健康状況自己申告
        mHealthSelfBtn = findViewById(R.id.health_check_self_button);
        if(mHealthSelfBtn != null){
            mHealthSelfBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context , HealthStatusRegActivity.class);
                    Bundle b = new Bundle();
                    b.putString(ConstUtil.FORWARD_KEY, mForward);
                    intent.putExtras(b);

                    startActivity(intent);
                }
            });
        }

        //バイタル検測
        mHeartCheckBtn = findViewById(R.id.heart_check_button);
        if(mHeartCheckBtn != null){
            mHeartCheckBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context , VitalMeasureActivity.class);
                    Bundle b = new Bundle();
                    b.putString(ConstUtil.FORWARD_KEY, mForward);
                    intent.putExtras(b);

                    startActivity(intent);
                }
            });
        }

        //バイタル自己申告
        mHeartSelfBtn = findViewById(R.id.heart_check_self_button);
        if(mHeartSelfBtn != null){
            mHeartSelfBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context , VitalSignMeasureActivity.class);
                    Bundle b = new Bundle();
                    b.putString(ConstUtil.FORWARD_KEY, mForward);
                    intent.putExtras(b);

                    startActivity(intent);
                }
            });
        }

        //点呼方式（対面）
        mMethodFaceBtn = findViewById(R.id.call_method_face_button);
        if(mMethodFaceBtn != null){
            mMethodFaceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "mMethodFaceBtn onClick");
                    //点呼方式のボタン文字列設定
                    setCallTypeBtn(1);
                }
            });
        }


        //点呼方式（電話）
        mMethodPhoneBtn = findViewById(R.id.call_method_phone_button);
        if(mMethodPhoneBtn != null){
            mMethodPhoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "mMethodPhoneBtn onClick");
                    //点呼方式のボタン文字列設定
                    setCallTypeBtn(2);
                }
            });
        }


        //メニュー画面に戻り
        mMenuBtn = findViewById(R.id.menu_button);
        mMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MenuActivity.class);
                startActivity(intent);

                finish();
            }
        });

    }



    //initView初期化
    private void initView(Bundle b){

        TextView crewInfoTv = findViewById(R.id.call_menu_title);

        mForward = b.getString(ConstUtil.FORWARD_KEY);

        if(mForward != null && mForward.equals(ConstUtil.CALL_FORWARD_AFTER)) {

            crewInfoTv.setText(R.string.call_menu_aft_title);

            View healthCheckLayout = findViewById(R.id.health_check_self_layout);
            View healthCheckSeparator = findViewById(R.id.heart_check_separator);

            View heartLayout = findViewById(R.id.heart_check_layout);
            View heartSeparator= findViewById(R.id.heart_check_self_separator);

            View heartSelfLayout = findViewById(R.id.heart_check_self_layout);
            View heartSelfSeparator= findViewById(R.id.call_method_separator);

            ViewGroup vg = (ViewGroup)(healthCheckLayout.getParent());
            vg.removeView(healthCheckLayout);
            vg.removeView(healthCheckSeparator);
            vg.removeView(heartLayout);
            vg.removeView(heartSeparator);
            vg.removeView(heartSelfLayout);
            vg.removeView(heartSelfSeparator);

        }else{
            crewInfoTv.setText(R.string.call_menu_bef_title);
        }


        String data = b.getString(ConstUtil.RESP_DATA_KEY);

        CallRespPojo pojo = RespCheckUtil.getCallRespPojo(data);
        if(pojo != null){

            //アルコール測定button文字列表示設定
            int altcheck = pojo.getAlcoholResult();
            int checkCount = pojo.getTestCount();
            setAltBtn(altcheck, checkCount);

            //健康状態（自己申告）button文字列表示設定
            int healthSelf = pojo.getHealthSelfResult();
            setHealthSelfBtn(healthSelf);

            //バイタル測定button文字列表示設定
            int vital = pojo.getVitalResult();
            setVitalBtn(vital);


            //バイタル測定(自己申告)button文字列表示設定
            int vitalSelf = pojo.getVitalSelfResult();
            setVitalSelfBtn(vitalSelf);

            //点呼方式button文字列表示設定
            int callType = pojo.getCallType();
            setCallTypeBtn(callType);


            // 承認済み、全部ボタン無効
            int admit = pojo.getAdmit();
            if(admit != 0){

                mAltCheckBtn.setEnabled(false);
                mHealthSelfBtn.setEnabled(false);
                mHeartCheckBtn.setEnabled(false);
                mHeartSelfBtn.setEnabled(false);
                mMethodFaceBtn.setEnabled(false);
                mMethodPhoneBtn.setEnabled(false);

                TextView msgText = findViewById(R.id.msg);
                msgText.setText(getString(R.string.call_menu_admit_end));
            }
        }
    }



    //アルコールボタン文字列設定
    private void setAltBtn(int altVal, int testCnt){

        switch(altVal){
            case 0: // 未測定
                mAltCheckBtn.setText(getString(R.string.call_menu_btn_check));
                break;
            case 1: // 異常なし（測定値は0）　
                mAltCheckBtn.setText(getString(R.string.call_menu_btn_check_ok));
                mAltCheckBtn.setTextColor(ContextCompat.getColor(this, android.R.color.background_light));
                mAltCheckBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

                break;
            case 2: // 要確認（測定値は0以上）　
                mAltCheckBtn.setText(getString(R.string.call_menu_btn_check_ng));
                mAltCheckBtn.setTextColor(ContextCompat.getColor(this, android.R.color.background_light));
                mAltCheckBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                break;
            default:
                break;
        }

        if(testCnt >= 3){
            mAltCheckBtn.setEnabled(false);
        }

    }




    //健康状態（自己申告）ボタン文字列設定
    private void setHealthSelfBtn(int healthSelf){

        switch(healthSelf){
            case 0: // 未測定
                mHealthSelfBtn.setText(getString(R.string.call_menu_btn_check));
                break;
            case 1: // 異常なし（測定値は0）　
                mHealthSelfBtn.setText(getString(R.string.call_menu_btn_check_ok));
                mHealthSelfBtn.setTextColor(ContextCompat.getColor(this, android.R.color.background_light));
                mHealthSelfBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

                break;
            case 2: // 要確認（測定値は0以上）　
                mHealthSelfBtn.setText(getString(R.string.call_menu_btn_check_ng));
                mHealthSelfBtn.setTextColor(ContextCompat.getColor(this, android.R.color.background_light));
                mHealthSelfBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                break;
            default:
                break;
        }

    }


    //バイタル測定ボタン文字列設定
    private void setVitalBtn(int vital){
        switch(vital) {
            case 0: // 未測定
                mHeartCheckBtn.setText(getString(R.string.call_menu_btn_check));
                break;
            case 1: // 実施済み
                mHeartCheckBtn.setText(getString(R.string.call_menu_btn_check_ok));
                mHeartCheckBtn.setTextColor(ContextCompat.getColor(this, android.R.color.background_light));
                mHeartCheckBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

                break;
            default:
                break;
        }
    }



    //バイタル（自己申告）ボタン文字列設定
    private void setVitalSelfBtn(int vitalSelf){
        switch(vitalSelf){
            case 0: // 未測定
                mHeartSelfBtn.setText(getString(R.string.call_menu_btn_check));
                break;
            case 1: // 実施済み
                mHeartSelfBtn.setText(getString(R.string.call_menu_btn_check_ok));
                mHeartSelfBtn.setTextColor(ContextCompat.getColor(this, android.R.color.background_light));
                mHeartSelfBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

                break;
            default:
                break;
        }
    }



    //点呼方式のボタン文字列設定
    private void setCallTypeBtn(int callType){

        switch(callType){
            case 1: // 点呼（対面）　
                mMethodFaceBtn.setTextColor(ContextCompat.getColor(this, android.R.color.background_light));
                mMethodFaceBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

                mMethodPhoneBtn.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                mMethodPhoneBtn.setBackgroundResource(android.R.drawable.btn_default);

                break;
            case 2: // 点呼（電話）
                mMethodPhoneBtn.setTextColor(ContextCompat.getColor(this, android.R.color.background_light));
                mMethodPhoneBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

                mMethodFaceBtn.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                mMethodFaceBtn.setBackgroundResource(android.R.drawable.btn_default);

                break;
            default:
                break;
        }
    }


    @Override
    public void onResume() {
        d(TAG, "onResume");
        super.onResume();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
