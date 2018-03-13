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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.yaw.tpw.smartinspection.R;
import java.util.ArrayList;
import java.util.List;

import co.yaw.tpw.smartinspection.bltUtil.BltDeviceUtil;
import co.yaw.tpw.smartinspection.cmdVital.EcgConnect;
import co.yaw.tpw.smartinspection.cmdVital.EcgProcess;
import co.yaw.tpw.smartinspection.cmdVital.VitalHandlerMsg;

import static android.util.Log.d;

public class VitalMeasureActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final static String TAG = VitalMeasureActivity.class.getSimpleName();
    private final static int VITAL_PRM_REQ_CODE = 100;


    final Context context = this;

    private Button nextBtn = null;
    private Spinner vitalSensorSpinner = null;

    private BltDeviceUtil mBltDeviceUtil = null;
    private Button mStartBtn = null;
    private String mSelectDevice = null;

    private VitalHandlerMsg mVitalHandlerMsg = null;
    private EcgConnect mEcgConnect = null;
    private EcgProcess mEcgProcess = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vital_measure);

        nextBtn = findViewById(R.id.next_button);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VitalSignMeasureActivity.class);
                startActivity(intent);
            }
        });

        mStartBtn = findViewById(R.id.measure_button);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //権限チェック
                if(!checkPermission() || (mSelectDevice == null)){
                    return;
                }

                Log.d(TAG, "mBltDeviceUtil.connectDevice="+mSelectDevice);

                mEcgProcess.initView();

                // scan stop
                mBltDeviceUtil.scanLeDevice(false);

                if(!mVitalHandlerMsg.getConnectFailed()) {
                    mEcgConnect.stopTgStreamReader();
                }

                mVitalHandlerMsg.setConnectFailed(false);

                BluetoothDevice device = mBltDeviceUtil.getblueDevice(mSelectDevice);
                if(device == null) {
                    Log.d(TAG, "device is null");
                    return;
                }

                Log.d(TAG, "getBondState ="+device.getBondState());

                mEcgProcess.initNskECG();
                mEcgConnect.connectTgStream(device);

                // 開始ボタン無効
                Button startBtn = findViewById(R.id.measure_button);
                startBtn.setEnabled(false);
            }
        });

        setSpinnerAdapter();

        // bule tooth初期化
        initBltDeviceInfo();

        initEcgInfo();

    }

    private void setSpinnerAdapter() {
        vitalSensorSpinner = findViewById(R.id.vital_sensor_spinner);
        vitalSensorSpinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add(getResources().getString(R.string.vital_sensor_spinner_label));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vitalSensorSpinner.setAdapter(dataAdapter);

        // バイタルセンサー一覧表示の初期化
        mVitalHandlerMsg = new VitalHandlerMsg(this);
        mVitalHandlerMsg.initDeviceAdapter(categories, dataAdapter);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemSelected position="+position);
        mSelectDevice = null;

        if(position > 0){
            mSelectDevice = parent.getItemAtPosition(position).toString();
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
        mBltDeviceUtil.initBluetoothGatt();

        mEcgProcess.initView();

        mStartBtn.setEnabled(true);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!mBltDeviceUtil.checkIsEnabled()) {
            mBltDeviceUtil.enable();
        }
    }


    @Override
    protected void onDestroy() {

        mEcgConnect.closeStreamReader();
        mBltDeviceUtil.clearReceiver();

        super.onDestroy();

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        boolean flag = false;

        switch (requestCode) {
            case VITAL_PRM_REQ_CODE:

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
            // buletooth検索開始
            mBltDeviceUtil.scanLeDevice(true);
        }

    }


    private void initBltDeviceInfo() {

        mBltDeviceUtil = new BltDeviceUtil(this, "MindWave");

        // Checks if Bluetooth is supported on the device.
        if (!mBltDeviceUtil.initBuleToothDevice(mVitalHandlerMsg, null)) {
            finish();
            return;
        }

        // 権限チェック
        boolean ret = checkPermission();
        if(ret){
            // buletooth検索開始
            mBltDeviceUtil.scanLeDevice(true);
        }

    }


    private void initEcgInfo() {

        mVitalHandlerMsg = new VitalHandlerMsg(this);

        mEcgConnect = new EcgConnect(this);
        mEcgConnect.initTgStream();
        mEcgConnect.setHandler(mVitalHandlerMsg);

        mEcgProcess = new EcgProcess(this);
        mEcgProcess.setEcgConnect(mEcgConnect);

        mVitalHandlerMsg.setEcgProcess(mEcgProcess);

    }


    // 権限のチェック
    private boolean checkPermission() {

        String[] reqArray = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
        };

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
            ActivityCompat.requestPermissions(this, permissions, VITAL_PRM_REQ_CODE);
            return false;
        }

        return true;
    }


}
