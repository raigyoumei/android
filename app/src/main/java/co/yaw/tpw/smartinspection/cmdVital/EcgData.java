package co.yaw.tpw.smartinspection.cmdVital;


import android.util.Log;

import com.neurosky.ecg_algo.NeuroSkyECG;
import com.neurosky.ecg_algo.NeuroSkyECGCallback;


public class EcgData implements Runnable {

    private int key = 0;
    private Object data = null;

    public EcgData(int k, Object d) {

        key = k;
        data = d;
    }

    @Override
    public void run() {

    }


    public int getKey() {

        return this.key;
    }


    public Object getData() {

        return this.data;
    }

}

