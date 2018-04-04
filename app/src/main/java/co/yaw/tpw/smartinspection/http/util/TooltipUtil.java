package co.yaw.tpw.smartinspection.http.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by leixiaoming on 2018/04/04.
 */

public class TooltipUtil {

    private final static String TAG = TooltipUtil.class.getSimpleName();

    private final static int mTextSize = 16;

    private static Activity mActivity = null;
    private static Toast mToast = null;


    public static void showToast(Context context, String s) {

        Log.d(TAG, "showToast = " + s);

        TextView text = new TextView(context.getApplicationContext());
        //Toastに表示する文字
        text.setText(s);
        //フォントの種類
        text.setTypeface(Typeface.SANS_SERIF);
        //フォントの大きさ
        text.setTextSize(mTextSize);
        //フォントの色
        text.setTextColor(Color.RED);
        //文字の背景色(ARGB)
        text.setBackgroundColor(0x88dcdcdc);

        //Toastの表示
        if(mToast == null){
            mToast = new Toast(context.getApplicationContext());
            mToast.setView(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }else{
            mToast.setView(text);
        }
        mToast.show();
    }

}
