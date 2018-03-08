package co.yaw.tpw.smartinspection.bltUtil;


import java.lang.reflect.Method;
import java.lang.reflect.Field;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;


public class BltBandUtil {

    private final static String TAG = BltBandUtil.class.getSimpleName();


    static public boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {

        Method createBondMethod = btClass.getMethod("createBond");
        boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue;
    }

    /**
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    static public boolean removeBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {

        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    static public boolean setPin(Class btClass, BluetoothDevice btDevice,
                                 String str) throws Exception {

        try
        {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin",
                    new Class[]
                            {byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
                    new Object[]
                            {str.getBytes()});
            Log.e(TAG, "setPin ret=" + returnValue);
        }
        catch (SecurityException e)
        {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }


    // cancel user input
    static public boolean cancelPairingUserInput(Class btClass,
                                                 BluetoothDevice device)
            throws Exception {

        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
        // cancelBondProcess()
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    // pair cancel
    static public boolean cancelBondProcess(Class btClass,
                                            BluetoothDevice device)
            throws Exception {

        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    /**
     *
     * @param clsShow
     */
    static public void printAllInform(Class clsShow) {

        try
        {
            // get mothed
            Method[] hideMethod = clsShow.getMethods();
            int i = 0;
            for (; i < hideMethod.length; i++)
            {
                Log.e(TAG, hideMethod[i].getName() + ";and the i is:"
                        + i);
            }

            //
            Field[] allFields = clsShow.getFields();
            for (i = 0; i < allFields.length; i++)
            {
                Log.e(TAG, allFields[i].getName());
            }
        }
        catch (SecurityException e)
        {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    static public boolean pair(String strAddr, String strPsw) {

        boolean result = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();
        if (!bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.enable();
        }

        if (!BluetoothAdapter.checkBluetoothAddress(strAddr))
        {
            Log.d(TAG, "devAdd un effient!");
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);
        if (device.getBondState() == BluetoothDevice.BOND_NONE)
        {
            try
            {
                Log.d(TAG, "NOT BOND_BONDED");
                BltBandUtil.setPin(device.getClass(), device, strPsw);
                BltBandUtil.createBond(device.getClass(), device);
                //BltBandUtil.cancelPairingUserInput(device.getClass(), device);

                result = true;
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                Log.d(TAG, "setPiN failed="+e.toString());
            }
        }
        else
        {
            Log.d(TAG, "HAS BOND_BONDED");

            try
            {
                //BltBandUtil.createBond(device.getClass(), device);
                //BltBandUtil.setPin(device.getClass(), device, strPsw);
                //BltBandUtil.createBond(device.getClass(), device);
                //ClsUtils.cancelPairingUserInput(device.getClass(), device);
                result = true;
            }
            catch (Exception e)
            {
                Log.d(TAG, "setPiN failed="+e.toString());
            }
        }

        return result;
    }



    static public boolean bltBond(BluetoothDevice device, String strPsw) {

        boolean result = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();

        if (!bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.enable();
        }

        if (device.getBondState() == BluetoothDevice.BOND_NONE)
        {
            try
            {
                Log.d(TAG, "bltBond call");

                //BltBandUtil.cancelBondProcess(device.getClass(), device);
                //BltBandUtil.setPin(device.getClass(), device, strPsw);
                BltBandUtil.createBond(device.getClass(), device);
                //BltBandUtil.cancelPairingUserInput(device.getClass(), device);

                result = true;
            }
            catch (Exception e)
            {
                Log.d(TAG, "bltBond failed="+e.toString());
            }
        }

        return result;
    }


}
