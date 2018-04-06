package co.yaw.tpw.smartinspection;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.yaw.tpw.smartinspection.R;
import java.util.ArrayList;
import java.util.List;

import co.yaw.tpw.smartinspection.bltUtil.BltDeviceUtil;
import co.yaw.tpw.smartinspection.bltUtil.ConstUtil;
import co.yaw.tpw.smartinspection.cmdVital.EcgConnect;
import co.yaw.tpw.smartinspection.cmdVital.EcgProcess;
import co.yaw.tpw.smartinspection.cmdVital.VitalHandlerMsg;

import static android.util.Log.d;

public class VitalMeasureActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final static String TAG = VitalMeasureActivity.class.getSimpleName();

    final Context context = this;

    private Button nextBtn = null;
    private Spinner vitalSensorSpinner = null;

    private BltDeviceUtil mBltDeviceUtil = null;
    private Button mBackBtn = null;
    private String mForward = null;

    private VitalHandlerMsg mVitalHandlerMsg = null;
    private EcgConnect mEcgConnect = null;
    private EcgProcess mEcgProcess = null;

    private TextView mTestmsg = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_measure);

        mTestmsg = findViewById(R.id.test_msg);
        mBackBtn = findViewById(R.id.menu_button);

        final Bundle b = getIntent().getExtras();
        if(b != null) {
            mForward = b.getString(ConstUtil.FORWARD_KEY);

            if(mForward.equals(ConstUtil.CALL_FORWARD_BEFORE)) {
                mBackBtn.setText(R.string.check_crew_back_before);
            } else {
                mBackBtn.setText(R.string.check_crew_back_after);
            }
        }

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CallMenuActivity.class);
                Bundle b = new Bundle();
                b.putString(ConstUtil.FORWARD_KEY, mForward);
                b.putString(ConstUtil.HEART_CHECKED_KEY, mVitalHandlerMsg.isChecked()+"");

                intent.putExtras(b);
                startActivity(intent);
            }
        });

        setSpinnerAdapter();

        // bule tooth初期化
        initBltDeviceInfo();

        initEcgInfo();

        checkPermission(BltDeviceUtil.BLT_PRM_SCAN_NO);

    }


    private void setSpinnerAdapter() {

        vitalSensorSpinner = findViewById(R.id.vital_sensor_spinner);
        vitalSensorSpinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add(getResources().getString(R.string.vital_sensor_spinner_label));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vitalSensorSpinner.setAdapter(dataAdapter);

        vitalSensorSpinner.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                Log.d(TAG, "setOnTouchListener onTouch call action="+action);

                if(action != MotionEvent.ACTION_UP){
                    return false;
                }

                // 権限チェック
                boolean ret = checkPermission(BltDeviceUtil.BLT_PRM_SCAN_START);
                if(ret){

                    if(!mEcgConnect.getConnectStatus()){
                        // buletooth検索開始
                        mBltDeviceUtil.scanLeDevice(true);
                    }
                }

                return false;
            }
        });

        // バイタルセンサー一覧表示の初期化
        mVitalHandlerMsg = new VitalHandlerMsg(this);
        mVitalHandlerMsg.initDeviceAdapter(categories, dataAdapter);

    }


    private void connectTgStream(String select) {

        //権限チェック
        if(!checkPermission(BltDeviceUtil.BLT_PRM_SCAN_START)){

            Log.d(TAG, "checkPermission is false");
            return;
        }

        if(mEcgConnect.getConnectStatus()){
            Log.d(TAG, "getConnectStatus is ture");
            return;
        }

        Log.d(TAG, "mBltDeviceUtil.connectDevice="+select);

        mEcgProcess.initView();

        mTestmsg.setText(getString(R.string.vital_test_connect));

        // scan stop
        mBltDeviceUtil.scanLeDevice(false);

        BluetoothDevice device = mBltDeviceUtil.getblueDevice(select);
        if(device == null) {
            Log.d(TAG, "device is null");
            return;
        }

        Log.d(TAG, "getBondState ="+device.getBondState());

        mEcgConnect.stopTgStreamReader();
        mEcgProcess.initNskECG();
        mEcgConnect.connectTgStream(device);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemSelected position="+position);
        String select = null;

        if(position > 0){
            select = parent.getItemAtPosition(position).toString();

            Log.d(TAG, "onItemSelected name="+select);

            connectTgStream(select);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG, "onNothingSelected");
    }



    @Override
    protected void onPause() {

        super.onPause();

        mBltDeviceUtil.scanLeDevice(false);

        mEcgConnect.stopTgStreamReader();

        mBltDeviceUtil.clearReceiver();
        mBltDeviceUtil.initBluetoothGatt();

        //mStartBtn.setEnabled(true);
    }


    @Override
    public void onStop() {

        super.onStop();

        mEcgProcess.initView();

        //mTestmsg.setText(getString(R.string.vital_test_msg_blt_select));
    }


    @Override
    public void onResume() {
        super.onResume();

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

            case BltDeviceUtil.BLT_PRM_SCAN_NO:

                flag = false;

                break;

            default:
                break;
        }

        //全部の権限を持つ場合
        if(flag){

            if(!mEcgConnect.getConnectStatus()){
                // buletooth検索開始
                mBltDeviceUtil.scanLeDevice(true);
            }
        }
    }


    private void initBltDeviceInfo() {

        mBltDeviceUtil = new BltDeviceUtil(this, "MindWave");

        // Checks if Bluetooth is supported on the device.
        if (!mBltDeviceUtil.initBuleToothDevice(mVitalHandlerMsg, null)) {
            finish();
            return;
        }

//        // 権限チェック
//        boolean ret = checkPermission();
//        if(ret){
//            // buletooth検索開始
//            mBltDeviceUtil.scanLeDevice(true);
//        }

    }


    private void initEcgInfo() {

        mEcgConnect = new EcgConnect(this);
        mEcgConnect.initTgStream();
        mEcgConnect.setHandler(mVitalHandlerMsg);

        mEcgProcess = new EcgProcess(this);
        mEcgProcess.setEcgConnect(mEcgConnect);

        mVitalHandlerMsg.setEcgProcess(mEcgProcess);

    }


    // 権限のチェック
    private boolean checkPermission(int reqCode) {

        String[] reqArray = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        };

        boolean ret = mBltDeviceUtil.checkPerm(reqArray, reqCode);

        return ret;
    }


}
