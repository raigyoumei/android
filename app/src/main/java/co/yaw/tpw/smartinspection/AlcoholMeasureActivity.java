package co.yaw.tpw.smartinspection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;

import java.util.ArrayList;
import java.util.List;

import co.yaw.tpw.smartinspection.bltUtil.BltDeviceUtil;
import co.yaw.tpw.smartinspection.camera.CameraCom;
import co.yaw.tpw.smartinspection.cmdAlcohol.AcoholCmd;
import co.yaw.tpw.smartinspection.cmdAlcohol.AcoholHandlerMsg;
import co.yaw.tpw.smartinspection.maskview.CameraMaskView;

import static android.util.Log.d;

public class AlcoholMeasureActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final static String TAG = AlcoholMeasureActivity.class.getSimpleName();

    final Context context = this;
    private Button backBtn;
    private Button nextBtn;
    private TextView crewInfoTv;
    private Class<?> forwardCls = null;
    private Spinner alcoholSensorSpinner;

    private AcoholHandlerMsg mAcoholHandlerMsg = null;
    private BltDeviceUtil mBltDeviceUtil = null;
    private AcoholCmd mAcoholCmd = null;
    //private Button mStartBtn = null;
    //private String mSelectDevice = null;
    private CameraCom mCameraCom = null;
    private TextView mtestMsg = null;

    private int mcrewBef = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol_measure);

        mtestMsg = findViewById(R.id.test_msg);

        final Bundle b = getIntent().getExtras();
        if(b != null) {
            String forward = b.getString("forward");
            if(forward.equals("beforeCrew")) {
                //forwardCls = VitalSignMeasureActivity.class;
                forwardCls = VitalMeasureActivity.class;

            } else {
                forwardCls = CallConfirmActivity.class;
            }
        }

        backBtn = findViewById(R.id.back_button);
        nextBtn = findViewById(R.id.next_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MenuActivity.class);
                startActivity(intent);
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, forwardCls);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        crewInfoTv = findViewById(R.id.crew_info);
        if (forwardCls == VitalMeasureActivity.class) {
            crewInfoTv.setText(R.string.alcohol_measure_crew_info_before);
            mcrewBef = 0;
        } else {
            crewInfoTv.setText(R.string.alcohol_measure_crew_info_after);
            mcrewBef = 1;
        }

        setSpinnerAdapter();
        setCameraMaskView();

        // bule tooth初期化
        initBltDeviceInfo();

        // 必要な権限チェック
        checkPermission(BltDeviceUtil.BLT_PRM_SCAN_NO);

    }

    private void setSpinnerAdapter() {
        alcoholSensorSpinner = findViewById(R.id.alcohol_sensor_spinner);
        alcoholSensorSpinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add(getResources().getString(R.string.alcohol_sensor_spinner_label));
//        categories.add("ES3256151SDF\n thidsdjslafdj");
//        categories.add("FS3256151SDF");
//        categories.add("GS3256151SDF");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alcoholSensorSpinner.setAdapter(dataAdapter);

        // アルコール一覧表示の初期化
        CameraMaskView camera = findViewById(R.id.camera);

        mCameraCom= new CameraCom(this, camera);
        mCameraCom.setStopFlag(true);

        mAcoholHandlerMsg = new AcoholHandlerMsg(this);
        mAcoholHandlerMsg.initDeviceAdapter(categories, dataAdapter);
        mAcoholHandlerMsg.setCameraView(mCameraCom);
        mAcoholHandlerMsg.setMcrewInfo(mcrewBef);

        alcoholSensorSpinner.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                Log.d(TAG, "setOnTouchListener onTouch call action="+action);

                if(action != MotionEvent.ACTION_UP){
                    return false;
                }

                boolean ret = checkPermission(BltDeviceUtil.BLT_PRM_SCAN_START);
                if(ret){

                    if(!mBltDeviceUtil.getConnectStatus()){

                        // buletooth検索開始
                        mBltDeviceUtil.scanLeDevice(true);
                    }
                }

                return false;
            }
        });

    }

    private void setCameraMaskView() {
        LayerDrawable layerDrawable = (LayerDrawable) getResources()
                .getDrawable(R.drawable.fg_round_image);
        ScaleDrawable scaleDraw = (ScaleDrawable) layerDrawable
                .findDrawableByLayerId(R.id.bg_camera_scale);
        scaleDraw.setLevel(1);
        View v = findViewById(R.id.cameraMessage);
        v.setBackground(layerDrawable);

        final ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                int height = scrollView.getHeight();
                int width = scrollView.getWidth();
                int wh = height < width ? height : width;
                if (wh > 0) {
                    FrameLayout frameLayout = findViewById(R.id.circle_frame);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            wh,
                            wh);
                    layoutParams.gravity = Gravity.CENTER;
                    frameLayout.setLayoutParams(layoutParams);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemSelected position="+position);
        String select = null;

        if(position > 0){

            select = parent.getItemAtPosition(position).toString();
            Log.d(TAG, "onItemSelected select="+select);

            //bltを接続する
            connectDevice(select);
        }
    }


    private void connectDevice(String select) {

        //必要な権限チェック
        if(!checkPermission(BltDeviceUtil.BLT_PRM_SCAN_START)){
            Log.d(TAG, "checkPermission is false");
            return;
        }

        // 既に接続中の場合、接続しない
        if(mBltDeviceUtil.getConnectStatus()){
            Log.d(TAG, "getConnectStatus is ture");
            return;
        }

        Log.d(TAG, "mBltDeviceUtil.connectDevice="+select);

        //画面表示を初期化する
        initView();

        // 接続中表示
        mtestMsg.setText(getString(R.string.alcohol_test_connect));

        // BLT接続を実行する
        mBltDeviceUtil.connectDevice(select);

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG, "onNothingSelected");
    }


    @Override
    protected void onPause() {

        super.onPause();

        mBltDeviceUtil.removeReceiver();

        mBltDeviceUtil.scanLeDevice(false);
        mBltDeviceUtil.initBluetoothGatt();
    }


    @Override
    protected void onStop() {
        super.onStop();

        initView();
    }



    @Override
    protected void onStart() {
        super.onStart();

        //mtestMsg.setText(getString(R.string.alcohol_test_blt_select));

    }


    @Override
    public void onResume() {
        super.onResume();

        // auto pair
        mBltDeviceUtil.addReceiver();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        boolean flag = false;

        switch (requestCode) {
            // blt 自動検索
            case BltDeviceUtil.BLT_PRM_SCAN_START:

                for(int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        flag = true;
                    }else{
                        flag = false;
                        break;
                    }
                }

                break;

            // blt 自動検索なし
            case BltDeviceUtil.BLT_PRM_SCAN_NO:

                flag = false;

                break;

            default:
                break;
        }

        //全部の権限を持つ場合
        if(flag){

            if(!mBltDeviceUtil.getConnectStatus()){
                Log.d(TAG, "getConnectStatus is false");

                //buletooth検索開始
                mBltDeviceUtil.scanLeDevice(true);
            }
        }

    }


    private void initBltDeviceInfo() {

        mBltDeviceUtil = new BltDeviceUtil(this, "PAB");

        // Checks if Bluetooth is supported on the device.
        mAcoholCmd = new AcoholCmd(mAcoholHandlerMsg);
        if (!mBltDeviceUtil.initBuleToothDevice(mAcoholHandlerMsg, mAcoholCmd)) {
            finish();
            return;
        }

        // 権限チェック
//        boolean ret = checkPermission();
//        if(ret){
//            // buletooth検索開始
//            mBltDeviceUtil.scanLeDevice(true);
//        }

    }


    // VIEW 初期化
    private void initView() {

        //TextView fwVersion = findViewById(R.id.fw_version);
        TextView usageCount = findViewById(R.id.usge_count);
        TextView testValue = findViewById(R.id.test_value);

        //fwVersion.setText("");
        usageCount.setText("");
        testValue.setText(getString(R.string.alcohol_test_default_value));

        mCameraCom.cameraStop();

    }


    // 権限のチェック
    private boolean checkPermission(int bltScan) {

        String[] reqArray = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA};

        mBltDeviceUtil.bltEnable();

        List list = new ArrayList();
        for(int i = 0; i < reqArray.length; i++){
            if (PermissionChecker.checkSelfPermission(this, reqArray[i]) != PermissionChecker.PERMISSION_GRANTED) {
                //権限がない場合はパーミッションを要求するメソッドを呼び出し
                // 許諾が無い場合は表示
                d(TAG, "permission=" +reqArray[i]);
                list.add(reqArray[i]);
            }
        }

        if(list.size() > 0){
            String[] permissions = (String[])list.toArray(new String[list.size()]);
            ActivityCompat.requestPermissions(this, permissions, bltScan);
            return false;
        }

        return true;
    }



    public BltDeviceUtil getBltDeviceUtil(){
        return mBltDeviceUtil;
    }

}
