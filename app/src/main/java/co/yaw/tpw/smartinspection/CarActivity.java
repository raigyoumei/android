package co.yaw.tpw.smartinspection;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import co.yaw.tpw.smartinspection.cmdObd.ObdHandlerMsg;
import co.yaw.tpw.smartinspection.cmdObd.OdbCmd;


public class CarActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final static String TAG = CarActivity.class.getSimpleName();

    final Context context = this;
    private Button nextBtn;
    private Spinner carNumberSpinner;

    private ObdHandlerMsg mObdHandlerMsg = null;
    private BltDeviceUtil mBltDeviceUtil = null;
    private OdbCmd mOdbCmd = null;
    private TextView mtestMsg = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        mtestMsg = findViewById(R.id.test_msg);
        nextBtn = findViewById(R.id.next_button);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VisualCheckActivity.class);
                startActivity(intent);
            }
        });

        setSpinnerAdapter();

        // bule tooth初期化
        initBltDeviceInfo();

        // 必要な権限チェック
        checkPermission(BltDeviceUtil.BLT_PRM_SCAN_NO);

    }

    private void setSpinnerAdapter() {
        carNumberSpinner = findViewById(R.id.car_number_spinner);
        carNumberSpinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add(getResources().getString(R.string.car_spinner_prompt));
//        categories.add("品川47　ぬ24-869");
//        categories.add("札幌500 れ92-52");
//        categories.add("仙台78  か36-57");
//        categories.add("三河60　あ88-90");
//        categories.add("大阪20　き78-20");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carNumberSpinner.setAdapter(dataAdapter);


        mObdHandlerMsg = new ObdHandlerMsg(this);
        mObdHandlerMsg.initDeviceAdapter(categories, dataAdapter);

        carNumberSpinner.setOnTouchListener(new View.OnTouchListener(){

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


    private void initBltDeviceInfo() {

        mBltDeviceUtil = new BltDeviceUtil(this, "nanoOne");

        // Checks if Bluetooth is supported on the device.
        mOdbCmd = new OdbCmd(mObdHandlerMsg);
        if (!mBltDeviceUtil.initBuleToothDevice(mObdHandlerMsg, mOdbCmd)) {
            finish();
            return;
        }

    }


    // 権限のチェック
    private boolean checkPermission(int reqCode) {

        String[] reqArray = new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN};

        boolean ret = mBltDeviceUtil.checkPerm(reqArray, reqCode);

        return ret;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {

            String item = parent.getItemAtPosition(position).toString();
            popup(item);

            Log.d(TAG, "onItemSelected select="+item);

            //bltを接続する
            //connectDevice(item);
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


        // 接続中表示
        mtestMsg.setText(getString(R.string.alcohol_test_connect));

        // BLT接続を実行する
        mBltDeviceUtil.connectDevice(select);

    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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


    public void popup(String carNumber) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.car_selector, null);
        TextView carSelectorContent = alertLayout.findViewById(R.id.car_selector_content);
        carSelectorContent.setText("車両番号：" + carNumber + "\nが選択されています。");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);

        final AlertDialog dialog = alert.create();
        dialog.show();

        TextView cancleBtn = alertLayout.findViewById(R.id.cancle_button);
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView okBtn = alertLayout.findViewById(R.id.ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @Override
    protected void onPause() {

        super.onPause();

        mBltDeviceUtil.removeReceiver();
        mBltDeviceUtil.scanLeDevice(false);

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

        mBltDeviceUtil.initBluetoothGatt();
    }



}
