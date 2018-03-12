package co.yaw.tpw.smartinspection.bltUtil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BltDeviceUtil {

    public static final int MSG_SCAN_START = 1001;
    public static final int MSG_DEVACE_FIND = 1002;
    public static final int MSG_DEVACE_CONNECT = 1003;
    public static final int MSG_DEVACE_DISCONNECT = 1004;
    public static final int MSG_DEVACE_CONNECT_NG = 1005;
    public static final int MSG_DEVACE_SCAN_END = 1006;

    public static final int MSG_DEVACE_PAIR_START = 1007;
    public static final int MSG_DEVACE_PAIR_END = 1008;

    private final static String TAG = BltBandUtil.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter = null;
    private Activity mActivity = null;
    private Handler mHandler = null;
    private Runnable mRunnable = null;

    private BluetoothGatt mBluetoothGatt = null;
    private BltComCmd mBaseSensorCmd = null;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    public static final String PAIR_PWD = "0000";
    private static final String SPIT_STR = "   ";

    private String mDeviceName = null;
    private boolean mScan = false;

    private HandlerUtil mHandlerUtil = null;
    private BltCntReceiverUtil mReceiverUtil = null;


    private List<BluetoothDevice> mBluetoothDeviceList = new ArrayList<BluetoothDevice>();

    public BltDeviceUtil(Activity Context) {
        Log.i(TAG, "BuleToothDevice");

        mActivity = Context;
    }


    public BltDeviceUtil(Activity Context, String devName) {
        Log.i(TAG, "BuleToothDevice");

        mActivity = Context;
        mDeviceName = devName;
    }


    public boolean checkIsEnabled() {
        return mBluetoothAdapter.isEnabled();
    }


    public void enable() {
        mBluetoothAdapter.enable();
    }


    public void clearReceiver() {

        if(mReceiverUtil != null) {
            mActivity.getBaseContext().unregisterReceiver(mReceiverUtil);
        }

        mReceiverUtil = null;
    }


    public BluetoothDevice getblueDevice(String name) {

        BluetoothDevice ret = null;

        for (BluetoothDevice device:mBluetoothDeviceList){

            String nameAdr = device.getName()+ SPIT_STR + device.getAddress();

            if (name.equals(nameAdr.trim())){

                ret = mBluetoothAdapter.getRemoteDevice(device.getAddress());
                break;
            }
        }

        return ret;
    }


    public boolean initBuleToothDevice(Handler handler, BltComCmd baseSensorCmd) {

        Log.i(TAG, "initBuleToothDevice");

        mHandler = handler;
        mBaseSensorCmd = baseSensorCmd;
        mHandlerUtil = new HandlerUtil(handler);

        if (!mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //Toast.makeText(this, "warning :bluetooth not supported", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            //Toast.makeText(this, "error : bluetooth not supported", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        // auto pair
        addReceiver();

        return true;

    }



    private void addReceiver(){

        mReceiverUtil = new BltCntReceiverUtil(mHandler, PAIR_PWD);

        IntentFilter intent = new IntentFilter();
        intent.setPriority(1000);

        intent.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mActivity.registerReceiver(mReceiverUtil, intent);

        Log.d(TAG, "mActivity.registerReceiver call");

    }



    public void removeReceiver(){

        Log.d(TAG, "mActivity.removeReceiver call");

        mActivity.unregisterReceiver(mReceiverUtil);
    }



    public void scanLeDevice(final boolean enable) {
        if (enable) {
            if (!mScan){

                mScan = true;
                mHandlerUtil.sendHandler(MSG_SCAN_START);

                mBluetoothDeviceList.clear();

                mBluetoothAdapter.startLeScan(mLeScanCallback);

                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mScan = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mHandlerUtil.sendHandler(MSG_DEVACE_SCAN_END);
                    }
                };

                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(mRunnable, SCAN_PERIOD);

            }

        } else {
            mScan = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);

            //mHandlerUtil.sendHandler(MSG_DEVACE_SCAN_END);

        }
    }


    public boolean connectDevice(String name) {

        if(name == null){
            return false;
        }

        initBluetoothGatt();

        for (BluetoothDevice device:mBluetoothDeviceList){

            String nameAdr = device.getName()+ SPIT_STR + device.getAddress();

            if (name.equals(nameAdr.trim())){

                device = mBluetoothAdapter.getRemoteDevice(device.getAddress());

                //boolean ret = BltBandUtil.pair(device.getAddress(), PAIR_PWD);
                //Log.d(TAG, "BuleToothBand.pair ret =" + ret);

                mScan = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mHandler.removeCallbacks(mRunnable);

                mBluetoothGatt = device.connectGatt(mActivity, false, mGattCallback);

                return true;
            }
        }

        return false;
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan( final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            Log.d(TAG, device.getName() + "--" + device.getAddress());

                            Map<String, String> map = new HashMap<String, String>();
                            map.put(device.getName(), device.getAddress());

                            String name = device.getName();

                            Log.d(TAG, "device name: " + name);
                            Log.d(TAG, "device ID: " + device.getAddress());

//                            if (name == null) {
//                                return;
//                            }

                            //mHandlerUtil.sendHandler(MSG_DEVACE_FIND, name);
                            boolean addFlg = false;

                            if(mDeviceName == null){
                                addFlg = true;
                            }

                            if((mDeviceName != null)
                                    && (name != null)
                                    && (name.startsWith(mDeviceName))) {
                                addFlg = true;
                            }

                            if(addFlg) {
                                String deviceName = name + SPIT_STR + device.getAddress();
                                deviceName = deviceName.trim();

                                if (getblueDevice(deviceName) != null) {
                                    return;
                                }

                                if (mBluetoothDeviceList.indexOf(device) == -1) {
                                    mBluetoothDeviceList.add(device);
                                    mHandlerUtil.sendHandler(MSG_DEVACE_FIND, deviceName);
                                }

                                //BltBandUtil.bltBond(device, PAIR_PWD);
                            }
                        }
                    });
                }
    };



    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override  //当连接上设备或者失去连接时会回调该函数
        public void onConnectionStateChange(BluetoothGatt gatt, int status,int newState) {

            super.onConnectionStateChange(gatt, status, newState);

            Log.d(TAG, "11111onConnectionStateChange: thread "
                    + Thread.currentThread() + status+"--->" + newState);

            if(gatt == null){
                Log.e(TAG, "error gatt is null");
            }

            if (status != BluetoothGatt.GATT_SUCCESS) {
                 // 当尝试连接失败的时候调用 disconnect 方法是不会引起这个方法回调的，所以这里
                //   直接回调就可以了。
                gatt.close();
                Log.e(TAG, "Cannot connect device with error status: " + status);

                mHandlerUtil.sendHandler(MSG_DEVACE_CONNECT_NG);

                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.d(TAG, "STATE_CONNECTED");

                gatt.discoverServices();
                mHandlerUtil.sendHandler(MSG_DEVACE_CONNECT);
                return;
            }

            if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                Log.d(TAG, "STATE_DISCONNECTED");

                gatt.close();
                mHandlerUtil.sendHandler(MSG_DEVACE_DISCONNECT);
                return;
            }

        }


        @Override  //当设备是否找到服务时，会回调该函数
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            super.onServicesDiscovered(gatt, status);

            Log.d(TAG, "11111onServicesDiscovered: " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {   //找到服务了
                //在这里可以对服务进行解析，寻找到你需要的服务

                List<BluetoothGattService> list =gatt.getServices();
                //List<BluetoothGattService> list = mBluetoothGatt.getServices();

                for (BluetoothGattService gattService : list) {

                    String uuid = gattService.getUuid().toString();

                    Log.d(TAG, "getUuid: " + uuid);
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                        uuid = gattCharacteristic.getUuid().toString();

                        final int charaProp = gattCharacteristic.getProperties();
                        Log.d(TAG, "gattCharacteristic getUuid: " + uuid +"  charaProp="+charaProp);

                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {

                            Log.d(TAG, "PROPERTY_NOTIFY " + charaProp);

                            boolean ret = gatt.setCharacteristicNotification(gattCharacteristic, true);
                            //boolean ret = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);

                            Log.d(TAG, "setCharacteristicNotification ret= " + ret);

                            for(BluetoothGattDescriptor dp:gattCharacteristic.getDescriptors()){

                                Log.d(TAG, "setCharacteristicNotification dp= " + dp.getUuid());

                                dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(dp);
                            }
                        }else if ((charaProp & (BluetoothGattCharacteristic.PROPERTY_READ|BluetoothGattCharacteristic.PROPERTY_WRITE)) != 0) {

                            Log.d(TAG, "PROPERTY_READ " + charaProp);

                            //gatt.readCharacteristic(gattCharacteristic);

                            //gatt.setCharacteristicNotification(gattCharacteristic, true);


                        }else if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {

                            Log.d(TAG, "PROPERTY_INDICATE " + charaProp);

                            //boolean ret = gatt.setCharacteristicNotification(gattCharacteristic, true);

                            for(BluetoothGattDescriptor dp:gattCharacteristic.getDescriptors()){
                                dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                                gatt.writeDescriptor(dp);
                            }

                        }


                    }
                }

            } else {
                Log.d(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override  //当读取设备时会回调该函数
        public void onCharacteristicRead(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            //Log.d(TAG, "11111onCharacteristicRead: " + status);
            //System.out.println("onCharacteristicRead");

            Log.d(TAG, "111111onCharacteristicRead = " + characteristic.getStringValue(0));

            if (status == BluetoothGatt.GATT_SUCCESS) {
                //读取到的数据存在characteristic当中，可以通过characteristic.getValue();函数取出。然后再进行解析操作。
                int charaProp = characteristic.getProperties();

                //Log.d(TAG, "111111onCharacteristicRead = " + characteristic.getStringValue(0));

                byte[] value = characteristic.getValue();
                String hexStr = HexUtil.formatHexString(value);

                Log.d(TAG, "111111onCharacteristicRead = " + hexStr);

                if(mBaseSensorCmd != null) {
                    mBaseSensorCmd.cmdProc(value);
                }

            }
        }


        @Override //当向设备Descriptor中写数据时，会回调该函数
        public void onDescriptorWrite( BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

            Log.d(TAG, "11111onDescriptorWriteonDescriptorWrite = " + status + ", descriptor =" + descriptor.getUuid().toString());

            //System.out.println("onDescriptorWriteonDescriptorWrite = " + status + ", descriptor =" + descriptor.getUuid().toString());
        }

        @Override //设备发出通知时会调用到该接口
        public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            byte[] value = characteristic.getValue();
            String hexStr = HexUtil.formatHexString(value);

            Log.d(TAG, "11111onCharacteristicChanged=" + hexStr);

            if (characteristic.getValue() != null) {
                //System.out.println(characteristic.getStringValue(0));

                if(mBaseSensorCmd != null) {
                    mBaseSensorCmd.cmdProc(value);
                }

            }
            //System.out.println("--------onCharacteristicChanged-----");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

            Log.d(TAG, "11111onReadRemoteRssi = " + rssi);

            //System.out.println("rssi = " + rssi);
        }

        @Override //当向Characteristic写数据时会回调该函数
        public void onCharacteristicWrite(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            Log.d(TAG, "11111onCharacteristicWrite = " + status);

            //System.out.println("--------write success----- status:" + status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                     int status) {

            super.onDescriptorRead(gatt, descriptor, status);

            Log.d(TAG, "11111onDescriptorRead = " + status);

        }

        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);

            Log.d(TAG, "11111onPhyUpdate = " + status);
        }

    };


    public void initBluetoothGatt() {
        if(mBluetoothGatt != null){
            mBluetoothGatt.disconnect();
            //mBluetoothGatt.close();
        }
        mBluetoothGatt = null;
    }

}
