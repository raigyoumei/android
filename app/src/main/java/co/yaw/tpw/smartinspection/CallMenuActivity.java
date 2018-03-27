package co.yaw.tpw.smartinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;

import co.yaw.tpw.smartinspection.bltUtil.ConstUtil;

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

        if(b != null) {
            mForward = b.getString(ConstUtil.FORWARD_KEY);
        }

        // 画面表示内容初期化
        initView(mForward);

        //ボタンaction押下動作設定
        initButtonAction();

    }


    private void initView(String forward){

        TextView crewInfoTv = findViewById(R.id.call_menu_title);

        if(forward != null && forward.equals(ConstUtil.FORWARD_AFTER_VALUE)) {

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
            }
        });

    }


}
