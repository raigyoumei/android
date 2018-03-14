package co.yaw.tpw.smartinspection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.wonderkiln.camerakit.CameraView;
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

    private final static int ALCOHOL_PRM_REQ_CODE = 100;

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
    private String mSelectDevice = null;
    private CameraCom mCameraCom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol_measure);

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
        } else {
            crewInfoTv.setText(R.string.alcohol_measure_crew_info_after);
        }


//        mStartBtn = findViewById(R.id.measure_button);
//        mStartBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //権限チェック
//                if(!checkPermission() || (mSelectDevice == null)){
//                    return;
//                }
//
//                Log.d(TAG, "mBltDeviceUtil.connectDevice="+mSelectDevice);
//
//                initView();
//
//                // bule tooth 接続
//                mBltDeviceUtil.connectDevice(mSelectDevice);
//
//                // 継続中表示
//                TextView testMsg = findViewById(R.id.test_msg);
//                testMsg.setText(getString(R.string.alcohol_test_connect));
//
//                // 開始ボタン無効
//                Button startBtn = findViewById(R.id.measure_button);
//                startBtn.setEnabled(false);
//            }
//        });

        setSpinnerAdapter();

        // bule tooth初期化
        initBltDeviceInfo();

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
        CameraMaskView camera = (CameraMaskView) findViewById(R.id.camera);

        mCameraCom= new CameraCom(this, camera);
        mCameraCom.setStopFlag(true);

        mAcoholHandlerMsg = new AcoholHandlerMsg(this);
        mAcoholHandlerMsg.initDeviceAdapter(categories, dataAdapter);
        mAcoholHandlerMsg.setCameraView(mCameraCom);

        alcoholSensorSpinner.setOnTouchListener(new AdapterView.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.d(TAG, "setOnTouchListener onTouch call");

                boolean ret = checkPermission();
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemSelected position="+position);

        if(position > 0){

            mSelectDevice = parent.getItemAtPosition(position).toString();

        }else{

            Log.d(TAG, "onItemSelected position is 0");
            return;
        }

        if(mBltDeviceUtil.getConnectStatus()){
            Log.d(TAG, "getConnectStatus is true");
            return;
        }

        //権限チェック
        if(!checkPermission()){

            Log.d(TAG, "checkPermission is false");
            return;
        }

        Log.d(TAG, "mBltDeviceUtil.connectDevice="+mSelectDevice);

        initView();

        // bule tooth 接続
        mBltDeviceUtil.connectDevice(mSelectDevice);

        // 継続中表示
        TextView testMsg = findViewById(R.id.test_msg);
        testMsg.setText(getString(R.string.alcohol_test_connect));

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG, "onNothingSelected");
    }


    @Override
    protected void onPause() {

        mCameraCom.cameraStop();

        super.onPause();
        mBltDeviceUtil.scanLeDevice(false);
        mBltDeviceUtil.initBluetoothGatt();
        //mStartTime = 0;

        initView();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBltDeviceUtil.removeReceiver();

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        boolean flag = false;

        switch (requestCode) {
            case ALCOHOL_PRM_REQ_CODE:

                for(int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        flag = true;
                    }else{
                        flag = false;
                        break;
                    }
                }
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

        //mBltDeviceUtil = new BltDeviceUtil(this, null);

        // Checks if Bluetooth is supported on the device.
        mAcoholCmd = new AcoholCmd(mAcoholHandlerMsg);
        if (!mBltDeviceUtil.initBuleToothDevice(mAcoholHandlerMsg, mAcoholCmd)) {
            //Toast.makeText(this, "error : bluetooth not supported", Toast.LENGTH_SHORT).show();
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
        TextView testMsg = findViewById(R.id.test_msg);
        TextView testValue = findViewById(R.id.test_value);

        //fwVersion.setText("");
        usageCount.setText("");
        testMsg.setText("");
        testValue.setText(getString(R.string.alcohol_test_default_value));

        mCameraCom.cameraStop();

    }


    // 権限のチェック
    private boolean checkPermission() {

        String[] reqArray = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA};

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
            ActivityCompat.requestPermissions(this, permissions, ALCOHOL_PRM_REQ_CODE);
            return false;
        }

        return true;
    }



    public BltDeviceUtil getBltDeviceUtil(){
        return mBltDeviceUtil;
    }

}
